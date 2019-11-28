package org.springframework.beans.factory.xml;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;

/**
 * 自定义XSD解析器
 *
 * @author gaoteng
 */
public class TestEntityResolver implements EntityResolver {

	private static final Log log = LogFactory.getLog(TestEntityResolver.class);

	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {

		log.info("publicId: " + publicId);
		log.info("systemId: " + systemId);

		if (systemId.equals("http://www.springframework.org/schema/beans/spring-beans.xsd")) {
			// 从本地磁盘获取XSD验证文件
			FileSystemResourceLoader resourceLoader = new FileSystemResourceLoader();
			Resource resource = resourceLoader.getResource("E:\\spring-beans.dtd");
			try {
				InputSource source = new InputSource(resource.getInputStream());
				source.setPublicId(publicId);
				source.setSystemId(systemId);
				if (log.isDebugEnabled()) {
					log.trace("Found XML schema [" + systemId + "] in classpath: " + resource.getDescription());
				}
				return source;
			} catch (Exception ex) {
				if (log.isDebugEnabled()) {
					log.debug("Could not find XML schema [" + systemId + "]: " + resource, ex);
				}
			}
		}

		return null;
	}
}

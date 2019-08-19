package org.springframework.beans.factory.xml;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.parsing.*;
import org.springframework.beans.factory.support.AbstractBeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.Constants;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.io.DescriptiveResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.xml.SimpleSaxErrorHandler;
import org.springframework.util.xml.XmlValidationModeDetector;
import org.w3c.dom.Document;
import org.xml.sax.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * 用于XML bean定义的Bean定义读取器
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Chris Beams
 * @since 26.11.2003
 */
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {

	/**
	 * 禁用验证模型
	 */
	public static final int VALIDATION_NONE = XmlValidationModeDetector.VALIDATION_NONE;

	/**
	 * 自动检测验证模型
	 */
	public static final int VALIDATION_AUTO = XmlValidationModeDetector.VALIDATION_AUTO;

	/**
	 * DTD 验证模型
	 */
	public static final int VALIDATION_DTD = XmlValidationModeDetector.VALIDATION_DTD;

	/**
	 * XSD 验证模型
	 */
	public static final int VALIDATION_XSD = XmlValidationModeDetector.VALIDATION_XSD;

	/**
	 * 默认为自动验证模型
	 */
	private int validationMode = VALIDATION_AUTO;

	/**
	 * Constants instance for this class.
	 */
	private static final Constants constants = new Constants(XmlBeanDefinitionReader.class);


	private boolean namespaceAware = false;

	private Class<? extends BeanDefinitionDocumentReader> documentReaderClass = DefaultBeanDefinitionDocumentReader.class;

	private ProblemReporter problemReporter = new FailFastProblemReporter();

	private ReaderEventListener eventListener = new EmptyReaderEventListener();

	private SourceExtractor sourceExtractor = new NullSourceExtractor();

	@Nullable
	private NamespaceHandlerResolver namespaceHandlerResolver;

	/**
	 * Document 加载器
	 */
	private DocumentLoader documentLoader = new DefaultDocumentLoader();

	@Nullable
	private EntityResolver entityResolver;

	private ErrorHandler errorHandler = new SimpleSaxErrorHandler(logger);

	/**
	 * XML验证模型检测器,用于检测当前XML应该使用哪种验证模型
	 */
	private final XmlValidationModeDetector validationModeDetector = new XmlValidationModeDetector();

	/**
	 * 当前线程，正在加载的 EncodedResource 集合。
	 */
	private final ThreadLocal<Set<EncodedResource>> resourcesCurrentlyBeingLoaded = new NamedThreadLocal<>("XML bean definition resources currently being loaded");


	/**
	 * Create new XmlBeanDefinitionReader for the given bean factory.
	 *
	 * @param registry the BeanFactory to load bean definitions into,
	 *                 in the form of a BeanDefinitionRegistry
	 */
	public XmlBeanDefinitionReader(BeanDefinitionRegistry registry) {
		super(registry);
	}


	/**
	 * 设置校验类型
	 *
	 * @param validating
	 */
	public void setValidating(boolean validating) {
		this.validationMode = (validating ? VALIDATION_AUTO : VALIDATION_NONE);
		this.namespaceAware = !validating;
	}

	/**
	 * Set the validation mode to use by name. Defaults to {@link #VALIDATION_AUTO}.
	 *
	 * @see #setValidationMode
	 */
	public void setValidationModeName(String validationModeName) {
		setValidationMode(constants.asNumber(validationModeName).intValue());
	}

	/**
	 * 设置验证模型
	 *
	 * @param validationMode
	 */
	public void setValidationMode(int validationMode) {
		this.validationMode = validationMode;
	}

	/**
	 * 获取验证模型
	 */
	public int getValidationMode() {
		return this.validationMode;
	}

	/**
	 * Set whether or not the XML parser should be XML namespace aware.
	 * Default is "false".
	 * <p>This is typically not needed when schema validation is active.
	 * However, without validation, this has to be switched to "true"
	 * in order to properly process schema namespaces.
	 */
	public void setNamespaceAware(boolean namespaceAware) {
		this.namespaceAware = namespaceAware;
	}

	/**
	 * Return whether or not the XML parser should be XML namespace aware.
	 */
	public boolean isNamespaceAware() {
		return this.namespaceAware;
	}

	/**
	 * Specify which {@link org.springframework.beans.factory.parsing.ProblemReporter} to use.
	 * <p>The default implementation is {@link org.springframework.beans.factory.parsing.FailFastProblemReporter}
	 * which exhibits fail fast behaviour. External tools can provide an alternative implementation
	 * that collates errors and warnings for display in the tool UI.
	 */
	public void setProblemReporter(@Nullable ProblemReporter problemReporter) {
		this.problemReporter = (problemReporter != null ? problemReporter : new FailFastProblemReporter());
	}

	/**
	 * Specify which {@link ReaderEventListener} to use.
	 * <p>The default implementation is EmptyReaderEventListener which discards every event notification.
	 * External tools can provide an alternative implementation to monitor the components being
	 * registered in the BeanFactory.
	 */
	public void setEventListener(@Nullable ReaderEventListener eventListener) {
		this.eventListener = (eventListener != null ? eventListener : new EmptyReaderEventListener());
	}

	/**
	 * Specify the {@link SourceExtractor} to use.
	 * <p>The default implementation is {@link NullSourceExtractor} which simply returns {@code null}
	 * as the source object. This means that - during normal runtime execution -
	 * no additional source metadata is attached to the bean configuration metadata.
	 */
	public void setSourceExtractor(@Nullable SourceExtractor sourceExtractor) {
		this.sourceExtractor = (sourceExtractor != null ? sourceExtractor : new NullSourceExtractor());
	}

	/**
	 * Specify the {@link NamespaceHandlerResolver} to use.
	 * <p>If none is specified, a default instance will be created through
	 * {@link #createDefaultNamespaceHandlerResolver()}.
	 */
	public void setNamespaceHandlerResolver(@Nullable NamespaceHandlerResolver namespaceHandlerResolver) {
		this.namespaceHandlerResolver = namespaceHandlerResolver;
	}

	/**
	 * Specify the {@link DocumentLoader} to use.
	 * <p>The default implementation is {@link DefaultDocumentLoader}
	 * which loads {@link Document} instances using JAXP.
	 */
	public void setDocumentLoader(@Nullable DocumentLoader documentLoader) {
		this.documentLoader = (documentLoader != null ? documentLoader : new DefaultDocumentLoader());
	}

	/**
	 * Set a SAX entity resolver to be used for parsing.
	 * <p>By default, {@link ResourceEntityResolver} will be used. Can be overridden
	 * for custom entity resolution, for example relative to some specific base path.
	 */
	public void setEntityResolver(@Nullable EntityResolver entityResolver) {
		this.entityResolver = entityResolver;
	}

	/**
	 * Return the EntityResolver to use, building a default resolver
	 * if none specified.
	 */
	protected EntityResolver getEntityResolver() {
		if (this.entityResolver == null) {
			// Determine default EntityResolver to use.
			ResourceLoader resourceLoader = getResourceLoader();
			if (resourceLoader != null) {
				this.entityResolver = new ResourceEntityResolver(resourceLoader);
			} else {
				this.entityResolver = new DelegatingEntityResolver(getBeanClassLoader());
			}
		}
		return this.entityResolver;
	}

	/**
	 * Set an implementation of the {@code org.xml.sax.ErrorHandler}
	 * interface for custom handling of XML parsing errors and warnings.
	 * <p>If not set, a default SimpleSaxErrorHandler is used that simply
	 * logs warnings using the logger instance of the view class,
	 * and rethrows errors to discontinue the XML transformation.
	 *
	 * @see SimpleSaxErrorHandler
	 */
	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	/**
	 * Specify the {@link BeanDefinitionDocumentReader} implementation to use,
	 * responsible for the actual reading of the XML bean definition document.
	 * <p>The default is {@link DefaultBeanDefinitionDocumentReader}.
	 *
	 * @param documentReaderClass the desired BeanDefinitionDocumentReader implementation class
	 */
	public void setDocumentReaderClass(Class<? extends BeanDefinitionDocumentReader> documentReaderClass) {
		this.documentReaderClass = documentReaderClass;
	}


	/**
	 * 从指定的xml文件加载bean definition,先把资源封装成EncodedResource,主要是为了对Resource进行编码,保证内容读取的正确性
	 *
	 * @param resource the resource descriptor
	 * @return
	 * @throws BeanDefinitionStoreException
	 */
	@Override
	public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
		return loadBeanDefinitions(new EncodedResource(resource));
	}

	/**
	 * 从指定的xml文件加载bean definition
	 *
	 * @param encodedResource 编码后的Resource资源
	 * @return
	 * @throws BeanDefinitionStoreException
	 */
	public int loadBeanDefinitions(EncodedResource encodedResource) throws BeanDefinitionStoreException {

		// 断言判断
		Assert.notNull(encodedResource, "EncodedResource must not be null");
		if (logger.isTraceEnabled()) {
			logger.trace("Loading XML bean definitions from " + encodedResource);
		}

		// 获取已经加载过的资源
		Set<EncodedResource> currentResources = this.resourcesCurrentlyBeingLoaded.get();
		if (currentResources == null) {
			currentResources = new HashSet<>(4);
			this.resourcesCurrentlyBeingLoaded.set(currentResources);
		}
		if (!currentResources.add(encodedResource)) {
			// 将当前资源加入记录中,如果存在,抛出异常,避免一个EncodedResource在加载时,还没加载完,又加载自身,导致死循环
			throw new BeanDefinitionStoreException("Detected cyclic loading of " + encodedResource + " - check your import definitions!");
		}
		try {
			// 从EncodedResource中获取Resource,并从Resource中获取InputStream
			InputStream inputStream = encodedResource.getResource().getInputStream();
			try {
				InputSource inputSource = new InputSource(inputStream);
				if (encodedResource.getEncoding() != null) {
					// 设置编码
					inputSource.setEncoding(encodedResource.getEncoding());
				}
				// <核心>执行加载BeanDefinition
				return doLoadBeanDefinitions(inputSource, encodedResource.getResource());
			} finally {
				inputStream.close();
			}
		} catch (IOException ex) {
			throw new BeanDefinitionStoreException("IOException parsing XML document from " + encodedResource.getResource(), ex);
		} finally {
			// 清除加载记录
			currentResources.remove(encodedResource);
			if (currentResources.isEmpty()) {
				this.resourcesCurrentlyBeingLoaded.remove();
			}
		}
	}

	/**
	 * Load bean definitions from the specified XML file.
	 *
	 * @param inputSource the SAX InputSource to read from
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 */
	public int loadBeanDefinitions(InputSource inputSource) throws BeanDefinitionStoreException {
		return loadBeanDefinitions(inputSource, "resource loaded through SAX InputSource");
	}

	/**
	 * Load bean definitions from the specified XML file.
	 *
	 * @param inputSource         the SAX InputSource to read from
	 * @param resourceDescription a description of the resource
	 *                            (can be {@code null} or empty)
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 */
	public int loadBeanDefinitions(InputSource inputSource, @Nullable String resourceDescription)
			throws BeanDefinitionStoreException {

		return doLoadBeanDefinitions(inputSource, new DescriptiveResource(resourceDescription));
	}


	/**
	 * 真正执行Bean Definition加载的方法
	 *
	 * @param inputSource
	 * @param resource
	 * @return
	 * @throws BeanDefinitionStoreException
	 */
	protected int doLoadBeanDefinitions(InputSource inputSource, Resource resource) throws BeanDefinitionStoreException {

		try {
			// 获取XML Document实例
			Document doc = doLoadDocument(inputSource, resource);

			// 根据Document实例注册Bean信息
			int count = registerBeanDefinitions(doc, resource);
			if (logger.isDebugEnabled()) {
				logger.debug("Loaded " + count + " bean definitions from " + resource);
			}
			return count;
		} catch (BeanDefinitionStoreException ex) {
			throw ex;
		} catch (SAXParseException ex) {
			throw new XmlBeanDefinitionStoreException(resource.getDescription(),
					"Line " + ex.getLineNumber() + " in XML document from " + resource + " is invalid", ex);
		} catch (SAXException ex) {
			throw new XmlBeanDefinitionStoreException(resource.getDescription(),
					"XML document from " + resource + " is invalid", ex);
		} catch (ParserConfigurationException ex) {
			throw new BeanDefinitionStoreException(resource.getDescription(),
					"Parser configuration exception parsing XML from " + resource, ex);
		} catch (IOException ex) {
			throw new BeanDefinitionStoreException(resource.getDescription(),
					"IOException parsing XML document from " + resource, ex);
		} catch (Throwable ex) {
			throw new BeanDefinitionStoreException(resource.getDescription(),
					"Unexpected exception parsing XML document from " + resource, ex);
		}
	}

	/**
	 * 获取XML Document实例
	 *
	 * @param inputSource
	 * @param resource
	 * @return
	 * @throws Exception
	 */
	protected Document doLoadDocument(InputSource inputSource, Resource resource) throws Exception {
		return this.documentLoader.loadDocument(inputSource, getEntityResolver(), this.errorHandler,
				getValidationModeForResource(resource), isNamespaceAware());
	}

	/**
	 * 根据Resource获取XML验证模型
	 *
	 * @param resource
	 * @return
	 */
	protected int getValidationModeForResource(Resource resource) {

		// 获取指定的验证模型
		int validationModeToUse = getValidationMode();

		// 手动指定并且不是自动检测验证模型,直接返回指定的验证模型
		if (validationModeToUse != VALIDATION_AUTO) {
			return validationModeToUse;
		}

		// 自动获取验证模型并且获取到的不是自动检测验证模型,直接返回该验证模型
		int detectedMode = detectValidationMode(resource);
		if (detectedMode != VALIDATION_AUTO) {
			return detectedMode;
		}

		// 返回XSD验证模型
		return VALIDATION_XSD;
	}

	/**
	 * 检测验证模型
	 *
	 * @param resource
	 * @return
	 */
	protected int detectValidationMode(Resource resource) {

		if (resource.isOpen()) {
			// 不可读
			throw new BeanDefinitionStoreException(
					"Passed-in Resource [" + resource + "] contains an open stream: " +
							"cannot determine validation mode automatically. Either pass in a Resource " +
							"that is able to create fresh streams, or explicitly specify the validationMode " +
							"on your XmlBeanDefinitionReader instance.");
		}

		InputStream inputStream;
		try {
			inputStream = resource.getInputStream();
		} catch (IOException ex) {
			// 打开InputStream流失败
			throw new BeanDefinitionStoreException(
					"Unable to determine validation mode for [" + resource + "]: cannot open InputStream. " +
							"Did you attempt to load directly from a SAX InputSource without specifying the " +
							"validationMode on your XmlBeanDefinitionReader instance?", ex);
		}

		try {
			// 获取相应的验证模型
			return this.validationModeDetector.detectValidationMode(inputStream);
		} catch (IOException ex) {
			throw new BeanDefinitionStoreException("Unable to determine validation mode for [" +
					resource + "]: an error occurred whilst reading from the InputStream.", ex);
		}
	}

	/**
	 * Register the bean definitions contained in the given DOM document.
	 * Called by {@code loadBeanDefinitions}.
	 * <p>Creates a new instance of the parser class and invokes
	 * {@code registerBeanDefinitions} on it.
	 *
	 * @param doc      the DOM document
	 * @param resource the resource descriptor (for context information)
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of parsing errors
	 * @see #loadBeanDefinitions
	 * @see #setDocumentReaderClass
	 * @see BeanDefinitionDocumentReader#registerBeanDefinitions
	 */
	public int registerBeanDefinitions(Document doc, Resource resource) throws BeanDefinitionStoreException {
		BeanDefinitionDocumentReader documentReader = createBeanDefinitionDocumentReader();
		int countBefore = getRegistry().getBeanDefinitionCount();
		documentReader.registerBeanDefinitions(doc, createReaderContext(resource));
		return getRegistry().getBeanDefinitionCount() - countBefore;
	}

	/**
	 * Create the {@link BeanDefinitionDocumentReader} to use for actually
	 * reading bean definitions from an XML document.
	 * <p>The default implementation instantiates the specified "documentReaderClass".
	 *
	 * @see #setDocumentReaderClass
	 */
	protected BeanDefinitionDocumentReader createBeanDefinitionDocumentReader() {
		return BeanUtils.instantiateClass(this.documentReaderClass);
	}

	/**
	 * Create the {@link XmlReaderContext} to pass over to the document reader.
	 */
	public XmlReaderContext createReaderContext(Resource resource) {
		return new XmlReaderContext(resource, this.problemReporter, this.eventListener,
				this.sourceExtractor, this, getNamespaceHandlerResolver());
	}

	/**
	 * Lazily create a default NamespaceHandlerResolver, if not set before.
	 *
	 * @see #createDefaultNamespaceHandlerResolver()
	 */
	public NamespaceHandlerResolver getNamespaceHandlerResolver() {
		if (this.namespaceHandlerResolver == null) {
			this.namespaceHandlerResolver = createDefaultNamespaceHandlerResolver();
		}
		return this.namespaceHandlerResolver;
	}

	/**
	 * Create the default implementation of {@link NamespaceHandlerResolver} used if none is specified.
	 * <p>The default implementation returns an instance of {@link DefaultNamespaceHandlerResolver}.
	 *
	 * @see DefaultNamespaceHandlerResolver#DefaultNamespaceHandlerResolver(ClassLoader)
	 */
	protected NamespaceHandlerResolver createDefaultNamespaceHandlerResolver() {
		ClassLoader cl = (getResourceLoader() != null ? getResourceLoader().getClassLoader() : getBeanClassLoader());
		return new DefaultNamespaceHandlerResolver(cl);
	}

}

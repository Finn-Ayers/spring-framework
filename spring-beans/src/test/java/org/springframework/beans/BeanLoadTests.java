package org.springframework.beans;

import org.junit.Test;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.tests.sample.beans.TestBean;

/**
 * copyright (C), 2015-2019, 深圳市点购电子商务控股有限公司广州分公司
 * history:
 * <author>          <time>          <version>          <desc>
 * GT             updatedtime          1.0              desc
 *
 * @author GT
 * @version 1.0
 * @description bean加载测试类
 * @create 2019/8/20 11:11
 */
public class BeanLoadTests {

	@Test
	public void beanLoadTest() {

		// 创建默认的Bean工厂,DefaultListableBeanFactory 是 BeanFactory 的一个子类
		// DefaultListableBeanFactory 则是真正可以独立使用的 IoC 容器，它是整个 Spring IoC 的始祖
		DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

		// 创建XML读取器,用于读取并解析Spring Xml配置文件
		XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);

		// 资源读取器
		FileSystemResourceLoader resourceLoader = new FileSystemResourceLoader();
		Resource resource = resourceLoader.getResource("E:\\BeanFactoryUtilsTests-leaf.xml");

		// 加载并注册Bean Definition到IOC容器
		int registerNum = beanDefinitionReader.loadBeanDefinitions(resource);

		// 从IOC容器中获取注册的Bean
		// BeanDefinition beanDefinition = beanDefinitionReader.getRegistry().getBeanDefinition("test3");

		Object circularA = beanFactory.getBean("circularA");
		Object circularB = beanFactory.getBean("circularB");

		System.out.println("注册数: " + registerNum);
	}
}
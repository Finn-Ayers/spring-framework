/*
 * Copyright 2002-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans.factory.xml;

import org.w3c.dom.Document;

import org.springframework.beans.factory.BeanDefinitionStoreException;

/**
 * BeanDefinition文档读取器接口,只有一个默认子类
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @see XmlBeanDefinitionReader#setDocumentReaderClass
 * @see DefaultBeanDefinitionDocumentReader#registerBeanDefinitions(Document, XmlReaderContext) 默认子类
 * @since 18.12.2003
 */
public interface BeanDefinitionDocumentReader {

	/**
	 * 从给定的 Document 对象中解析定义的 BeanDefinition 并将他们注册到注册表中
	 *
	 * @param doc           待解析的 Document 对象
	 * @param readerContext 解析器的当前上下文，包括目标注册表和被解析的资源,它是根据 Resource 来创建的
	 * @throws BeanDefinitionStoreException
	 */
	void registerBeanDefinitions(Document doc, XmlReaderContext readerContext)
			throws BeanDefinitionStoreException;

}

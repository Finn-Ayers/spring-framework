/*
 * Copyright 2002-2012 the original author or authors.
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
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

/**
 * 获取XML Document实例
 *
 * @author Rob Harrop
 * @see DefaultDocumentLoader
 * @since 2.0
 */
public interface DocumentLoader {

	/**
	 * 获取XML Document实例
	 *
	 * @param inputSource
	 * @param entityResolver
	 * @param errorHandler
	 * @param validationMode
	 * @param namespaceAware
	 * @return
	 * @throws Exception
	 */
	Document loadDocument(
			InputSource inputSource,
			EntityResolver entityResolver,
			ErrorHandler errorHandler,
			int validationMode, boolean namespaceAware) throws Exception;

}

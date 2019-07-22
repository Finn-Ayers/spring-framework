/*
 * Copyright 2002-2007 the original author or authors.
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

package org.springframework.core.io;

/**
 * 上下文加载的资源的扩展接口,可以来自{@link javax.servlet.ServletContext},也可以来自普通类路径路径或相关文件系统路径
 *
 * @author Juergen Hoeller
 * @see org.springframework.web.context.support.ServletContextResource
 * @since 2.5
 */
public interface ContextResource extends Resource {

	/**
	 * 返回上下文中的路径
	 *
	 * <p>这通常是相对于特定于上下文的根目录的路径，例如* ServletContext根目录或PortletContext根目录
	 */
	String getPathWithinContext();

}

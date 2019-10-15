/*
 * Copyright 2002-2018 the original author or authors.
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

import org.springframework.lang.Nullable;
import org.springframework.util.ResourceUtils;

/**
 * 统一资源定定位(加载),为 Spring 资源加载的统一抽象，具体的资源加载则由相应的实现类来完成
 *
 * @author Juergen Hoeller
 */
public interface ResourceLoader {

	/**
	 * CLASSPATH URL 前缀。默认为："classpath:"
	 */
	String CLASSPATH_URL_PREFIX = ResourceUtils.CLASSPATH_URL_PREFIX;


	/**
	 * 根据所提供资源的路径 location 返回 Resource 实例，但是它不确保该 Resource 一定存在，需要调用 Resource#exist() 方法来判断
	 * 该方法支持以下模式的资源加载：
	 * URL位置资源，如 "file:C:/test.dat" 。
	 * ClassPath位置资源，如 "classpath:test.dat 。
	 * 相对路径资源，如 "WEB-INF/test.dat" ，此时返回的Resource 实例，根据实现不同而不同。
	 * 该方法的主要实现是在其子类 DefaultResourceLoader 中实现
	 *
	 * @param location
	 * @return
	 */
	Resource getResource(String location);

	/**
	 * 返回 ClassLoader 实例，对于想要获取 ResourceLoader 使用的 ClassLoader 用户来说，可以直接调用该方法来获取
	 *
	 * @return
	 */
	@Nullable
	ClassLoader getClassLoader();

}

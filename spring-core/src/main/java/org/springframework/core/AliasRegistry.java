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

package org.springframework.core;

/**
 * 用于管理别名的通用接口
 *
 * @author Juergen Hoeller
 * @since 2.5.2
 */
public interface AliasRegistry {

	/**
	 * 根据别名注册
	 *
	 * @param name
	 * @param alias
	 */
	void registerAlias(String name, String alias);

	/**
	 * 从注册表中删除给定的别名
	 *
	 * @param alias
	 */
	void removeAlias(String alias);

	/**
	 * 判断给定的名称是否是别名
	 *
	 * @param name
	 * @return
	 */
	boolean isAlias(String name);

	/**
	 * 返回给定的别名集
	 *
	 * @param name
	 * @return
	 */
	String[] getAliases(String name);

}

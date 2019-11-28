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

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.AliasRegistry;

/**
 * 该类的作用主要是向注册表中注册 BeanDefinition 实例，完成 注册的过程,
 * 已知默认实现类是DefaultListableBeanFactory、GenericApplicationContext、SimpleBeanDefinitionRegistry
 *
 * @author Juergen Hoeller
 * @see org.springframework.beans.factory.config.BeanDefinition
 * @see AbstractBeanDefinition
 * @see RootBeanDefinition
 * @see ChildBeanDefinition
 * @see DefaultListableBeanFactory
 * @see org.springframework.context.support.GenericApplicationContext
 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
 * @see PropertiesBeanDefinitionReader
 * @since 26.11.2003
 */
public interface BeanDefinitionRegistry extends AliasRegistry {

	/**
	 * 注册bean实例
	 *
	 * @param beanName
	 * @param beanDefinition
	 * @throws BeanDefinitionStoreException
	 */
	void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException;

	/**
	 * 移除bean实例
	 *
	 * @param beanName
	 * @throws NoSuchBeanDefinitionException
	 */
	void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

	/**
	 * 获取bean实例
	 *
	 * @param beanName
	 * @return
	 * @throws NoSuchBeanDefinitionException
	 */
	BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

	/**
	 * 判断容器中是否包含bean实例
	 *
	 * @param beanName
	 * @return
	 */
	boolean containsBeanDefinition(String beanName);

	/**
	 * 获取容器中所有bean名称
	 *
	 * @return
	 */
	String[] getBeanDefinitionNames();

	/**
	 * 返回容器中bean实例数
	 *
	 * @return
	 */
	int getBeanDefinitionCount();

	/**
	 * 确定给定的bean名称是否已在此注册表中使用,即是否在该名称下注册了本地bean或别名
	 *
	 * @param beanName
	 * @return
	 */
	boolean isBeanNameInUse(String beanName);

}

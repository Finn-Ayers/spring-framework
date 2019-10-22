/*
 * Copyright 2002-2019 the original author or authors.
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

package org.springframework.transaction;

import org.springframework.lang.Nullable;

/**
 * 事务定义,定义事务的传播特性和隔离级别
 *
 * @author Juergen Hoeller
 * @since 08.05.2003
 */
public interface TransactionDefinition {

	/**
	 * 如果存在一个事务，则支持当前事务。如果没有事务则开启
	 */
	int PROPAGATION_REQUIRED = 0;

	/**
	 * 如果存在一个事务，支持当前事务。如果没有事务，则非事务的执行
	 *
	 * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#setTransactionSynchronization
	 * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#SYNCHRONIZATION_ON_ACTUAL_TRANSACTION
	 */
	int PROPAGATION_SUPPORTS = 1;

	/**
	 * 如果已经存在一个事务，支持当前事务。如果没有一个活动的事务，则抛出异常
	 */
	int PROPAGATION_MANDATORY = 2;

	/**
	 * 总是开启一个新的事务。如果一个事务已经存在，则将这个存在的事务挂起
	 *
	 * @see org.springframework.transaction.jta.JtaTransactionManager#setTransactionManager
	 */
	int PROPAGATION_REQUIRES_NEW = 3;

	/**
	 * 总是非事务地执行，并挂起任何存在的事务
	 *
	 * @see org.springframework.transaction.jta.JtaTransactionManager#setTransactionManager
	 */
	int PROPAGATION_NOT_SUPPORTED = 4;

	/**
	 * 总是非事务地执行，如果存在一个活动事务，则抛出异常
	 */
	int PROPAGATION_NEVER = 5;

	/**
	 * 如果一个活动的事务存在，则运行在一个嵌套的事务中. 如果没有活动事务, PROPAGATION_REQUIRED 属性执行
	 *
	 * @see org.springframework.jdbc.datasource.DataSourceTransactionManager
	 */
	int PROPAGATION_NESTED = 6;


	/**
	 * 这是一个PlatfromTransactionManager默认的隔离级别，使用数据库默认的事务隔离级别
	 *
	 * @see java.sql.Connection
	 */
	int ISOLATION_DEFAULT = -1;

	/**
	 * 读未提交
	 *
	 * @see java.sql.Connection#TRANSACTION_READ_UNCOMMITTED
	 */
	int ISOLATION_READ_UNCOMMITTED = 1;

	/**
	 * 读提交
	 *
	 * @see java.sql.Connection#TRANSACTION_READ_COMMITTED
	 */
	int ISOLATION_READ_COMMITTED = 2;

	/**
	 * 可重复度
	 *
	 * @see java.sql.Connection#TRANSACTION_REPEATABLE_READ
	 */
	int ISOLATION_REPEATABLE_READ = 4;

	/**
	 * 串行化
	 *
	 * @see java.sql.Connection#TRANSACTION_SERIALIZABLE
	 */
	int ISOLATION_SERIALIZABLE = 8;

	/**
	 * 事务超时,就是指一个事务所允许执行的最长时间,如果超过该时间限制但事务还没有完成,则自动回滚事务,其单位是秒
	 * 默认设置为底层事务系统的超时值,如果底层数据库事务系统没有设置超时值,那么就是none(-1),既没有超时限制
	 */
	int TIMEOUT_DEFAULT = -1;

	/**
	 * 获取事务传播特性
	 *
	 * @see org.springframework.transaction.support.TransactionSynchronizationManager#isActualTransactionActive()
	 */
	default int getPropagationBehavior() {
		return PROPAGATION_REQUIRED;
	}

	/**
	 * 获取事务隔离级别
	 *
	 * @see org.springframework.transaction.support.AbstractPlatformTransactionManager#setValidateExistingTransaction
	 */
	default int getIsolationLevel() {
		return ISOLATION_DEFAULT;
	}

	/**
	 * 获取事务超时时间
	 */
	default int getTimeout() {
		return TIMEOUT_DEFAULT;
	}

	/**
	 * 是否是只读事务,默认为false
	 *
	 * @see org.springframework.transaction.support.TransactionSynchronization#beforeCommit(boolean)
	 * @see org.springframework.transaction.support.TransactionSynchronizationManager#isCurrentTransactionReadOnly()
	 */
	default boolean isReadOnly() {
		return false;
	}

	/**
	 * 获取事务名称,默认为null
	 *
	 * @see org.springframework.transaction.interceptor.TransactionAspectSupport
	 * @see org.springframework.transaction.support.TransactionSynchronizationManager#getCurrentTransactionName()
	 */
	@Nullable
	default String getName() {
		return null;
	}


	/**
	 * Return an unmodifiable {@code TransactionDefinition} with defaults.
	 * <p>For customization purposes, use the modifiable
	 * {@link org.springframework.transaction.support.DefaultTransactionDefinition}
	 * instead.
	 *
	 * @since 5.2
	 */
	static TransactionDefinition withDefaults() {
		return StaticTransactionDefinition.INSTANCE;
	}

}

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
import org.springframework.util.Assert;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * 对字节数组提供的数据的封装Resource,如果通过InputStream形式访问该类型的资源,
 *
 * <p>该实现会根据字节数组的数据构造一个相应的 ByteArrayInputStream
 *
 * @author gaoteng
 * @create 2019-07-08 13:23
 */
public class ByteArrayResource extends AbstractResource {

	private final byte[] byteArray;

	private final String description;


	/**
	 * 根据byte数组构建
	 *
	 * @param byteArray
	 */
	public ByteArrayResource(byte[] byteArray) {
		this(byteArray, "resource loaded from byte array");
	}

	/**
	 * 根据byte数组、description构建
	 *
	 * @param byteArray
	 * @param description
	 */
	public ByteArrayResource(byte[] byteArray, @Nullable String description) {
		Assert.notNull(byteArray, "Byte array must not be null");
		this.byteArray = byteArray;
		this.description = (description != null ? description : "");
	}

	/**
	 * 获取byte数组
	 *
	 * @return
	 */
	public final byte[] getByteArray() {
		return this.byteArray;
	}

	/**
	 * 是否存在,默认为true表示存在
	 *
	 * @return
	 */
	@Override
	public boolean exists() {
		return true;
	}

	/**
	 * 内容长度,返回byte数组长度
	 *
	 * @return
	 */
	@Override
	public long contentLength() {
		return this.byteArray.length;
	}

	/**
	 * 获取InputStream
	 *
	 * @return
	 * @throws IOException
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(this.byteArray);
	}

	/**
	 * 获取资源描述,描述由构造器传入
	 *
	 * @return
	 */
	@Override
	public String getDescription() {
		return "Byte array resource [" + this.description + "]";
	}

	@Override
	public boolean equals(@Nullable Object other) {
		return (this == other || (other instanceof ByteArrayResource &&
				Arrays.equals(((ByteArrayResource) other).byteArray, this.byteArray)));
	}

	@Override
	public int hashCode() {
		return (byte[].class.hashCode() * 29 * this.byteArray.length);
	}

}

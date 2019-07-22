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

import java.io.IOException;
import java.io.InputStream;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * 将给定的 InputStream 作为一种资源的 Resource 的实现类
 *
 * @author gaoteng
 * @create 2019-07-08 13:23
 */
public class InputStreamResource extends AbstractResource {

	private final InputStream inputStream;

	private final String description;

	private boolean read = false;


	public InputStreamResource(InputStream inputStream) {
		this(inputStream, "resource loaded through InputStream");
	}

	/**
	 * 根据inputStream、描述创建
	 *
	 * @param inputStream
	 * @param description
	 */
	public InputStreamResource(InputStream inputStream, @Nullable String description) {
		Assert.notNull(inputStream, "InputStream must not be null");
		this.inputStream = inputStream;
		this.description = (description != null ? description : "");
	}


	/**
	 * 资源是否存在,默认为true表示存在
	 *
	 * @return
	 */
	@Override
	public boolean exists() {
		return true;
	}

	/**
	 * 资源是否打开,默认为true表示打开
	 *
	 * @return
	 */
	@Override
	public boolean isOpen() {
		return true;
	}

	@Override
	public InputStream getInputStream() throws IOException, IllegalStateException {
		if (this.read) {
			throw new IllegalStateException("InputStream has already been read - " +
					"do not use InputStreamResource if a stream needs to be read multiple times");
		}
		this.read = true;
		return this.inputStream;
	}

	/**
	 * 获取资源描述
	 *
	 * @return
	 */
	@Override
	public String getDescription() {
		return "InputStream resource [" + this.description + "]";
	}

	@Override
	public boolean equals(@Nullable Object other) {
		return (this == other || (other instanceof InputStreamResource &&
				((InputStreamResource) other).inputStream.equals(this.inputStream)));
	}

	@Override
	public int hashCode() {
		return this.inputStream.hashCode();
	}

}

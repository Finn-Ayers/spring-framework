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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import org.springframework.lang.Nullable;

/**
 * spring统一资源抽象类,为spring提供所有资源的抽象和访问接口
 *
 * @author gaoteng
 * @create 2019-07-08 14:23
 **/
public interface Resource extends InputStreamSource {

	/**
	 * 资源是否存在
	 *
	 * @return
	 */
	boolean exists();

	/**
	 * 资源是否可读
	 *
	 * @return
	 */
	default boolean isReadable() {
		return exists();
	}

	/**
	 * 资源所代表的句柄是否被一个stream打开了
	 *
	 * @return
	 */
	default boolean isOpen() {
		return false;
	}

	/**
	 * 是否为file
	 *
	 * @return
	 */
	default boolean isFile() {
		return false;
	}

	/**
	 * 返回资源的URL的句柄
	 *
	 * @return
	 * @throws IOException
	 */
	URL getURL() throws IOException;

	/**
	 * 返回资源的URI的句柄
	 *
	 * @return
	 * @throws IOException
	 */
	URI getURI() throws IOException;

	/**
	 * 返回资源的File的句柄
	 *
	 * @return
	 * @throws IOException
	 */
	File getFile() throws IOException;

	/**
	 * 返回 ReadableByteChannel
	 *
	 * @return
	 * @throws IOException
	 */
	default ReadableByteChannel readableChannel() throws IOException {
		return Channels.newChannel(getInputStream());
	}

	/**
	 * 资源内容的长度
	 *
	 * @return
	 * @throws IOException
	 */
	long contentLength() throws IOException;

	/**
	 * 资源最后的修改时间
	 *
	 * @return
	 * @throws IOException
	 */
	long lastModified() throws IOException;

	/**
	 * 根据资源的相对路径创建新资源
	 *
	 * @param relativePath
	 * @return
	 * @throws IOException
	 */
	Resource createRelative(String relativePath) throws IOException;

	/**
	 * 资源的文件名 @Nullable表示资源的文件名值不能为空
	 *
	 * @return
	 */
	@Nullable
	String getFilename();

	/**
	 * 资源的描述
	 *
	 * @return
	 */
	String getDescription();

}

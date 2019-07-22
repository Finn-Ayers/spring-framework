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

package org.springframework.core.io;

import org.springframework.core.NestedIOException;
import org.springframework.core.log.LogAccessor;
import org.springframework.lang.Nullable;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

/**
 * Resource接口的默认抽象实现,它实现了Resource接口的大部分的公共实现
 *
 * <p>如果我们想要实现自定义的Resource,记住不要实现Resource接口,
 * 应该继承AbstractResource抽象类,然后根据当前的具体资源特性覆盖相应的方法即可
 *
 * @author gaoteng
 * @create 2019-07-08 13:23
 */
public abstract class AbstractResource implements Resource {

	private static final LogAccessor logAccessor = new LogAccessor(AbstractResource.class);

	/**
	 * 判断资源是否存在,若判断过程产生异常(因为会调用SecurityManager来判断)就关闭对应的流
	 *
	 * @return
	 */
	@Override
	public boolean exists() {
		try {
			// 基于File进行判断
			boolean exists = getFile().exists();
			return exists;
		} catch (IOException ex) {
			try {
				// 基于InputStream进行判断
				getInputStream().close();
				return true;
			} catch (Throwable isEx) {
				logAccessor.debug(ex, () -> "Could not close InputStream for resource: " + getDescription());
				return false;
			}
		}
	}

	/**
	 * 根据exists方法返回值来判断是否可读,返回true表示可读,返回false表示不可读
	 *
	 * @return
	 */
	@Override
	public boolean isReadable() {
		return exists();
	}

	/**
	 * 资源是否打开,直接返回false表示未打开
	 *
	 * @return
	 */
	@Override
	public boolean isOpen() {
		return false;
	}

	/**
	 * 是否是文件,直接返回false表示不是文件
	 *
	 * @return
	 */
	@Override
	public boolean isFile() {
		return false;
	}

	/**
	 * 直接抛出异常让子类实现
	 *
	 * @return
	 * @throws IOException
	 */
	@Override
	public URL getURL() throws IOException {
		throw new FileNotFoundException(getDescription() + " cannot be resolved to URL");
	}

	/**
	 * 基于getURL()返回的URL构建URI
	 *
	 * @return
	 * @throws IOException
	 */
	@Override
	public URI getURI() throws IOException {
		URL url = getURL();
		try {
			return ResourceUtils.toURI(url);
		} catch (URISyntaxException ex) {
			throw new NestedIOException("Invalid URI [" + url + "]", ex);
		}
	}

	/**
	 * 直接抛出异常让子类实现
	 *
	 * @return
	 * @throws IOException
	 */
	@Override
	public File getFile() throws IOException {
		throw new FileNotFoundException(getDescription() + " cannot be resolved to absolute file path");
	}

	/**
	 * 根据getInputStream()的返回结果构建ReadableByteChannel
	 *
	 * @return
	 * @throws IOException
	 */
	@Override
	public ReadableByteChannel readableChannel() throws IOException {
		return Channels.newChannel(getInputStream());
	}

	/**
	 * 获取资源内容的长度,这个资源内容长度实际就是资源的字节长度，通过全部读取一遍来判断
	 *
	 * @return
	 * @throws IOException
	 */
	@Override
	public long contentLength() throws IOException {
		InputStream is = getInputStream();
		try {
			long size = 0;
			byte[] buf = new byte[256];
			int read;
			while ((read = is.read(buf)) != -1) {
				size += read;
			}
			return size;
		} finally {
			try {
				is.close();
			} catch (IOException ex) {
				logAccessor.debug(ex, () -> "Could not close InputStream for resource: " + getDescription());
			}
		}
	}

	/**
	 * 返回资源最后的修改时间
	 *
	 * @return
	 * @throws IOException
	 */
	@Override
	public long lastModified() throws IOException {
		File fileToCheck = getFileForLastModifiedCheck();
		long lastModified = fileToCheck.lastModified();
		if (lastModified == 0L && !fileToCheck.exists()) {
			throw new FileNotFoundException(getDescription() +
					" cannot be resolved in the file system for checking its last-modified timestamp");
		}
		return lastModified;
	}

	protected File getFileForLastModifiedCheck() throws IOException {
		return getFile();
	}

	/**
	 * 直接抛出异常让子类实现
	 *
	 * @param relativePath
	 * @return
	 * @throws IOException
	 */
	@Override
	public Resource createRelative(String relativePath) throws IOException {
		throw new FileNotFoundException("Cannot create a relative resource for " + getDescription());
	}

	/**
	 * 获取资源名称,默认返回null,交给子类实现
	 *
	 * @return
	 */
	@Override
	@Nullable
	public String getFilename() {
		return null;
	}

	@Override
	public boolean equals(@Nullable Object other) {
		return (this == other || (other instanceof Resource &&
				((Resource) other).getDescription().equals(getDescription())));
	}

	@Override
	public int hashCode() {
		return getDescription().hashCode();
	}

	/**
	 * 返回资源的描述
	 *
	 * @return
	 */
	@Override
	public String toString() {
		return getDescription();
	}

}

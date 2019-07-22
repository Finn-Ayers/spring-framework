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
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;

/**
 * Url相关资源类,继承自AbstractFileResolvingResource,具体实现委托给父类实现
 *
 * @author gaoteng
 * @create 2019-07-08 13:23
 */
public class UrlResource extends AbstractFileResolvingResource {

	/**
	 * 原始URI(如果有)用于URI和文件访问
	 */
	@Nullable
	private final URI uri;

	/**
	 * 原始URL，用于实际访问
	 */
	private final URL url;

	/**
	 * 清理的url
	 */
	private final URL cleanedUrl;


	/**
	 * 根据URI创建
	 *
	 * @param uri
	 * @throws MalformedURLException
	 */
	public UrlResource(URI uri) throws MalformedURLException {
		Assert.notNull(uri, "URI must not be null");
		this.uri = uri;
		this.url = uri.toURL();
		this.cleanedUrl = getCleanedUrl(this.url, uri.toString());
	}

	/**
	 * 根据URL创建
	 *
	 * @param url
	 */
	public UrlResource(URL url) {
		Assert.notNull(url, "URL must not be null");
		this.url = url;
		this.cleanedUrl = getCleanedUrl(this.url, url.toString());
		this.uri = null;
	}

	/**
	 * 根据字符串path创建
	 *
	 * @param path
	 * @throws MalformedURLException
	 */
	public UrlResource(String path) throws MalformedURLException {
		Assert.notNull(path, "Path must not be null");
		this.uri = null;
		this.url = new URL(path);
		this.cleanedUrl = getCleanedUrl(this.url, path);
	}

	/**
	 * 根据URL规范创建
	 *
	 * @param protocol
	 * @param location
	 * @throws MalformedURLException
	 */
	public UrlResource(String protocol, String location) throws MalformedURLException {
		this(protocol, location, null);
	}

	/**
	 * 根据URL规范创建
	 *
	 * @param protocol
	 * @param location
	 * @param fragment
	 * @throws MalformedURLException
	 */
	public UrlResource(String protocol, String location, @Nullable String fragment) throws MalformedURLException {
		try {
			this.uri = new URI(protocol, location, fragment);
			this.url = this.uri.toURL();
			this.cleanedUrl = getCleanedUrl(this.url, this.uri.toString());
		} catch (URISyntaxException ex) {
			MalformedURLException exToThrow = new MalformedURLException(ex.getMessage());
			exToThrow.initCause(ex);
			throw exToThrow;
		}
	}


	/**
	 * 获取原始URL的已清理URL
	 *
	 * @param originalUrl
	 * @param originalPath
	 * @return
	 */
	private URL getCleanedUrl(URL originalUrl, String originalPath) {
		String cleanedPath = StringUtils.cleanPath(originalPath);
		if (!cleanedPath.equals(originalPath)) {
			try {
				return new URL(cleanedPath);
			} catch (MalformedURLException ex) {
				// Cleaned URL path cannot be converted to URL -> take original URL.
			}
		}
		return originalUrl;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		URLConnection con = this.url.openConnection();
		ResourceUtils.useCachesIfNecessary(con);
		try {
			return con.getInputStream();
		} catch (IOException ex) {
			// Close the HTTP connection (if applicable).
			if (con instanceof HttpURLConnection) {
				((HttpURLConnection) con).disconnect();
			}
			throw ex;
		}
	}

	/**
	 * 获取URL
	 *
	 * @return
	 */
	@Override
	public URL getURL() {
		return this.url;
	}

	/**
	 * 获取URI,如果为空则从父类获取
	 *
	 * @return
	 * @throws IOException
	 */
	@Override
	public URI getURI() throws IOException {
		if (this.uri != null) {
			return this.uri;
		} else {
			return super.getURI();
		}
	}

	/**
	 * 是否为File,如果uri为空则从父类获取
	 *
	 * @return
	 */
	@Override
	public boolean isFile() {
		if (this.uri != null) {
			return super.isFile(this.uri);
		} else {
			return super.isFile();
		}
	}

	/**
	 * 获取File,如果为空则从父类获取
	 *
	 * @return
	 * @throws IOException
	 */
	@Override
	public File getFile() throws IOException {
		if (this.uri != null) {
			return super.getFile(this.uri);
		} else {
			return super.getFile();
		}
	}

	/**
	 * 根据relativePath创建Resource
	 *
	 * @param relativePath
	 * @return
	 * @throws MalformedURLException
	 */
	@Override
	public Resource createRelative(String relativePath) throws MalformedURLException {
		if (relativePath.startsWith("/")) {
			relativePath = relativePath.substring(1);
		}
		return new UrlResource(new URL(this.url, relativePath));
	}

	/**
	 * 获取文件名称
	 *
	 * @return
	 */
	@Override
	public String getFilename() {
		return StringUtils.getFilename(this.cleanedUrl.getPath());
	}

	/**
	 * 获取描述
	 *
	 * @return
	 */
	@Override
	public String getDescription() {
		return "URL [" + this.url + "]";
	}

	@Override
	public boolean equals(@Nullable Object other) {
		return (this == other || (other instanceof UrlResource &&
				this.cleanedUrl.equals(((UrlResource) other).cleanedUrl)));
	}

	@Override
	public int hashCode() {
		return this.cleanedUrl.hashCode();
	}

}

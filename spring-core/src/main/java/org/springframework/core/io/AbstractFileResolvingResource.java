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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardOpenOption;

import org.springframework.util.ResourceUtils;

/**
 * 抽象文件资源解析类,对java.net.URL类型资源封装,内部委派URL进行具体的资源操作
 *
 * @author gaoteng
 * @create 2019-07-08 13:23
 */
public abstract class AbstractFileResolvingResource extends AbstractResource {

	/**
	 * File是否存在
	 *
	 * @return
	 */
	@Override
	public boolean exists() {
		try {
			URL url = getURL();
			if (ResourceUtils.isFileURL(url)) {
				// Proceed with file system resolution
				return getFile().exists();
			} else {
				// Try a URL connection content-length header
				URLConnection con = url.openConnection();
				customizeConnection(con);
				HttpURLConnection httpCon =
						(con instanceof HttpURLConnection ? (HttpURLConnection) con : null);
				if (httpCon != null) {
					int code = httpCon.getResponseCode();
					if (code == HttpURLConnection.HTTP_OK) {
						return true;
					} else if (code == HttpURLConnection.HTTP_NOT_FOUND) {
						return false;
					}
				}
				if (con.getContentLengthLong() > 0) {
					return true;
				}
				if (httpCon != null) {
					// No HTTP OK status, and no content-length header: give up
					httpCon.disconnect();
					return false;
				} else {
					// Fall back to stream existence: can we open the stream?
					getInputStream().close();
					return true;
				}
			}
		} catch (IOException ex) {
			return false;
		}
	}

	/**
	 * 资源是否可读
	 *
	 * @return
	 */
	@Override
	public boolean isReadable() {
		try {
			URL url = getURL();
			if (ResourceUtils.isFileURL(url)) {
				// Proceed with file system resolution
				File file = getFile();
				return (file.canRead() && !file.isDirectory());
			} else {
				// Try InputStream resolution for jar resources
				URLConnection con = url.openConnection();
				customizeConnection(con);
				if (con instanceof HttpURLConnection) {
					HttpURLConnection httpCon = (HttpURLConnection) con;
					int code = httpCon.getResponseCode();
					if (code != HttpURLConnection.HTTP_OK) {
						httpCon.disconnect();
						return false;
					}
				}
				long contentLength = con.getContentLengthLong();
				if (contentLength > 0) {
					return true;
				} else if (contentLength == 0) {
					// Empty file or directory -> not considered readable...
					return false;
				} else {
					// Fall back to stream existence: can we open the stream?
					getInputStream().close();
					return true;
				}
			}
		} catch (IOException ex) {
			return false;
		}
	}

	/**
	 * 资源是否为File
	 *
	 * @return
	 */
	@Override
	public boolean isFile() {
		try {
			URL url = getURL();
			if (url.getProtocol().startsWith(ResourceUtils.URL_PROTOCOL_VFS)) {
				return VfsResourceDelegate.getResource(url).isFile();
			}
			return ResourceUtils.URL_PROTOCOL_FILE.equals(url.getProtocol());
		} catch (IOException ex) {
			return false;
		}
	}

	/**
	 * 获取File
	 *
	 * @return
	 * @throws IOException
	 */
	@Override
	public File getFile() throws IOException {
		URL url = getURL();
		if (url.getProtocol().startsWith(ResourceUtils.URL_PROTOCOL_VFS)) {
			return VfsResourceDelegate.getResource(url).getFile();
		}
		return ResourceUtils.getFile(url, getDescription());
	}

	/**
	 * 获取File最后修改时间
	 *
	 * @return
	 * @throws IOException
	 */
	@Override
	protected File getFileForLastModifiedCheck() throws IOException {
		URL url = getURL();
		if (ResourceUtils.isJarURL(url)) {
			URL actualUrl = ResourceUtils.extractArchiveURL(url);
			if (actualUrl.getProtocol().startsWith(ResourceUtils.URL_PROTOCOL_VFS)) {
				return VfsResourceDelegate.getResource(actualUrl).getFile();
			}
			return ResourceUtils.getFile(actualUrl, "Jar URL");
		} else {
			return getFile();
		}
	}

	/**
	 * 资源是否为File
	 *
	 * @param uri
	 * @return
	 */
	protected boolean isFile(URI uri) {
		try {
			if (uri.getScheme().startsWith(ResourceUtils.URL_PROTOCOL_VFS)) {
				return VfsResourceDelegate.getResource(uri).isFile();
			}
			return ResourceUtils.URL_PROTOCOL_FILE.equals(uri.getScheme());
		} catch (IOException ex) {
			return false;
		}
	}

	/**
	 * 通过URI获取File
	 *
	 * @param uri
	 * @return
	 * @throws IOException
	 */
	protected File getFile(URI uri) throws IOException {
		if (uri.getScheme().startsWith(ResourceUtils.URL_PROTOCOL_VFS)) {
			return VfsResourceDelegate.getResource(uri).getFile();
		}
		return ResourceUtils.getFile(uri, getDescription());
	}

	@Override
	public ReadableByteChannel readableChannel() throws IOException {
		try {
			// Try file system channel
			return FileChannel.open(getFile().toPath(), StandardOpenOption.READ);
		} catch (FileNotFoundException | NoSuchFileException ex) {
			// Fall back to InputStream adaptation in superclass
			return super.readableChannel();
		}
	}

	/**
	 * 资源内容长度
	 *
	 * @return
	 * @throws IOException
	 */
	@Override
	public long contentLength() throws IOException {
		URL url = getURL();
		if (ResourceUtils.isFileURL(url)) {
			// Proceed with file system resolution
			File file = getFile();
			long length = file.length();
			if (length == 0L && !file.exists()) {
				throw new FileNotFoundException(getDescription() +
						" cannot be resolved in the file system for checking its content length");
			}
			return length;
		} else {
			// Try a URL connection content-length header
			URLConnection con = url.openConnection();
			customizeConnection(con);
			return con.getContentLengthLong();
		}
	}

	/**
	 * 资源最后修改时间
	 *
	 * @return
	 * @throws IOException
	 */
	@Override
	public long lastModified() throws IOException {
		URL url = getURL();
		boolean fileCheck = false;
		if (ResourceUtils.isFileURL(url) || ResourceUtils.isJarURL(url)) {
			// Proceed with file system resolution
			fileCheck = true;
			try {
				File fileToCheck = getFileForLastModifiedCheck();
				long lastModified = fileToCheck.lastModified();
				if (lastModified > 0L || fileToCheck.exists()) {
					return lastModified;
				}
			} catch (FileNotFoundException ex) {
				// Defensively fall back to URL connection check instead
			}
		}
		// Try a URL connection last-modified header
		URLConnection con = url.openConnection();
		customizeConnection(con);
		long lastModified = con.getLastModified();
		if (fileCheck && lastModified == 0 && con.getContentLengthLong() <= 0) {
			throw new FileNotFoundException(getDescription() +
					" cannot be resolved in the file system for checking its last-modified timestamp");
		}
		return lastModified;
	}

	/**
	 * Customize the given {@link URLConnection}, obtained in the course of an
	 * {@link #exists()}, {@link #contentLength()} or {@link #lastModified()} call.
	 * <p>Calls {@link ResourceUtils#useCachesIfNecessary(URLConnection)} and
	 * delegates to {@link #customizeConnection(HttpURLConnection)} if possible.
	 * Can be overridden in subclasses.
	 *
	 * @param con the URLConnection to customize
	 * @throws IOException if thrown from URLConnection methods
	 */
	protected void customizeConnection(URLConnection con) throws IOException {
		ResourceUtils.useCachesIfNecessary(con);
		if (con instanceof HttpURLConnection) {
			customizeConnection((HttpURLConnection) con);
		}
	}

	/**
	 * Customize the given {@link HttpURLConnection}, obtained in the course of an
	 * {@link #exists()}, {@link #contentLength()} or {@link #lastModified()} call.
	 * <p>Sets request method "HEAD" by default. Can be overridden in subclasses.
	 *
	 * @param con the HttpURLConnection to customize
	 * @throws IOException if thrown from HttpURLConnection methods
	 */
	protected void customizeConnection(HttpURLConnection con) throws IOException {
		con.setRequestMethod("HEAD");
	}


	/**
	 * Inner delegate class, avoiding a hard JBoss VFS API dependency at runtime.
	 */
	private static class VfsResourceDelegate {

		public static Resource getResource(URL url) throws IOException {
			return new VfsResource(VfsUtils.getRoot(url));
		}

		public static Resource getResource(URI uri) throws IOException {
			return new VfsResource(VfsUtils.getRoot(uri));
		}
	}

}

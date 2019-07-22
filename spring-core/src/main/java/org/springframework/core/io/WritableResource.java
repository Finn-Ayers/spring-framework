/*
 * Copyright 2002-2017 the original author or authors.
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
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

/**
 * 继承自Resource,表示一个可写的资源
 *
 * @author gaoteng
 * @create 2019-07-08 14:23
 **/
public interface WritableResource extends Resource {

	/**
	 * 资源是否可写,默认为true可写
	 *
	 * @return
	 */
	default boolean isWritable() {
		return true;
	}

	/**0
	 * 获取OutputStream对象
	 *
	 * @return
	 * @throws IOException
	 */
	OutputStream getOutputStream() throws IOException;

	/**
	 * 根据getOutputStream()的返回结果构建ReadableByteChannel
	 *
	 * @return
	 * @throws IOException
	 */
	default WritableByteChannel writableChannel() throws IOException {
		return Channels.newChannel(getOutputStream());
	}

}

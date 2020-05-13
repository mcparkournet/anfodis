/*
 * MIT License
 *
 * Copyright (c) 2020 MCParkour
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.mcparkour.anfodis.codec;

import java.util.Map;
import org.jetbrains.annotations.Nullable;

public class CodecRegistry<T> {

	private Map<Class<?>, T> typedCodecs;
	private Map<String, T> keyedCodecs;

	public CodecRegistry(Map<Class<?>, T> typedCodecs, Map<String, T> keyedCodecs) {
		this.typedCodecs = typedCodecs;
		this.keyedCodecs = keyedCodecs;
	}

	@Nullable
	public T getTypedCodec(Class<?> type) {
		return this.typedCodecs.get(type);
	}

	@Nullable
	public T getKeyedCodec(String key) {
		return this.keyedCodecs.get(key);
	}

	Map<Class<?>, T> getTypedCodecs() {
		return Map.copyOf(this.typedCodecs);
	}

	Map<String, T> getKeyedCodecs() {
		return Map.copyOf(this.keyedCodecs);
	}
}

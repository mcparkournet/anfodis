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

package net.mcparkour.anfodis.mapper.injection;

import java.lang.reflect.Field;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.common.reflection.Reflections;

public class Injection {

	private MappedInjection mappedInjection;

	public Injection(MappedInjection mappedInjection) {
		this.mappedInjection = mappedInjection;
	}

	public void setValue(Object instance, Object value) {
		Field field = this.mappedInjection.getInjectionField();
		if (field == null) {
			throw new RuntimeException("Field is null");
		}
		Reflections.setFieldValue(field, instance, value);
	}

	public InjectionCodec<?> getCodec(CodecRegistry<InjectionCodec<?>> registry) {
		Field field = this.mappedInjection.getInjectionField();
		if (field == null) {
			throw new RuntimeException("Field is null");
		}
		Class<?> type = field.getType();
		String codecKey = this.mappedInjection.getCodecKey();
		InjectionCodec<?> codec = codecKey == null ? registry.getTypedCodec(type) : registry.getKeyedCodec(codecKey);
		if (codec == null) {
			throw new RuntimeException("Cannot find injection codec for type " + type);
		}
		return codec;
	}
}

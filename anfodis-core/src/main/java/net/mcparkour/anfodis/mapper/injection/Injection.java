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
import java.util.Objects;
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.common.reflection.Reflections;
import org.jetbrains.annotations.Nullable;

public class Injection {

	private Field field;
	@Nullable
	private String codecKey;

	public Injection(InjectionData injectionData) {
		Field field = injectionData.getInjectionField();
		this.field = Objects.requireNonNull(field, "Injection field is null");
		this.codecKey = injectionData.getCodecKey();
	}

	public void setInjectionField(Object instance, Object injection) {
		Reflections.setFieldValue(this.field, instance, injection);
	}

	public InjectionCodec<?> getCodec(CodecRegistry<InjectionCodec<?>> registry) {
		Class<?> type = this.field.getType();
		InjectionCodec<?> codec = this.codecKey == null || this.codecKey.isEmpty() ? registry.getTypedCodec(type) : registry.getKeyedCodec(this.codecKey);
		if (codec == null) {
			throw new RuntimeException("Cannot find injection codec for type " + type);
		}
		return codec;
	}
}

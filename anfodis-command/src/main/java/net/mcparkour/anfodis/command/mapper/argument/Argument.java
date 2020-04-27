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

package net.mcparkour.anfodis.command.mapper.argument;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.common.reflection.Reflections;
import net.mcparkour.common.reflection.type.Types;
import org.jetbrains.annotations.Nullable;

public class Argument<D extends ArgumentData> {

	private D argumentData;

	public Argument(D argumentData) {
		this.argumentData = argumentData;
	}

	public void setArgumentField(Object instance, @Nullable Object argument) {
		Field field = this.argumentData.getArgumentField();
		if (field == null) {
			throw new RuntimeException("Field is null");
		}
		Reflections.setFieldValue(field, instance, argument);
	}

	public ArgumentCodec<?> getArgumentCodec(CodecRegistry<ArgumentCodec<?>> registry) {
		Class<?> type = getFieldType();
		String codecKey = this.argumentData.getArgumentCodecKey();
		ArgumentCodec<?> codec = codecKey == null ? registry.getTypedCodec(type) : registry.getKeyedCodec(codecKey);
		if (codec == null) {
			throw new RuntimeException("Cannot find argument codec for type " + type);
		}
		return codec;
	}

	public Class<?> getFieldType() {
		Field field = this.argumentData.getArgumentField();
		if (field == null) {
			throw new RuntimeException("Field is null");
		}
		return field.getType();
	}

	public ArgumentCodec<?> getGenericTypeArgumentCodec(CodecRegistry<ArgumentCodec<?>> registry, int genericTypeIndex) {
		Type[] genericTypes = getFieldGenericTypes();
		Type genericType = genericTypes[genericTypeIndex];
		Class<?> type = Types.asClassType(genericType);
		String codecKey = this.argumentData.getArgumentCodecKey();
		ArgumentCodec<?> codec = codecKey == null ? registry.getTypedCodec(type) : registry.getKeyedCodec(codecKey);
		if (codec == null) {
			throw new RuntimeException("Cannot find argument codec for generic type " + type);
		}
		return codec;
	}

	public Type[] getFieldGenericTypes() {
		Field field = this.argumentData.getArgumentField();
		if (field == null) {
			throw new RuntimeException("Field is null");
		}
		Type genericType = field.getGenericType();
		ParameterizedType parameterizedType = Types.asParametrizedType(genericType);
		return parameterizedType.getActualTypeArguments();
	}

	public String getName() {
		String name = this.argumentData.getName();
		if (name == null) {
			throw new RuntimeException("Argument name is null");
		}
		if (name.isEmpty()) {
			return getFieldName();
		}
		return name;
	}


	private String getFieldName() {
		Field field = this.argumentData.getArgumentField();
		if (field == null) {
			throw new RuntimeException("Field is null");
		}
		return field.getName();
	}

	public boolean isOptional() {
		Boolean optional = this.argumentData.getOptional();
		if (optional == null) {
			return false;
		}
		return optional;
	}

	protected D getArgumentData() {
		return this.argumentData;
	}
}

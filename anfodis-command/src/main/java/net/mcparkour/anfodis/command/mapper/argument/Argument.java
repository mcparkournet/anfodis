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
import java.util.List;
import java.util.Objects;
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.UnknownCodecException;
import net.mcparkour.anfodis.command.OptionalArgument;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.common.reflection.Reflections;
import net.mcparkour.common.reflection.type.Types;
import org.jetbrains.annotations.Nullable;

public class Argument {

    private Field field;
    private Type argumentType;
    @Nullable
    private String codecKey;
    private String name;
    private boolean optional;

    public Argument(ArgumentData argumentData) {
        Field field = argumentData.getArgumentField();
        this.field = Objects.requireNonNull(field, "Argument field is null");
        this.argumentType = getArgumentType(field);
        this.codecKey = argumentData.getArgumentCodecKey();
        String name = argumentData.getName();
        this.name = Objects.requireNonNull(name, "Argument name is null").isEmpty() ? field.getName() : name;
        Boolean optional = argumentData.getOptional();
        this.optional = optional == null ? false : optional;
    }

    private static Type getArgumentType(Field field) {
        Type genericType = field.getGenericType();
        Class<?> classGenericType = Types.getRawClassType(genericType);
        if (!classGenericType.isAssignableFrom(OptionalArgument.class)) {
            return genericType;
        }
        ParameterizedType parameterizedType = Types.asParametrizedType(genericType);
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        return typeArguments[0];
    }

    public String getUsage() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.optional ? '[' : '<');
        builder.append(this.name);
        if (isList()) {
            builder.append("...");
        }
        builder.append(this.optional ? ']' : '>');
        return builder.toString();
    }

    public void setEmptyArgumentField(Object instance) {
        Object value = isOptionalArgument() ? MappedOptionalArgument.EMPTY_OPTIONAL_ARGUMENT : null;
        Reflections.setFieldValue(this.field, instance, value);
    }

    public void setArgumentField(Object instance, @Nullable Object argument) {
        Object value = isOptionalArgument() ? MappedOptionalArgument.of(argument) : argument;
        Reflections.setFieldValue(this.field, instance, value);
    }

    private boolean isOptionalArgument() {
        Class<?> fieldType = this.field.getType();
        return fieldType.isAssignableFrom(OptionalArgument.class);
    }

    public boolean isList() {
        Class<?> argumentClass = getArgumentClass();
        return argumentClass.isAssignableFrom(List.class);
    }

    public ArgumentCodec<?> getCodec(CodecRegistry<ArgumentCodec<?>> registry) {
        Class<?> argumentClass = getArgumentClass();
        return getCodec(registry, argumentClass);
    }

    public ArgumentCodec<?> getGenericTypeCodec(CodecRegistry<ArgumentCodec<?>> registry, int genericTypeIndex) {
        Type[] genericTypes = getArgumentGenericTypes();
        Type genericType = genericTypes[genericTypeIndex];
        Class<?> type = Types.getRawClassType(genericType);
        return getCodec(registry, type);
    }

    private ArgumentCodec<?> getCodec(CodecRegistry<ArgumentCodec<?>> registry, Class<?> type) {
        return this.codecKey == null || this.codecKey.isEmpty() ? getTypedCodec(registry, type) : getKeyedCodec(registry, this.codecKey);
    }

    private ArgumentCodec<?> getTypedCodec(CodecRegistry<ArgumentCodec<?>> registry, Class<?> type) {
        ArgumentCodec<?> codec = registry.getTypedCodec(type);
        if (codec == null) {
            throw new UnknownCodecException("Cannot find argument codec for type " + type);
        }
        return codec;
    }

    private ArgumentCodec<?> getKeyedCodec(CodecRegistry<ArgumentCodec<?>> registry, String key) {
        ArgumentCodec<?> codec = registry.getKeyedCodec(key);
        if (codec == null) {
            throw new UnknownCodecException("Cannot find argument codec for key '" + key + "'");
        }
        return codec;
    }

    private Type[] getArgumentGenericTypes() {
        ParameterizedType parameterizedType = Types.asParametrizedType(this.argumentType);
        return parameterizedType.getActualTypeArguments();
    }

    protected Class<?> getArgumentClass() {
        return Types.getRawClassType(this.argumentType);
    }

    public String getName() {
        return this.name;
    }

    public boolean isNotOptional() {
        return !this.optional;
    }

    public boolean isOptional() {
        return this.optional;
    }
}

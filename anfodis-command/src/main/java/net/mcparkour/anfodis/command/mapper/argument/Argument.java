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
import java.util.Objects;
import java.util.Optional;
import net.mcparkour.anfodis.codec.UnknownCodecException;
import net.mcparkour.anfodis.codec.registry.CodecRegistry;
import net.mcparkour.anfodis.command.argument.ArgumentContext;
import net.mcparkour.anfodis.command.argument.OptionalArgument;
import net.mcparkour.anfodis.command.argument.VariadicArgument;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.common.reflection.Reflections;
import net.mcparkour.common.reflection.type.Types;
import net.mcparkour.craftmon.permission.Permission;
import org.jetbrains.annotations.Nullable;

public class Argument {

    private final Field field;
    private final Type argumentType;
    private final @Nullable Class<? extends ArgumentCodec<?>> codecType;
    private final ArgumentContext context;
    private final @Nullable String permission;

    public Argument(final ArgumentData argumentData) {
        Field field = argumentData.getArgumentField();
        this.field = Objects.requireNonNull(field, "Argument field is null");
        this.argumentType = getArgumentType(field);
        this.codecType = argumentData.getArgumentCodecType();
        String name = argumentData.getName();
        name = Objects.requireNonNull(name, "Argument name is null").isEmpty() ?
            field.getName() :
            name;
        Boolean optional = argumentData.getOptional();
        optional = optional != null && optional;
        Boolean variadic = argumentData.getVariadic();
        variadic = variadic != null && variadic;
        String permission = argumentData.getPermission();
        this.permission = permission == null ?
            null :
            permission.isEmpty() ?
                name :
                permission;
        this.context = new ArgumentContext(name, optional, variadic);
    }

    private static Type getArgumentType(final Field field) {
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
        boolean optional = this.context.isOptional();
        builder.append(optional ? '[' : '<');
        builder.append(this.context.getName());
        if (this.context.isVariadic()) {
            builder.append("...");
        }
        builder.append(optional ? ']' : '>');
        return builder.toString();
    }

    public void setEmptyArgumentField(final Object instance) {
        Object value = isOptionalArgument() ? OptionalArgument.empty() : null;
        Reflections.setFieldValue(this.field, instance, value);
    }

    public void setArgumentField(final Object instance, final Object argument) {
        Object value = isOptionalArgument() ? OptionalArgument.of(argument) : argument;
        Reflections.setFieldValue(this.field, instance, value);
    }

    private boolean isOptionalArgument() {
        Class<?> fieldType = this.field.getType();
        return fieldType.isAssignableFrom(OptionalArgument.class);
    }

    public boolean isVariadicArgument() {
        Class<?> argumentClass = getArgumentClass();
        return argumentClass.isAssignableFrom(VariadicArgument.class);
    }

    public ArgumentCodec<?> getCodec(final CodecRegistry<ArgumentCodec<?>> registry) {
        Class<?> argumentClass = getArgumentClass();
        return getCodec(registry, argumentClass);
    }

    public ArgumentCodec<?> getGenericTypeCodec(final CodecRegistry<ArgumentCodec<?>> registry, final int genericTypeIndex) {
        Type[] genericTypes = getArgumentGenericTypes();
        Type genericType = genericTypes[genericTypeIndex];
        Class<?> type = Types.getRawClassType(genericType);
        return getCodec(registry, type);
    }

    private ArgumentCodec<?> getCodec(final CodecRegistry<ArgumentCodec<?>> registry, final Class<?> type) {
        return this.codecType == null ?
            getTypedCodec(registry, type) :
            getSelfCodec(registry, this.codecType);
    }

    private ArgumentCodec<?> getTypedCodec(final CodecRegistry<ArgumentCodec<?>> registry, final Class<?> type) {
        Optional<ArgumentCodec<?>> optionalCodec = registry.getTypedCodec(type);
        if (optionalCodec.isEmpty()) {
            throw new UnknownCodecException("Cannot find argument codec for type " + type);
        }
        return optionalCodec.get();
    }

    private ArgumentCodec<?> getSelfCodec(final CodecRegistry<ArgumentCodec<?>> registry, final Class<? extends ArgumentCodec<?>> key) {
        Optional<ArgumentCodec<?>> optionalCodec = registry.getSelfCodec(key);
        if (optionalCodec.isEmpty()) {
            throw new UnknownCodecException("Cannot find argument codec for key '" + key + "'");
        }
        return optionalCodec.get();
    }

    private Type[] getArgumentGenericTypes() {
        ParameterizedType parameterizedType = Types.asParametrizedType(this.argumentType);
        return parameterizedType.getActualTypeArguments();
    }

    protected Class<?> getArgumentClass() {
        return Types.getRawClassType(this.argumentType);
    }

    public boolean isNotOptional() {
        return !this.context.isOptional();
    }

    public ArgumentContext getContext() {
        return this.context;
    }

    public Optional<String> getPermission() {
        return Optional.ofNullable(this.permission);
    }
}

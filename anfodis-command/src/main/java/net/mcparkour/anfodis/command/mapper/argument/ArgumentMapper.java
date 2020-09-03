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
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.mcparkour.anfodis.command.annotation.argument.Argument;
import net.mcparkour.anfodis.command.annotation.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.annotation.argument.Optional;
import net.mcparkour.anfodis.mapper.ElementsMapper;
import net.mcparkour.anfodis.mapper.ElementsMapperBuilder;
import net.mcparkour.anfodis.mapper.Mapper;
import net.mcparkour.anfodis.mapper.MapperBuilderApplier;
import net.mcparkour.anfodis.mapper.SingleElementMapperBuilder;
import org.jetbrains.annotations.Nullable;

public class ArgumentMapper<A extends net.mcparkour.anfodis.command.mapper.argument.Argument, D extends ArgumentData>
    implements Mapper<Field, List<A>> {

    private Function<D, A> argumentSupplier;
    private ElementsMapper<Field, D> mapper;

    public ArgumentMapper(
        final Function<D, A> argumentSupplier,
        final Supplier<D> argumentDataSupplier
    ) {
        this(argumentSupplier, argumentDataSupplier, null);
    }

    public ArgumentMapper(
        final Function<D, A> argumentSupplier,
        final Supplier<D> argumentDataSupplier,
        final @Nullable MapperBuilderApplier<Field, D> additional
    ) {
        this.argumentSupplier = argumentSupplier;
        this.mapper = createMapper(argumentDataSupplier, additional);
    }

    private static <D extends ArgumentData> ElementsMapper<Field, D> createMapper(
        final Supplier<D> argumentDataSupplier,
        final @Nullable MapperBuilderApplier<Field, D> additional
    ) {
        MapperBuilderApplier<Field, D> applier = (builder, data) -> builder
            .required(Argument.class, argument ->
                data.setName(argument.value()))
            .additional(ArgumentCodec.class, argumentCodec ->
                data.setArgumentCodecType(argumentCodec.value()))
            .additional(Optional.class, optional ->
                data.setOptional(true))
            .elementConsumer(data::setArgumentField);
        if (additional != null) {
            applier = applier.andThen(additional);
        }
        return new ElementsMapperBuilder<Field, D>()
            .data(argumentDataSupplier)
            .element(applier)
            .build();
    }

    @Override
    public List<A> map(final Collection<Field> elements) {
        return this.mapper.mapToMultiple(elements)
            .map(this.argumentSupplier)
            .collect(Collectors.toUnmodifiableList());
    }
}

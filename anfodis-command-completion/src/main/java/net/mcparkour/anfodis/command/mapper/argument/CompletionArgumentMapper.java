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
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.mcparkour.anfodis.command.annotation.argument.Completion;
import net.mcparkour.anfodis.command.annotation.argument.CompletionCodec;
import net.mcparkour.anfodis.mapper.MapperBuilderApplier;
import net.mcparkour.anfodis.mapper.SingleElementMapperBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompletionArgumentMapper<A extends CompletionArgument, D extends CompletionArgumentData>
    extends ArgumentMapper<A, D> {

    public CompletionArgumentMapper(
        final Function<D, A> argumentSupplier,
        final Supplier<D> argumentDataSupplier
    ) {
        this(argumentSupplier, argumentDataSupplier, null);
    }

    public CompletionArgumentMapper(
        final Function<D, A> argumentSupplier,
        final Supplier<D> argumentDataSupplier,
        @Nullable final MapperBuilderApplier<Field, D> additional
    ) {
        super(argumentSupplier, argumentDataSupplier, createApplier(additional));
    }

    private static <D extends CompletionArgumentData> MapperBuilderApplier<Field, D> createApplier(
        final @Nullable MapperBuilderApplier<Field, D> additional
    ) {
        MapperBuilderApplier<Field, D> applier = (builder, data) -> builder
            .additional(Completion.class, completion ->
                data.setHasCompletion(true))
            .additional(CompletionCodec.class, completionCodec ->
                data.setCompletionCodecType(completionCodec.value()));
        if (additional == null) {
            return applier;
        }
        return applier.andThen(additional);
    }
}

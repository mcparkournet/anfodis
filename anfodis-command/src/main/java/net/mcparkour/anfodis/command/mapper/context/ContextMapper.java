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

package net.mcparkour.anfodis.command.mapper.context;

import java.lang.reflect.Field;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.mcparkour.anfodis.command.annotation.context.Arguments;
import net.mcparkour.anfodis.command.annotation.context.Receiver;
import net.mcparkour.anfodis.command.annotation.context.RequiredPermission;
import net.mcparkour.anfodis.command.annotation.context.Sender;
import net.mcparkour.anfodis.mapper.ElementsMapperBuilder;
import net.mcparkour.anfodis.mapper.Mapper;
import net.mcparkour.anfodis.mapper.SingleElementMapperBuilder;

public class ContextMapper<C extends Context, D extends ContextData> implements Mapper<Field, C> {

    private Function<D, C> contextSupplier;
    private Supplier<D> contextDataSupplier;
    private Consumer<ElementsMapperBuilder<Field, D>> additional;

    public ContextMapper(final Function<D, C> contextSupplier, final Supplier<D> contextDataSupplier) {
        this(contextSupplier, contextDataSupplier, builder -> {});
    }

    public ContextMapper(final Function<D, C> contextSupplier, final Supplier<D> contextDataSupplier, final Consumer<ElementsMapperBuilder<Field, D>> additional) {
        this.contextSupplier = contextSupplier;
        this.contextDataSupplier = contextDataSupplier;
        this.additional = additional;
    }

    @Override
    public C map(final Iterable<Field> elements) {
        ElementsMapperBuilder<Field, D> builder = new ElementsMapperBuilder<Field, D>()
            .data(this.contextDataSupplier)
            .singleElement(data -> new SingleElementMapperBuilder<Field>()
                .annotation(Arguments.class)
                .elementConsumer(data::setArgumentsField)
                .build())
            .singleElement(data -> new SingleElementMapperBuilder<Field>()
                .annotation(RequiredPermission.class)
                .elementConsumer(data::setRequiredPermissionField)
                .build())
            .singleElement(data -> new SingleElementMapperBuilder<Field>()
                .annotation(Sender.class)
                .elementConsumer(data::setSenderField)
                .build())
            .singleElement(data -> new SingleElementMapperBuilder<Field>()
                .annotation(Receiver.class)
                .elementConsumer(data::setReceiverField)
                .build());
        this.additional.accept(builder);
        return builder.build()
            .mapFirstOptional(elements)
            .map(this.contextSupplier)
            .orElseThrow();
    }
}

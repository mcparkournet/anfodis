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

package net.mcparkour.anfodis.listener.mapper.properties;

import java.lang.annotation.Annotation;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.mcparkour.anfodis.mapper.ElementsMapperBuilder;
import net.mcparkour.anfodis.mapper.Mapper;
import net.mcparkour.anfodis.mapper.SingleElementMapperBuilder;

public class ListenerPropertiesMapper<P extends ListenerProperties<E>, D extends ListenerPropertiesData<E>, E, A extends Annotation> implements Mapper<Class<?>, P> {

    private Class<A> listenerAnnotationType;
    private Function<A, Class<? extends E>[]> listenedEventsExtractor;
    private Function<D, P> propertiesSupplier;
    private Supplier<D> propertiesDataSupplier;
    private BiConsumer<D, SingleElementMapperBuilder<Class<?>>> additional;

    public ListenerPropertiesMapper(Class<A> listenerAnnotationType, Function<A, Class<? extends E>[]> listenedEventsExtractor, Function<D, P> propertiesSupplier, Supplier<D> propertiesDataSupplier) {
        this(listenerAnnotationType, listenedEventsExtractor, propertiesSupplier, propertiesDataSupplier, (data, builder) -> {});
    }

    public ListenerPropertiesMapper(Class<A> listenerAnnotationType, Function<A, Class<? extends E>[]> listenedEventsExtractor, Function<D, P> propertiesSupplier, Supplier<D> propertiesDataSupplier, BiConsumer<D, SingleElementMapperBuilder<Class<?>>> additional) {
        this.listenerAnnotationType = listenerAnnotationType;
        this.listenedEventsExtractor = listenedEventsExtractor;
        this.propertiesSupplier = propertiesSupplier;
        this.propertiesDataSupplier = propertiesDataSupplier;
        this.additional = additional;
    }

    @Override
    public P map(Iterable<Class<?>> elements) {
        return new ElementsMapperBuilder<Class<?>, D>()
            .data(this.propertiesDataSupplier)
            .singleElement(data -> {
                SingleElementMapperBuilder<Class<?>> builder = new SingleElementMapperBuilder<Class<?>>()
                    .annotation(this.listenerAnnotationType, listener -> {
                        Class<? extends E>[] events = this.listenedEventsExtractor.apply(listener);
                        data.setListenedEvents(events);
                    });
                this.additional.accept(data, builder);
                return builder.build();
            })
            .build()
            .mapFirstOptional(elements)
            .map(this.propertiesSupplier)
            .orElseThrow();
    }
}

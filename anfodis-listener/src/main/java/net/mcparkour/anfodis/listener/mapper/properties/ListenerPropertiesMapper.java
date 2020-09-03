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
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.mcparkour.anfodis.mapper.ElementsMapper;
import net.mcparkour.anfodis.mapper.ElementsMapperBuilder;
import net.mcparkour.anfodis.mapper.Mapper;
import net.mcparkour.anfodis.mapper.MapperBuilderApplier;
import net.mcparkour.anfodis.mapper.SingleElementMapperBuilder;
import org.jetbrains.annotations.Nullable;

public class ListenerPropertiesMapper<P extends ListenerProperties<E>, D extends ListenerPropertiesData<E>, E, A extends Annotation>
    implements Mapper<Class<?>, P> {

    private final Function<D, P> propertiesSupplier;
    private final ElementsMapper<Class<?>, D> mapper;

    public ListenerPropertiesMapper(
        final Class<A> listenerAnnotationType,
        final Function<A, Class<? extends E>[]> listenedEventsExtractor,
        final Function<D, P> propertiesSupplier,
        final Supplier<D> propertiesDataSupplier
    ) {
        this(listenerAnnotationType, listenedEventsExtractor, propertiesSupplier, propertiesDataSupplier, null);
    }

    public ListenerPropertiesMapper(
        final Class<A> listenerAnnotationType,
        final Function<A, Class<? extends E>[]> listenedEventsExtractor,
        final Function<D, P> propertiesSupplier,
        final Supplier<D> propertiesDataSupplier,
        final @Nullable MapperBuilderApplier<Class<?>, D> additional
    ) {
        this.propertiesSupplier = propertiesSupplier;
        this.mapper = createMapper(propertiesDataSupplier, listenerAnnotationType, listenedEventsExtractor, additional);
    }

    private static <D extends ListenerPropertiesData<E>, E, A extends Annotation> ElementsMapper<Class<?>, D> createMapper(
        final Supplier<D> propertiesDataSupplier,
        final Class<A> listenerAnnotationType,
        final Function<A, Class<? extends E>[]> listenedEventsExtractor,
        final @Nullable MapperBuilderApplier<Class<?>, D> additional
    ) {
        MapperBuilderApplier<Class<?>, D> applier = (builder, data) -> builder
            .required(listenerAnnotationType, listener ->
                data.setListenedEvents(listenedEventsExtractor.apply(listener)));
        if (additional != null) {
            applier = applier.andThen(additional);
        }
        return new ElementsMapperBuilder<Class<?>, D>()
            .data(propertiesDataSupplier)
            .element(applier)
            .build();
    }

    @Override
    public P map(final Collection<Class<?>> elements) {
        D propertiesData = this.mapper.mapToSingle(elements);
        return this.propertiesSupplier.apply(propertiesData);
    }
}

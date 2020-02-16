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
import java.util.function.Function;
import java.util.function.Supplier;
import net.mcparkour.anfodis.mapper.Mapper;
import net.mcparkour.anfodis.mapper.ElementsMapperBuilder;
import net.mcparkour.anfodis.mapper.SingleElementMapperBuilder;

public class ListenerPropertiesMapper<A extends Annotation, P extends ListenerProperties<M, E>, M extends MappedListenerProperties<E>, E> implements Mapper<Class<?>, P> {

	private Class<A> listenerAnnotationClass;
	private Function<A, Class<? extends E>[]> listenedEventsSupplier;
	private Supplier<M> mappedPropertiesSupplier;
	private Function<M, P> mappedPropertiesToPropertiesMapper;

	public ListenerPropertiesMapper(Class<A> listenerAnnotationClass, Function<A, Class<? extends E>[]> listenedEventsSupplier, Supplier<M> mappedPropertiesSupplier, Function<M, P> mappedPropertiesToPropertiesMapper) {
		this.listenerAnnotationClass = listenerAnnotationClass;
		this.listenedEventsSupplier = listenedEventsSupplier;
		this.mappedPropertiesSupplier = mappedPropertiesSupplier;
		this.mappedPropertiesToPropertiesMapper = mappedPropertiesToPropertiesMapper;
	}

	@Override
	public P map(Iterable<Class<?>> elements) {
		return new ElementsMapperBuilder<Class<?>, M>()
			.data(this.mappedPropertiesSupplier)
			.singleElement(listener -> new SingleElementMapperBuilder<Class<?>>()
				.annotation(this.listenerAnnotationClass, listenerAnnotation -> listener.setListenedEvents(this.listenedEventsSupplier.apply(listenerAnnotation)))
				.build())
			.build()
			.mapFirstOptional(elements)
			.map(this.mappedPropertiesToPropertiesMapper)
			.orElseThrow(() -> new RuntimeException("Listener is null"));
	}
}

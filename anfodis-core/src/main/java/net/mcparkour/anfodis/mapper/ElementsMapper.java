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

package net.mcparkour.anfodis.mapper;

import java.lang.reflect.AnnotatedElement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class ElementsMapper<E extends AnnotatedElement, T> {

    private Supplier<T> dataSupplier;
    private List<MapperBuilderApplier<E, T>> mapperBuilderAppliers;

    public ElementsMapper(final Supplier<T> dataSupplier, final List<MapperBuilderApplier<E, T>> mapperBuilderAppliers) {
        this.dataSupplier = dataSupplier;
        this.mapperBuilderAppliers = mapperBuilderAppliers;
    }

    public T mapToSingle(final E[] elements) {
        List<E> elementsList = List.of(elements);
        return mapToSingle(elementsList);
    }

    public T mapToSingle(final Collection<E> elements) {
        T data = this.dataSupplier.get();
        for (final E element : elements) {
            map(element, data);
        }
        return data;
    }

    public Stream<T> mapToMultiple(final E[] elements) {
        List<E> elementsList = List.of(elements);
        return mapToMultiple(elementsList);
    }

    public Stream<T> mapToMultiple(final Collection<E> elements) {
        return elements.stream()
            .map(element -> {
                T data = this.dataSupplier.get();
                return map(element, data);
            })
            .filter(Optional::isPresent)
            .map(Optional::get);
    }

    private Optional<T> map(final E element, final T data) {
        for (final var mapperDataApplier : this.mapperBuilderAppliers) {
            SingleElementMapper<E> mapper = createMapper(data, mapperDataApplier);
            var requiredAnnotation = mapper.getRequiredAnnotation();
            if (requiredAnnotation.isAnnotationPresent(element)) {
                requiredAnnotation.accept(element);
                mapper.acceptElement(element);
                for (final var additionalAnnotation : mapper.getAdditionalAnnotations()) {
                    additionalAnnotation.accept(element);
                }
                return Optional.of(data);
            }
        }
        return Optional.empty();
    }

    private SingleElementMapper<E> createMapper(final T data, final MapperBuilderApplier<E, T> mapperBuilderApplier) {
        SingleElementMapperBuilder<E> builder = new SingleElementMapperBuilder<>();
        mapperBuilderApplier.apply(builder, data);
        return builder.build();
    }
}

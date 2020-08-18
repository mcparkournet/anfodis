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

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class ElementsMapper<E extends AnnotatedElement, T> {

    private Supplier<T> dataSupplier;
    private List<Function<T, SingleElementMapper<E>>> singleElementMappers;

    public ElementsMapper(Supplier<T> dataSupplier, List<Function<T, SingleElementMapper<E>>> singleElementMappers) {
        this.dataSupplier = dataSupplier;
        this.singleElementMappers = singleElementMappers;
    }

    public Optional<T> mapFirstOptional(Iterable<E> elements) {
        return Optional.of(mapFirst(elements));
    }

    public Optional<T> mapFirstOptional(E[] elements) {
        return Optional.of(mapFirst(elements));
    }

    public T mapFirst(E[] elements) {
        return mapFirst(List.of(elements));
    }

    public T mapFirst(Iterable<E> elements) {
        T data = this.dataSupplier.get();
        for (E element : elements) {
            for (Function<T, SingleElementMapper<E>> mapperDataApplier : this.singleElementMappers) {
                SingleElementMapper<E> mapper = mapperDataApplier.apply(data);
                for (AnnotationConsumer<? extends Annotation> annotationConsumer : mapper.getAnnotations()) {
                    if (annotationConsumer.isAnnotationPresent(element)) {
                        annotationConsumer.accept(element);
                        mapper.accept(element);
                    }
                }
            }
        }
        return data;
    }

    public List<T> map(E[] elements) {
        return map(List.of(elements));
    }

    public List<T> map(Iterable<E> elements) {
        List<T> list = new ArrayList<>(1);
        for (E element : elements) {
            T data = this.dataSupplier.get();
            for (Function<T, SingleElementMapper<E>> mapperDataApplier : this.singleElementMappers) {
                SingleElementMapper<E> mapper = mapperDataApplier.apply(data);
                List<AnnotationConsumer<? extends Annotation>> annotationConsumers = mapper.getAnnotations();
                boolean added = false;
                for (AnnotationConsumer<? extends Annotation> annotationConsumer : annotationConsumers) {
                    if (annotationConsumer.isAnnotationPresent(element)) {
                        annotationConsumer.accept(element);
                        mapper.accept(element);
                        if (!added) {
                            list.add(data);
                            added = true;
                        }
                    }
                }
            }
        }
        return list;
    }
}

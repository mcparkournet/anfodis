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
import org.jetbrains.annotations.Nullable;

public class ElementsMapper<E extends AnnotatedElement, T> {

	private Supplier<T> dataSupplier;
	private List<Function<T, SingleElementMapper<E>>> singleElementMappers = new ArrayList<>();

	public static <E extends AnnotatedElement, T> ElementsMapperBuilder<E, T> builder() {
		return new ElementsMapperBuilder<>();
	}

	public ElementsMapper(Supplier<T> dataSupplier, List<Function<T, SingleElementMapper<E>>> singleElementMappers) {
		this.dataSupplier = dataSupplier;
		this.singleElementMappers = singleElementMappers;
	}

	public Optional<T> mapFirstOptional(Iterable<E> elements) {
		return Optional.ofNullable(mapFirst(elements));
	}

	public Optional<T> mapFirstOptional(E[] elements) {
		return Optional.ofNullable(mapFirst(elements));
	}

	@Nullable
	public T mapFirst(Iterable<E> elements) {
		List<T> map = map(elements);
		if (map.isEmpty()) {
			return null;
		}
		return map.get(0);
	}

	@Nullable
	public T mapFirst(E[] elements) {
		List<T> map = map(elements);
		if (map.isEmpty()) {
			return null;
		}
		return map.get(0);
	}

	public List<T> map(E[] elements) {
		return map(List.of(elements));
	}

	public List<T> map(Iterable<E> elements) {
		List<T> list = new ArrayList<>();
		if (this.singleElementMappers.size() > 1) {
			T data = this.dataSupplier.get();
			for (E element : elements) {
				map(element, data);
			}
			list.add(data);
		} else {
			for (E element : elements) {
				T data = this.dataSupplier.get();
				map(element, data);
				list.add(data);
			}
		}
		return list;
	}

	private void map(E element, T data) {
		for (Function<T, SingleElementMapper<E>> mapperDataApplier : this.singleElementMappers) {
			SingleElementMapper<E> mapper = mapperDataApplier.apply(data);
			map(element, mapper);
		}
	}

	private void map(E element, SingleElementMapper<E> mapper) {
		for (AnnotationConsumer<? extends Annotation> annotationConsumer : mapper.getAnnotations()) {
			if (annotationConsumer.isAnnotationPresent(element)) {
				annotationConsumer.accept(element);
				mapper.accept(element);
			}
		}
	}
}

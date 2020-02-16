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
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

public class SingleElementMapperBuilder<E extends AnnotatedElement> {

	private List<AnnotationConsumer<? extends Annotation>> annotations = new ArrayList<>();
	@Nullable
	private Consumer<E> elementConsumer;

	public <A extends Annotation> SingleElementMapperBuilder<E> annotation(Class<A> annotation, Consumer<A> consumer) {
		AnnotationConsumer<A> annotationConsumer = new AnnotationConsumer<>(annotation, consumer);
		this.annotations.add(annotationConsumer);
		return this;
	}

	public <A extends Annotation> SingleElementMapperBuilder<E> annotation(Class<A> annotation) {
		AnnotationConsumer<A> annotationConsumer = new AnnotationConsumer<>(annotation, (a) -> {});
		this.annotations.add(annotationConsumer);
		return this;
	}

	public SingleElementMapperBuilder<E> elementConsumer(Consumer<E> elementConsumer) {
		this.elementConsumer = elementConsumer;
		return this;
	}

	public SingleElementMapper<E> build() {
		return new SingleElementMapper<>(this.annotations, this.elementConsumer);
	}
}

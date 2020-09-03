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
import java.util.List;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

public class SingleElementMapper<E extends AnnotatedElement> {

    private final AnnotationConsumer<? extends Annotation> requiredAnnotation;
    private final List<AnnotationConsumer<? extends Annotation>> additionalAnnotations;
    @Nullable
    private final Consumer<E> elementConsumer;

    public static <E extends AnnotatedElement> SingleElementMapperBuilder<E> builder() {
        return new SingleElementMapperBuilder<>();
    }

    public SingleElementMapper(final AnnotationConsumer<? extends Annotation> requiredAnnotation, final List<AnnotationConsumer<? extends Annotation>> additionalAnnotations, @Nullable final Consumer<E> elementConsumer) {
        this.requiredAnnotation = requiredAnnotation;
        this.additionalAnnotations = additionalAnnotations;
        this.elementConsumer = elementConsumer;
    }

    public void acceptElement(final E element) {
        if (this.elementConsumer != null) {
            this.elementConsumer.accept(element);
        }
    }

    public AnnotationConsumer<? extends Annotation> getRequiredAnnotation() {
        return this.requiredAnnotation;
    }

    public List<AnnotationConsumer<? extends Annotation>> getAdditionalAnnotations() {
        return List.copyOf(this.additionalAnnotations);
    }
}

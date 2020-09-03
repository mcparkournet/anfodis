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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ElementsMapperBuilder<E extends AnnotatedElement, T> {

    private Supplier<T> dataSupplier;
    private List<MapperBuilderApplier<E, T>> mapperBuilderAppliers;

    public ElementsMapperBuilder() {
        this.mapperBuilderAppliers = new ArrayList<>(0);
    }

    public ElementsMapperBuilder<E, T> data(final Supplier<T> dataSupplier) {
        this.dataSupplier = dataSupplier;
        return this;
    }

    public ElementsMapperBuilder<E, T> element(final MapperBuilderApplier<E, T> applier, final MapperBuilderApplier<E, T> additional) {
        return element(applier.andThen(additional));
    }

    public ElementsMapperBuilder<E, T> element(final MapperBuilderApplier<E, T> applier) {
        this.mapperBuilderAppliers.add(applier);
        return this;
    }

    public ElementsMapper<E, T> build() {
        Objects.requireNonNull(this.dataSupplier);
        return new ElementsMapper<>(this.dataSupplier, this.mapperBuilderAppliers);
    }
}

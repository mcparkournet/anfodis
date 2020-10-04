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

package net.mcparkour.anfodis.registry;

import java.lang.annotation.Annotation;
import net.mcparkour.anfodis.codec.context.TransformCodec;
import net.mcparkour.anfodis.codec.registry.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.handler.ContextHandler;
import net.mcparkour.anfodis.handler.RootContext;
import net.mcparkour.anfodis.mapper.Root;
import net.mcparkour.anfodis.mapper.RootMapper;

public abstract class AbstractRegistry<T extends Root<C>, C extends RootContext> implements Registry {

    private final Class<? extends Annotation> annotationClass;
    private final RootMapper<T, C> mapper;
    private final CodecRegistry<InjectionCodec<?>> injectionCodecRegistry;
    private final CodecRegistry<TransformCodec<C, ?>> transformCodecRegistry;

    public AbstractRegistry(
        final Class<? extends Annotation> annotation,
        final RootMapper<T, C> mapper,
        final CodecRegistry<InjectionCodec<?>> injectionCodecRegistry,
        final CodecRegistry<TransformCodec<C, ?>> transformCodecRegistry
    ) {
        this.annotationClass = annotation;
        this.mapper = mapper;
        this.injectionCodecRegistry = injectionCodecRegistry;
        this.transformCodecRegistry = transformCodecRegistry;
    }

    @Override
    public void register(final Class<?> annotatedClass) {
        if (!annotatedClass.isAnnotationPresent(this.annotationClass)) {
            throw new IllegalArgumentException("Class " + annotatedClass.getName() + " is not annotated with " + this.annotationClass.getName() + " annotation");
        }
        T root = this.mapper.map(annotatedClass);
        register(root);
    }

    public abstract void register(T root);

    public abstract void register(T root, ContextHandler<C> handler);

    protected CodecRegistry<InjectionCodec<?>> getInjectionCodecRegistry() {
        return this.injectionCodecRegistry;
    }

    protected CodecRegistry<TransformCodec<C, ?>> getTransformCodecRegistry() {
        return this.transformCodecRegistry;
    }
}

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

package net.mcparkour.anfodis.handler;

import net.mcparkour.anfodis.codec.context.TransformCodec;
import net.mcparkour.anfodis.codec.registry.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.mapper.Root;
import net.mcparkour.anfodis.mapper.transform.Transform;
import net.mcparkour.anfodis.mapper.executor.Executor;
import net.mcparkour.anfodis.mapper.injection.Injection;
import net.mcparkour.anfodis.result.Result;

public class RootHandler<T extends Root<C>, C extends RootContext> implements ContextHandler<C> {

    private final T root;
    private final CodecRegistry<InjectionCodec<?>> injectionCodecRegistry;
    private final CodecRegistry<TransformCodec<C, ?>> transformCodecRegistry;

    public RootHandler(
        final T root,
        final CodecRegistry<InjectionCodec<?>> injectionCodecRegistry,
        final CodecRegistry<TransformCodec<C, ?>> transformCodecRegistry
    ) {
        this.root = root;
        this.injectionCodecRegistry = injectionCodecRegistry;
        this.transformCodecRegistry = transformCodecRegistry;
    }

    @Override
    public void handle(final C context, final Object instance) {
        setInjections(instance);
        setTransforms(context, instance);
        execute(instance);
    }

    private void setInjections(final Object instance) {
        for (final Injection injection : this.root.getInjections()) {
            InjectionCodec<?> codec = injection.getCodec(this.injectionCodecRegistry);
            Object codecInjection = codec.getInjection();
            injection.setInjectionField(instance, codecInjection);
        }
    }

    private void setTransforms(final C context, final Object instance) {
        for (final Transform<C> transform : this.root.getTransforms()) {
            TransformCodec<C, ?> codec = transform.getCodec(this.transformCodecRegistry);
            Object transformed = codec.transform(context);
            transform.setTransformField(instance, transformed);
        }
    }

    private void execute(final Object instance) {
        Executor executor = this.root.getExecutor();
        executor.invokeBefore(instance);
        Object invokeResult = executor.invokeExecutor(instance);
        if (invokeResult instanceof Result) {
            Result result = (Result) invokeResult;
            result.onResult();
        }
        executor.invokeAfter(instance);
    }

    protected T getRoot() {
        return this.root;
    }
}

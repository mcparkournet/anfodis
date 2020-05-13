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

import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.mapper.Root;
import net.mcparkour.anfodis.mapper.executor.Executor;
import net.mcparkour.anfodis.mapper.injection.Injection;
import net.mcparkour.anfodis.result.Result;

public class RootHandler<T extends Root, C extends RootContext> implements ContextHandler<C> {

	private T root;
	private CodecRegistry<InjectionCodec<?>> injectionCodecRegistry;

	public RootHandler(T root, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry) {
		this.root = root;
		this.injectionCodecRegistry = injectionCodecRegistry;
	}

	@Override
	public void handle(C context, Object instance) {
		setInjections(instance);
		execute(instance);
	}

	private void setInjections(Object instance) {
		for (Injection injection : this.root.getInjections()) {
			InjectionCodec<?> codec = injection.getCodec(this.injectionCodecRegistry);
			Object codecInjection = codec.getInjection();
			injection.setInjectionField(instance, codecInjection);
		}
	}

	private void execute(Object instance) {
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

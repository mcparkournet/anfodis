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

package net.mcparkour.anfodis.listener.handler;

import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.listener.mapper.Listener;
import net.mcparkour.anfodis.mapper.executor.Executor;
import net.mcparkour.anfodis.mapper.injection.Injection;
import net.mcparkour.anfodis.result.Result;

public class ListenerExecutorHandler extends ListenerHandler {

	private Executor executor;
	private Object listenerInstance;

	public ListenerExecutorHandler(Class<?> eventType, Object event, Listener<?, ?, ?> listener, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, Executor executor) {
		super(eventType, event, listener, injectionCodecRegistry);
		this.executor = executor;
		this.listenerInstance = listener.createListener();
	}

	@Override
	public void handle() {
		fillInjections();
		setEvent();
		execute();
	}

	private void fillInjections() {
		Listener<?, ?, ?> listener = getListener();
		CodecRegistry<InjectionCodec<?>> injectionCodecRegistry = getInjectionCodecRegistry();
		for (Injection injection : listener.getInjections()) {
			InjectionCodec<?> codec = injection.getCodec(injectionCodecRegistry);
			Object codecInjection = codec.getInjection();
			injection.setValue(this.listenerInstance, codecInjection);
		}
	}

	private void setEvent() {
		Listener<?, ?, ?> listener = getListener();
		net.mcparkour.anfodis.listener.mapper.event.Event event = listener.getEvent();
		Object eventObject = getEvent();
		event.setValue(this.listenerInstance, eventObject);
	}

	private void execute() {
		this.executor.invokeBefore(this.listenerInstance);
		Object invokeResult = this.executor.invokeExecutor(this.listenerInstance);
		if (invokeResult instanceof Result) {
			Result result = (Result) invokeResult;
			result.onResult();
		}
		this.executor.invokeAfter(this.listenerInstance);
	}
}

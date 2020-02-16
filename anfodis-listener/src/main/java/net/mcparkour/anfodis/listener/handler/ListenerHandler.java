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
import net.mcparkour.anfodis.handler.Handler;
import net.mcparkour.anfodis.listener.mapper.Listener;
import net.mcparkour.anfodis.mapper.executor.Executor;

public class ListenerHandler implements Handler {

	private Class<?> eventType;
	private Object event;
	private Listener<?, ?, ?> listener;
	private CodecRegistry<InjectionCodec<?>> injectionCodecRegistry;

	public ListenerHandler(Class<?> eventType, Object event, Listener<?, ?, ?> listener, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry) {
		this.eventType = eventType;
		this.event = event;
		this.listener = listener;
		this.injectionCodecRegistry = injectionCodecRegistry;
	}

	@Override
	public void handle() {
		if (!this.eventType.isInstance(this.event)) {
			return;
		}
		Executor executor = this.listener.getExecutor();
		if (executor == null) {
			return;
		}
		Handler executorHandler = new ListenerExecutorHandler(this.eventType, this.event, this.listener, this.injectionCodecRegistry, executor);
		executorHandler.handle();
	}

	protected Object getEvent() {
		return this.event;
	}

	protected Listener<?, ?, ?> getListener() {
		return this.listener;
	}

	protected CodecRegistry<InjectionCodec<?>> getInjectionCodecRegistry() {
		return this.injectionCodecRegistry;
	}
}

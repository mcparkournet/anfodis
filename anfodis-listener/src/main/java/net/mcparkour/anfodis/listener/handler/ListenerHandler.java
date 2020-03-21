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
import net.mcparkour.anfodis.handler.RootHandler;
import net.mcparkour.anfodis.listener.mapper.Listener;
import net.mcparkour.anfodis.listener.mapper.event.Event;

public class ListenerHandler extends RootHandler<Listener<?>> {

	private Object event;

	public ListenerHandler(Listener<?> root, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, Object event) {
		super(root, injectionCodecRegistry);
		this.event = event;
	}

	@Override
	public void handle() {
		setEvent();
		super.handle();
	}

	private void setEvent() {
		Listener<?> listener = getRoot();
		Event event = listener.getEvent();
		Object listenerInstance = getInstance();
		event.setEventField(listenerInstance, this.event);
	}
}

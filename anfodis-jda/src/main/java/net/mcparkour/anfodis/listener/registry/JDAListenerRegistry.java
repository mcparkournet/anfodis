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

package net.mcparkour.anfodis.listener.registry;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.GenericEvent;
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.handler.Handler;
import net.mcparkour.anfodis.listener.annotation.properties.Listener;
import net.mcparkour.anfodis.listener.handler.ListenerHandler;
import net.mcparkour.anfodis.listener.mapper.JDAListener;
import net.mcparkour.anfodis.listener.mapper.JDAListenerMapper;
import net.mcparkour.anfodis.listener.mapper.properties.JDAListenerProperties;
import net.mcparkour.common.reflection.Casts;

public class JDAListenerRegistry extends AbstractListenerRegistry<JDAListener, JDADirectListener<? extends GenericEvent>> {

	private static final JDAListenerMapper LISTENER_MAPPER = new JDAListenerMapper();

	private JDA jda;

	public JDAListenerRegistry(CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, JDA jda) {
		super(Listener.class, LISTENER_MAPPER, injectionCodecRegistry);
		this.jda = jda;
	}

	@Override
	protected void register(JDAListener root) {
		CodecRegistry<InjectionCodec<?>> injectionCodecRegistry = getInjectionCodecRegistry();
		registerDirect(root, event -> {
			Handler handler = new ListenerHandler(root, injectionCodecRegistry, event);
			handler.handle();
		});
	}

	@Override
	public void registerDirect(JDAListener root, JDADirectListener<? extends GenericEvent> directHandler) {
		JDAListenerProperties properties = root.getListenerProperties();
		Iterable<Class<? extends GenericEvent>> eventTypes = properties.getListenedEvents();
		for (Class<? extends GenericEvent> eventType : eventTypes) {
			JDADirectListener<? extends GenericEvent> listener = event -> {
				if (eventType.isInstance(event)) {
					Casts.sneakyCast(event, directHandler);
				}
			};
			this.jda.addEventListener(listener);
		}
	}
}

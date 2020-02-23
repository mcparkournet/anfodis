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

import com.velocitypowered.api.event.EventHandler;
import com.velocitypowered.api.event.EventManager;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.proxy.ProxyServer;
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.handler.Handler;
import net.mcparkour.anfodis.listener.annotation.properties.Listener;
import net.mcparkour.anfodis.listener.handler.ListenerHandler;
import net.mcparkour.anfodis.listener.mapper.VelocityListener;
import net.mcparkour.anfodis.listener.mapper.VelocityListenerMapper;
import net.mcparkour.anfodis.listener.mapper.properties.VelocityListenerProperties;

public class VelocityListenerRegistry extends AbstractListenerRegistry<VelocityListener> {

	private static final VelocityListenerMapper LISTENER_MAPPER = new VelocityListenerMapper();

	private EventManager eventManager;
	private Object plugin;

	public VelocityListenerRegistry(CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, ProxyServer server, Object plugin) {
		super(Listener.class, LISTENER_MAPPER, injectionCodecRegistry);
		this.eventManager = server.getEventManager();
		this.plugin = plugin;
	}

	@Override
	protected void register(VelocityListener root) {
		CodecRegistry<InjectionCodec<?>> injectionCodecRegistry = getInjectionCodecRegistry();
		VelocityListenerProperties properties = root.getListenerProperties();
		PostOrder priority = properties.getPriority();
		Iterable<Class<?>> eventTypes = properties.getListenedEvents();
		for (Class<?> eventType : eventTypes) {
			register(injectionCodecRegistry, root, eventType, priority);
		}
	}

	private <E> void register(CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, VelocityListener listener, Class<E> eventType, PostOrder priority) {
		EventHandler<E> eventHandler = event -> {
			Handler handler = new ListenerHandler(eventType, event, listener, injectionCodecRegistry);
			handler.handle();
		};
		this.eventManager.register(this.plugin, eventType, priority, eventHandler);
	}
}

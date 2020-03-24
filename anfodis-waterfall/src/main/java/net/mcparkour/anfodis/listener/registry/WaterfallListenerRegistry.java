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

import java.lang.reflect.Method;
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.handler.ContextHandler;
import net.mcparkour.anfodis.listener.annotation.properties.Listener;
import net.mcparkour.anfodis.listener.handler.ListenerContext;
import net.mcparkour.anfodis.listener.mapper.WaterfallListener;
import net.mcparkour.anfodis.listener.mapper.WaterfallListenerMapper;
import net.mcparkour.anfodis.listener.mapper.properties.WaterfallListenerProperties;
import net.mcparkour.common.reflection.Reflections;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.event.EventPriority;

public class WaterfallListenerRegistry extends AbstractListenerRegistry<WaterfallListener, ListenerContext<? extends Event>> {

	private static final WaterfallListenerMapper LISTENER_MAPPER = new WaterfallListenerMapper();

	private Plugin plugin;
	private ReflectedPluginManager reflectedPluginManager;

	public WaterfallListenerRegistry(CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, Plugin plugin) {
		super(Listener.class, LISTENER_MAPPER, injectionCodecRegistry);
		this.plugin = plugin;
		this.reflectedPluginManager = createReflectedPluginManager(plugin);
	}

	private static ReflectedPluginManager createReflectedPluginManager(Plugin plugin) {
		ProxyServer server = plugin.getProxy();
		PluginManager pluginManager = server.getPluginManager();
		return new ReflectedPluginManager(pluginManager);
	}

	@Override
	public void register(WaterfallListener root, ContextHandler<ListenerContext<? extends Event>> handler) {
		WaterfallListenerProperties properties = root.getProperties();
		byte priority = properties.getPriority();
		Iterable<Class<? extends Event>> eventTypes = properties.getListenedEvents();
		for (Class<? extends Event> eventType : eventTypes) {
			sneakyRegister(eventType, priority, handler);
		}
	}

	@SuppressWarnings("unchecked")
	private <E extends Event> void sneakyRegister(Class<? extends Event> eventType, byte priority, ContextHandler<? extends ListenerContext<? extends Event>> handler) {
		ContextHandler<ListenerContext<E>> castedHandler = (ContextHandler<ListenerContext<E>>) handler;
		Class<E> castedEventType = (Class<E>) eventType;
		register(castedEventType, priority, castedHandler);
	}

	public <E extends Event> void register(Class<E> eventType, ContextHandler<ListenerContext<E>> handler) {
		register(eventType, EventPriority.NORMAL, handler);
	}

	public <E extends Event> void register(Class<E> eventType, byte priority, ContextHandler<ListenerContext<E>> handler) {
		EventExecutor<E> eventExecutor = createEventExecutor(eventType, handler);
		Method listenMethod = Reflections.getMethod(EventExecutor.class, "execute", eventType);
		this.reflectedPluginManager.registerListener(this.plugin, eventExecutor, listenMethod, eventType, priority);
	}

	private <E extends Event> EventExecutor<E> createEventExecutor(Class<E> eventType, ContextHandler<ListenerContext<E>> handler) {
		return event -> {
			if (eventType.isInstance(event)) {
				ListenerContext<E> context = new ListenerContext<>(event);
				handler.handle(context);
			}
		};
	}
}

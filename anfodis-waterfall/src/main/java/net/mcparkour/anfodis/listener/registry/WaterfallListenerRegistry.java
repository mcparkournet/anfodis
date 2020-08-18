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

    public WaterfallListenerRegistry(final CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, final Plugin plugin) {
        super(Listener.class, LISTENER_MAPPER, injectionCodecRegistry);
        this.plugin = plugin;
        this.reflectedPluginManager = createReflectedPluginManager(plugin);
    }

    private static ReflectedPluginManager createReflectedPluginManager(final Plugin plugin) {
        ProxyServer server = plugin.getProxy();
        PluginManager pluginManager = server.getPluginManager();
        return new ReflectedPluginManager(pluginManager);
    }

    @Override
    public void register(final WaterfallListener root, final ContextHandler<ListenerContext<? extends Event>> handler) {
        WaterfallListenerProperties properties = root.getProperties();
        byte priority = properties.getPriority();
        Iterable<Class<? extends Event>> eventTypes = properties.getListenedEvents();
        for (final Class<? extends Event> eventType : eventTypes) {
            register(root, eventType, priority, handler);
        }
    }

    @SuppressWarnings("unchecked")
    private <E extends Event> void register(final WaterfallListener listener, final Class<? extends Event> eventType, final byte priority, final ContextHandler<ListenerContext<? extends Event>> handler) {
        Class<E> castedEventType = (Class<E>) eventType;
        WaterfallEventListener<E> eventListener = event -> {
            ListenerContext<E> context = new ListenerContext<>(event);
            Object listenerInstance = listener.createInstance();
            handler.handle(context, listenerInstance);
        };
        register(castedEventType, priority, eventListener);
    }

    public <E extends Event> void register(final Class<E> eventType, final WaterfallEventListener<E> listener) {
        register(eventType, EventPriority.NORMAL, listener);
    }

    public <E extends Event> void register(final Class<E> eventType, final byte priority, final WaterfallEventListener<E> listener) {
        EventExecutor<E> eventExecutor = createEventExecutor(eventType, listener);
        Method listenMethod = Reflections.getMethod(EventExecutor.class, "execute", eventType);
        this.reflectedPluginManager.registerListener(this.plugin, eventExecutor, listenMethod, eventType, priority);
    }

    private <E extends Event> EventExecutor<E> createEventExecutor(final Class<E> eventType, final WaterfallEventListener<E> listener) {
        return event -> {
            if (eventType.isInstance(event)) {
                listener.listen(event);
            }
        };
    }
}

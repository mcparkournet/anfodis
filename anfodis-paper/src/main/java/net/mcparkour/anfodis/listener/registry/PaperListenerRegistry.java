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

import net.mcparkour.anfodis.codec.context.TransformCodec;
import net.mcparkour.anfodis.codec.registry.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.handler.ContextHandler;
import net.mcparkour.anfodis.listener.annotation.properties.Listener;
import net.mcparkour.anfodis.listener.context.PaperListenerContext;
import net.mcparkour.anfodis.listener.handler.ListenerContext;
import net.mcparkour.anfodis.listener.mapper.PaperListener;
import net.mcparkour.anfodis.listener.mapper.PaperListenerMapper;
import net.mcparkour.anfodis.listener.mapper.properties.PaperListenerProperties;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class PaperListenerRegistry extends AbstractListenerRegistry<PaperListener, PaperListenerContext, Event> {

    private static final PaperListenerMapper LISTENER_MAPPER = new PaperListenerMapper();
    private static final org.bukkit.event.Listener EMPTY_LISTENER = new org.bukkit.event.Listener() {};

    private final Plugin plugin;
    private final PluginManager pluginManager;

    public PaperListenerRegistry(final CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, final CodecRegistry<TransformCodec<PaperListenerContext, ?>> transformCodecRegistry, final Plugin plugin) {
        super(Listener.class, LISTENER_MAPPER, injectionCodecRegistry, transformCodecRegistry);
        this.plugin = plugin;
        Server server = plugin.getServer();
        this.pluginManager = server.getPluginManager();
    }

    @Override
    public void register(final PaperListener root, final ContextHandler<PaperListenerContext> handler) {
        PaperListenerProperties properties = root.getProperties();
        EventPriority priority = properties.getPriority();
        boolean ignoreCancelled = properties.isIgnoreCancelled();
        Iterable<Class<? extends Event>> eventTypes = properties.getListenedEvents();
        for (final Class<? extends Event> eventType : eventTypes) {
            register(root, eventType, priority, ignoreCancelled, handler);
        }
    }

    @SuppressWarnings("unchecked")
    private <E extends Event> void register(final PaperListener listener, final Class<? extends Event> eventType, final EventPriority priority, final boolean ignoreCancelled, final ContextHandler<PaperListenerContext> handler) {
        Class<E> castedEventType = (Class<E>) eventType;
        PaperEventListener<E> eventListener = event -> {
            PaperListenerContext context = new PaperListenerContext(event);
            Object listenerInstance = listener.createInstance();
            handler.handle(context, listenerInstance);
        };
        register(castedEventType, priority, ignoreCancelled, eventListener);
    }

    public <E extends Event> void register(final Class<E> eventType, final PaperEventListener<E> listener) {
        register(eventType, EventPriority.NORMAL, listener);
    }

    public <E extends Event> void register(final Class<E> eventType, final EventPriority priority, final PaperEventListener<E> listener) {
        register(eventType, priority, false, listener);
    }

    public <E extends Event> void register(final Class<E> eventType, final EventPriority priority, final boolean ignoreCancelled, final PaperEventListener<E> listener) {
        EventExecutor executor = createEventExecutor(eventType, listener);
        this.pluginManager.registerEvent(eventType, EMPTY_LISTENER, priority, executor, this.plugin, ignoreCancelled);
    }

    @SuppressWarnings("unchecked")
    private <E extends Event> EventExecutor createEventExecutor(final Class<E> eventType, final PaperEventListener<E> eventListener) {
        return (listener, event) -> {
            if (eventType.isInstance(event)) {
                E castedEvent = (E) event;
                eventListener.listen(castedEvent);
            }
        };
    }
}

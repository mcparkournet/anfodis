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
import net.mcparkour.anfodis.codec.context.TransformCodec;
import net.mcparkour.anfodis.codec.registry.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.handler.ContextHandler;
import net.mcparkour.anfodis.listener.annotation.properties.Listener;
import net.mcparkour.anfodis.listener.context.VelocityListenerContext;
import net.mcparkour.anfodis.listener.handler.ListenerContext;
import net.mcparkour.anfodis.listener.mapper.VelocityListener;
import net.mcparkour.anfodis.listener.mapper.VelocityListenerMapper;
import net.mcparkour.anfodis.listener.mapper.properties.VelocityListenerProperties;

public class VelocityListenerRegistry extends AbstractListenerRegistry<VelocityListener, VelocityListenerContext, Object> {

    private static final VelocityListenerMapper LISTENER_MAPPER = new VelocityListenerMapper();

    private final EventManager eventManager;
    private final Object plugin;

    public VelocityListenerRegistry(
        final CodecRegistry<InjectionCodec<?>> injectionCodecRegistry,
        final CodecRegistry<TransformCodec<VelocityListenerContext, ?>> transformCodecRegistry,
        final ProxyServer server,
        final Object plugin
    ) {
        this(injectionCodecRegistry, transformCodecRegistry, server.getEventManager(), plugin);
    }

    public VelocityListenerRegistry(
        final CodecRegistry<InjectionCodec<?>> injectionCodecRegistry,
        final CodecRegistry<TransformCodec<VelocityListenerContext, ?>> transformCodecRegistry,
        final EventManager eventManager,
        final Object plugin
    ) {
        super(Listener.class, LISTENER_MAPPER, injectionCodecRegistry, transformCodecRegistry);
        this.eventManager = eventManager;
        this.plugin = plugin;
    }

    @Override
    public void register(final VelocityListener root, final ContextHandler<VelocityListenerContext> handler) {
        VelocityListenerProperties properties = root.getProperties();
        PostOrder priority = properties.getPriority();
        Iterable<Class<?>> eventTypes = properties.getListenedEvents();
        for (final Class<?> eventType : eventTypes) {
            register(root, eventType, priority, handler);
        }
    }

    @SuppressWarnings("unchecked")
    private <E> void register(
        final VelocityListener listener,
        final Class<?> eventType,
        final PostOrder priority,
        final ContextHandler<VelocityListenerContext> handler
    ) {
        Class<E> castedEventType = (Class<E>) eventType;
        VelocityEventListener<E> eventListener = event -> {
            VelocityListenerContext context = new VelocityListenerContext(event);
            Object listenerInstance = listener.createInstance();
            handler.handle(context, listenerInstance);
        };
        register(castedEventType, priority, eventListener);
    }

    public <E> void register(final Class<E> eventType, final VelocityEventListener<E> listener) {
        register(eventType, PostOrder.NORMAL, listener);
    }

    public <E> void register(final Class<E> eventType, final PostOrder priority, final VelocityEventListener<E> listener) {
        EventHandler<E> eventHandler = createEventHandler(eventType, listener);
        this.eventManager.register(this.plugin, eventType, priority, eventHandler);
    }

    private <E> EventHandler<E> createEventHandler(final Class<E> eventType, final VelocityEventListener<E> listener) {
        return event -> {
            if (eventType.isInstance(event)) {
                listener.listen(event);
            }
        };
    }
}

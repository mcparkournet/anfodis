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
import net.dv8tion.jda.api.hooks.EventListener;
import net.mcparkour.anfodis.codec.context.TransformCodec;
import net.mcparkour.anfodis.codec.registry.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.handler.ContextHandler;
import net.mcparkour.anfodis.listener.annotation.properties.Listener;
import net.mcparkour.anfodis.listener.context.JDAListenerContext;
import net.mcparkour.anfodis.listener.handler.ListenerContext;
import net.mcparkour.anfodis.listener.mapper.JDAListener;
import net.mcparkour.anfodis.listener.mapper.JDAListenerMapper;
import net.mcparkour.anfodis.listener.mapper.properties.JDAListenerProperties;

public class JDAListenerRegistry
    extends AbstractListenerRegistry<JDAListener, JDAListenerContext, GenericEvent> {

    private static final JDAListenerMapper LISTENER_MAPPER = new JDAListenerMapper();

    private final JDA jda;

    public JDAListenerRegistry(
        final CodecRegistry<InjectionCodec<?>> injectionCodecRegistry,
        final CodecRegistry<TransformCodec<JDAListenerContext, ?>> transformCodecRegistry,
        final JDA jda
    ) {
        super(Listener.class, LISTENER_MAPPER, injectionCodecRegistry, transformCodecRegistry);
        this.jda = jda;
    }

    @Override
    public void register(final JDAListener root, final ContextHandler<JDAListenerContext> handler) {
        JDAListenerProperties properties = root.getProperties();
        Iterable<Class<? extends GenericEvent>> eventTypes = properties.getListenedEvents();
        for (final Class<? extends GenericEvent> eventType : eventTypes) {
            register(root, eventType, handler);
        }
    }

    @SuppressWarnings("unchecked")
    private <E extends GenericEvent> void register(final JDAListener listener, final Class<? extends GenericEvent> eventType, final ContextHandler<JDAListenerContext> handler) {
        Class<E> castedEventType = (Class<E>) eventType;
        JDAEventListener<E> eventListener = event -> {
            JDAListenerContext context = new JDAListenerContext(event);
            Object listenerInstance = listener.createInstance();
            handler.handle(context, listenerInstance);
        };
        register(castedEventType, eventListener);
    }

    public <E extends GenericEvent> void register(final Class<E> eventType, final JDAEventListener<E> listener) {
        EventListener eventListener = createEventListener(eventType, listener);
        this.jda.addEventListener(eventListener);
    }

    @SuppressWarnings("unchecked")
    private <E extends GenericEvent> EventListener createEventListener(final Class<E> eventType, final JDAEventListener<E> listener) {
        return event -> {
            if (eventType.isInstance(event)) {
                E castedEvent = (E) event;
                listener.listen(castedEvent);
            }
        };
    }
}

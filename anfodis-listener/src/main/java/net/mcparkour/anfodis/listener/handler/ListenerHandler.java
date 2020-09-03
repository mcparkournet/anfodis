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

import net.mcparkour.anfodis.codec.registry.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.handler.RootHandler;
import net.mcparkour.anfodis.listener.mapper.Listener;
import net.mcparkour.anfodis.listener.mapper.context.Context;

public class ListenerHandler<T extends Listener<?, ?>, C extends ListenerContext<? extends E>, E> extends RootHandler<T, C> {

    public ListenerHandler(final T root, final CodecRegistry<InjectionCodec<?>> injectionCodecRegistry) {
        super(root, injectionCodecRegistry);
    }

    @Override
    public void handle(final C context, final Object instance) {
        setContext(context, instance);
        super.handle(context, instance);
    }

    private void setContext(final C context, final Object listenerInstance) {
        Listener<?, ?> listener = getRoot();
        Context listenerContext = listener.getContext();
        E event = context.getEvent();
        listenerContext.setEventField(listenerInstance, event);
    }
}

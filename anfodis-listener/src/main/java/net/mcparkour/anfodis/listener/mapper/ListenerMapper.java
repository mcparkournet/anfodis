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

package net.mcparkour.anfodis.listener.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import net.mcparkour.anfodis.listener.mapper.context.Context;
import net.mcparkour.anfodis.listener.mapper.context.ContextMapper;
import net.mcparkour.anfodis.listener.mapper.properties.ListenerProperties;
import net.mcparkour.anfodis.listener.mapper.properties.ListenerPropertiesMapper;
import net.mcparkour.anfodis.mapper.RootMapper;
import net.mcparkour.anfodis.mapper.executor.Executor;
import net.mcparkour.anfodis.mapper.injection.Injection;

public class ListenerMapper<T extends Listener<C, P>, C extends Context, P extends ListenerProperties<?>> implements RootMapper<T> {

    private ContextMapper<C, ?> contextMapper;
    private ListenerPropertiesMapper<P, ?, ?, ?> propertiesMapper;
    private ListenerMerger<T, C, P> listenerMerger;

    public ListenerMapper(final ContextMapper<C, ?> contextMapper, final ListenerPropertiesMapper<P, ?, ?, ?> propertiesMapper, final ListenerMerger<T, C, P> listenerMerger) {
        this.contextMapper = contextMapper;
        this.propertiesMapper = propertiesMapper;
        this.listenerMerger = listenerMerger;
    }

    @Override
    public T map(final Class<?> annotatedClass) {
        Field[] fields = annotatedClass.getDeclaredFields();
        Method[] methods = annotatedClass.getDeclaredMethods();
        Constructor<?> constructor = getConstructor(annotatedClass);
        C context = getContext(fields);
        P properties = getProperties(annotatedClass);
        List<Injection> injections = getInjections(fields);
        Executor executor = getExecutor(methods);
        return this.listenerMerger.merge(constructor, injections, executor, context, properties);
    }

    private C getContext(final Field[] fields) {
        return this.contextMapper.map(fields);
    }

    private P getProperties(final Class<?> listenerClass) {
        return this.propertiesMapper.map(listenerClass);
    }
}

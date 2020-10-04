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
import net.mcparkour.anfodis.listener.handler.ListenerContext;
import net.mcparkour.anfodis.listener.mapper.properties.ListenerProperties;
import net.mcparkour.anfodis.listener.mapper.properties.ListenerPropertiesMapper;
import net.mcparkour.anfodis.mapper.AbstractRootMapper;
import net.mcparkour.anfodis.mapper.RootMapper;
import net.mcparkour.anfodis.mapper.executor.Executor;
import net.mcparkour.anfodis.mapper.injection.Injection;
import net.mcparkour.anfodis.mapper.transform.Transform;

public class ListenerMapper<T extends Listener<P, C, E>, P extends ListenerProperties<E>, C extends ListenerContext<E>, E>
    extends AbstractRootMapper<T, C> {

    private final ListenerPropertiesMapper<P, ?, ?, ?> propertiesMapper;
    private final ListenerMerger<T, P, C, E> listenerMerger;

    public ListenerMapper(final ListenerPropertiesMapper<P, ?, ?, ?> propertiesMapper, final ListenerMerger<T, P, C, E> listenerMerger) {
        this.propertiesMapper = propertiesMapper;
        this.listenerMerger = listenerMerger;
    }

    @Override
    public T map(final Class<?> annotatedClass) {
        Field[] fields = annotatedClass.getDeclaredFields();
        Method[] methods = annotatedClass.getDeclaredMethods();
        Constructor<?> constructor = getConstructor(annotatedClass);
        P properties = getProperties(annotatedClass);
        List<Injection> injections = getInjections(fields);
        List<Transform<C>> transforms = getTransforms(fields);
        Executor executor = getExecutor(methods);
        return this.listenerMerger.merge(constructor, injections, transforms, executor, properties);
    }

    private P getProperties(final Class<?> listenerClass) {
        return this.propertiesMapper.map(listenerClass);
    }
}

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
import net.mcparkour.anfodis.listener.mapper.event.Event;
import net.mcparkour.anfodis.listener.mapper.event.EventMapper;
import net.mcparkour.anfodis.listener.mapper.properties.ListenerProperties;
import net.mcparkour.anfodis.listener.mapper.properties.ListenerPropertiesMapper;
import net.mcparkour.anfodis.mapper.ClassMapper;
import net.mcparkour.anfodis.mapper.executor.Executor;
import net.mcparkour.anfodis.mapper.executor.ExecutorMapper;
import net.mcparkour.anfodis.mapper.injection.Injection;
import net.mcparkour.anfodis.mapper.injection.InjectionMapper;
import net.mcparkour.common.reflection.Reflections;

public class ListenerMapper<L extends Listener<P>, P extends ListenerProperties<?, ?>> implements ClassMapper<L> {

	private ListenerMerger<L, P> listenerMerger;
	private ListenerPropertiesMapper<?, P, ?, ?> listenerPropertiesMapper;

	public ListenerMapper(ListenerMerger<L, P> listenerMerger, ListenerPropertiesMapper<?, P, ?, ?> listenerPropertiesMapper) {
		this.listenerMerger = listenerMerger;
		this.listenerPropertiesMapper = listenerPropertiesMapper;
	}

	@Override
	public L map(Class<?> annotatedClass) {
		Field[] fields = annotatedClass.getDeclaredFields();
		Method[] methods = annotatedClass.getDeclaredMethods();
		Constructor<?> constructor = Reflections.getSerializationConstructor(annotatedClass);
		P listenerProperties = getListenerProperties(annotatedClass);
		Event event = getEvent(fields);
		List<Injection> injections = getInjections(fields);
		Executor executor = getExecutor(methods);
		return this.listenerMerger.merge(constructor, listenerProperties, event, injections, executor);
	}

	private P getListenerProperties(Class<?> listenerClass) {
		return this.listenerPropertiesMapper.map(listenerClass);
	}

	private Event getEvent(Field[] fields) {
		EventMapper eventMapper = new EventMapper();
		return eventMapper.map(fields);
	}

	private List<Injection> getInjections(Field[] fields) {
		InjectionMapper injectionMapper = new InjectionMapper();
		return injectionMapper.map(fields);
	}

	private Executor getExecutor(Method[] methods) {
		ExecutorMapper executorMapper = new ExecutorMapper();
		return executorMapper.map(methods);
	}
}

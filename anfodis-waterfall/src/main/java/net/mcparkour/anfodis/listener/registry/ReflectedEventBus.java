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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import net.mcparkour.common.reflection.Reflections;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.event.EventBus;

public class ReflectedEventBus {

	private EventBus eventBus;
	private Map<Class<?>, Map<Byte, Map<Object, Method[]>>> byListenerAndPriority;
	private Lock lock;

	public ReflectedEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
		this.byListenerAndPriority = getFieldValue("byListenerAndPriority", eventBus);
		this.lock = getFieldValue("lock", eventBus);
	}

	@SuppressWarnings("unchecked")
	private static <T> T getFieldValue(String fieldName, EventBus eventBus) {
		Field field = Reflections.getField(EventBus.class, fieldName);
		Object fieldValue = Reflections.getFieldValue(field, eventBus);
		Objects.requireNonNull(fieldValue, "Value of field " + fieldName + " is null");
		return (T) fieldValue;
	}

	public void register(Object listener, Method listenMethod, Class<? extends Event> listenedEventType, byte priority) {
		this.lock.lock();
		try {
			Map<Byte, Map<Object, Method[]>> priorityListenerMap = this.byListenerAndPriority.computeIfAbsent(listenedEventType, key -> new HashMap<>(1));
			Map<Object, Method[]> listenerMethodsMap = priorityListenerMap.computeIfAbsent(priority, key -> new HashMap<>(1));
			Method[] methods = {listenMethod};
			listenerMethodsMap.put(listener, methods);
			bakeHandlers(listenedEventType);
		} finally {
			this.lock.unlock();
		}
	}

	private void bakeHandlers(Class<? extends Event> eventType) {
		Method method = Reflections.getMethod(EventBus.class, "bakeHandlers", Class.class);
		Reflections.invokeMethod(method, this.eventBus, eventType);
	}
}

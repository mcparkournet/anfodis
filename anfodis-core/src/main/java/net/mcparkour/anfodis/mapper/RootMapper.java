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

package net.mcparkour.anfodis.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import net.mcparkour.anfodis.mapper.executor.Executor;
import net.mcparkour.anfodis.mapper.executor.ExecutorMapper;
import net.mcparkour.anfodis.mapper.injection.Injection;
import net.mcparkour.anfodis.mapper.injection.InjectionMapper;
import net.mcparkour.common.reflection.Reflections;

public interface RootMapper<T extends Root> {

	InjectionMapper INJECTION_MAPPER = new InjectionMapper();
	ExecutorMapper EXECUTOR_MAPPER = new ExecutorMapper();

	T map(Class<?> annotatedClass);

	default Constructor<?> getConstructor(Class<?> type) {
		return Reflections.getSerializationConstructor(type);
	}

	default List<Injection> getInjections(Field[] fields) {
		return INJECTION_MAPPER.map(fields);
	}

	default Executor getExecutor(Method[] methods) {
		return EXECUTOR_MAPPER.map(methods);
	}
}
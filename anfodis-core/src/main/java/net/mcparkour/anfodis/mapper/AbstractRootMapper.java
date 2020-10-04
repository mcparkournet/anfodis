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
import net.mcparkour.anfodis.handler.RootContext;
import net.mcparkour.anfodis.mapper.executor.Executor;
import net.mcparkour.anfodis.mapper.executor.ExecutorMapper;
import net.mcparkour.anfodis.mapper.injection.Injection;
import net.mcparkour.anfodis.mapper.injection.InjectionMapper;
import net.mcparkour.anfodis.mapper.transform.Transform;
import net.mcparkour.anfodis.mapper.transform.TransformMapper;
import net.mcparkour.common.reflection.Reflections;

public abstract class AbstractRootMapper<T extends Root<C>, C extends RootContext> implements RootMapper<T, C> {

    private static final InjectionMapper INJECTION_MAPPER = new InjectionMapper();
    private static final ExecutorMapper EXECUTOR_MAPPER = new ExecutorMapper();

    private final TransformMapper<C> transformMapper;

    public AbstractRootMapper() {
        this.transformMapper = new TransformMapper<>();
    }

    public Constructor<?> getConstructor(final Class<?> type) {
        return Reflections.getSerializationConstructor(type);
    }

    public List<Injection> getInjections(final Field[] fields) {
        return INJECTION_MAPPER.map(fields);
    }

    public List<Transform<C>> getTransforms(final Field[] fields) {
        return this.transformMapper.map(fields);
    }

    public Executor getExecutor(final Method[] methods) {
        return EXECUTOR_MAPPER.map(methods);
    }
}

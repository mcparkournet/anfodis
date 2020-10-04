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
import java.util.List;
import net.mcparkour.anfodis.handler.RootContext;
import net.mcparkour.anfodis.mapper.transform.Transform;
import net.mcparkour.anfodis.mapper.executor.Executor;
import net.mcparkour.anfodis.mapper.injection.Injection;
import net.mcparkour.common.reflection.Reflections;

public class Root<C extends RootContext> {

    private final Constructor<?> constructor;
    private final List<Injection> injections;
    private final List<Transform<C>> transforms;
    private final Executor executor;

    public Root(
        final Constructor<?> constructor,
        final List<Injection> injections,
        final List<Transform<C>> transforms,
        final Executor executor
    ) {
        this.constructor = constructor;
        this.injections = injections;
        this.transforms = transforms;
        this.executor = executor;
    }

    public Object createInstance() {
        return Reflections.newInstance(this.constructor);
    }

    public List<Injection> getInjections() {
        return this.injections;
    }

    public List<Transform<C>> getTransforms() {
        return this.transforms;
    }

    public Executor getExecutor() {
        return this.executor;
    }
}

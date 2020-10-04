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

package net.mcparkour.anfodis.mapper.executor;

import java.lang.reflect.Method;
import java.util.Objects;
import net.mcparkour.common.reflection.Reflections;
import org.jetbrains.annotations.Nullable;

public class Executor {

    private final @Nullable Method beforeMethod;
    private final @Nullable Method executorMethod;
    private final @Nullable Method afterMethod;

    public Executor(final ExecutorData executorData) {
        this.beforeMethod = executorData.getBeforeMethod();
        this.executorMethod = executorData.getExecutorMethod();
        this.afterMethod = executorData.getAfterMethod();
    }

    public void invokeBefore(final Object instance) {
        if (this.beforeMethod == null) {
            return;
        }
        Reflections.invokeMethod(this.beforeMethod, instance);
    }

    public boolean hasExecutor() {
        return this.executorMethod != null;
    }

    public @Nullable Object invokeExecutor(final Object instance) {
        Objects.requireNonNull(this.executorMethod, "Executor method is null");
        return Reflections.invokeMethod(this.executorMethod, instance);
    }

    public void invokeAfter(final Object instance) {
        if (this.afterMethod == null) {
            return;
        }
        Reflections.invokeMethod(this.afterMethod, instance);
    }
}

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

import java.lang.reflect.Method;
import java.util.Objects;

public class MethodData {

    private Method beforeMethod;
    private Method executorMethod;
    private Method afterMethod;

    public MethodData() {}

    public MethodData(final Method beforeMethod, final Method executorMethod, final Method afterMethod) {
        this.beforeMethod = beforeMethod;
        this.executorMethod = executorMethod;
        this.afterMethod = afterMethod;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        MethodData that = (MethodData) object;
        return Objects.equals(this.beforeMethod, that.beforeMethod) &&
            Objects.equals(this.executorMethod, that.executorMethod) &&
            Objects.equals(this.afterMethod, that.afterMethod);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.beforeMethod, this.executorMethod, this.afterMethod);
    }

    @Override
    public String toString() {
        return "MethodData{" +
            "beforeMethod=" + this.beforeMethod +
            ", executorMethod=" + this.executorMethod +
            ", afterMethod=" + this.afterMethod +
            "}";
    }

    public Method getBeforeMethod() {
        return this.beforeMethod;
    }

    public void setBeforeMethod(final Method beforeMethod) {
        this.beforeMethod = beforeMethod;
    }

    public Method getExecutorMethod() {
        return this.executorMethod;
    }

    public void setExecutorMethod(final Method executorMethod) {
        this.executorMethod = executorMethod;
    }

    public Method getAfterMethod() {
        return this.afterMethod;
    }

    public void setAfterMethod(final Method afterMethod) {
        this.afterMethod = afterMethod;
    }
}

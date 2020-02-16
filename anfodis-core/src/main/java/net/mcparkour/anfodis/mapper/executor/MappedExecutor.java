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
import org.jetbrains.annotations.Nullable;

public class MappedExecutor {

	@Nullable
	private Method beforeMethod;
	@Nullable
	private Method executorMethod;
	@Nullable
	private Method afterMethod;

	public MappedExecutor() {}

	public MappedExecutor(@Nullable Method beforeMethod, @Nullable Method executorMethod, @Nullable Method afterMethod) {
		this.beforeMethod = beforeMethod;
		this.executorMethod = executorMethod;
		this.afterMethod = afterMethod;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		MappedExecutor that = (MappedExecutor) object;
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
		return "MappedExecutor{" +
			"beforeMethod=" + this.beforeMethod +
			", executorMethod=" + this.executorMethod +
			", afterMethod=" + this.afterMethod +
			"}";
	}

	@Nullable
	public Method getBeforeMethod() {
		return this.beforeMethod;
	}

	public void setBeforeMethod(@Nullable Method beforeMethod) {
		this.beforeMethod = beforeMethod;
	}

	@Nullable
	public Method getExecutorMethod() {
		return this.executorMethod;
	}

	public void setExecutorMethod(@Nullable Method executorMethod) {
		this.executorMethod = executorMethod;
	}

	@Nullable
	public Method getAfterMethod() {
		return this.afterMethod;
	}

	public void setAfterMethod(@Nullable Method afterMethod) {
		this.afterMethod = afterMethod;
	}
}

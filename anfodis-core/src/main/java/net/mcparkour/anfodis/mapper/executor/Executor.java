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
import net.mcparkour.common.reflection.Reflections;

public class Executor {

	private MappedExecutor mappedExecutor;

	public Executor(MappedExecutor mappedExecutor) {
		this.mappedExecutor = mappedExecutor;
	}

	public void invokeBefore(Object instance) {
		Method method = this.mappedExecutor.getBeforeMethod();
		if (method != null) {
			Reflections.invokeMethod(method, instance);
		}
	}

	public Object invokeExecutor(Object instance) {
		Method method = this.mappedExecutor.getExecutorMethod();
		if (method == null) {
			throw new RuntimeException("Method is null");
		}
		return Reflections.invokeMethod(method, instance);
	}

	public void invokeAfter(Object instance) {
		Method method = this.mappedExecutor.getAfterMethod();
		if (method != null) {
			Reflections.invokeMethod(method, instance);
		}
	}
}

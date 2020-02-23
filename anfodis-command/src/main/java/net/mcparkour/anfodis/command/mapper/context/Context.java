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

package net.mcparkour.anfodis.command.mapper.context;

import java.lang.reflect.Field;
import net.mcparkour.common.reflection.Reflections;
import org.jetbrains.annotations.Nullable;

public class Context<D extends ContextData> {

	private D contextData;

	public Context(D contextData) {
		this.contextData = contextData;
	}

	public void setArgumentsField(Object instance, Object arguments) {
		Field field = this.contextData.getArgumentsField();
		if (field == null) {
			return;
		}
		Reflections.setFieldValue(field, instance, arguments);
	}

	public void setRequiredPermissionField(Object instance, @Nullable Object requiredPermission) {
		Field field = this.contextData.getRequiredPermissionField();
		if (field == null) {
			return;
		}
		Reflections.setFieldValue(field, instance, requiredPermission);
	}

	public void setSenderField(Object instance, Object sender) {
		Field field = this.contextData.getSenderField();
		if (field == null) {
			return;
		}
		Reflections.setFieldValue(field, instance, sender);
	}
}

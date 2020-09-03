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
import java.util.List;
import net.mcparkour.common.reflection.Reflections;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.intext.message.MessageReceiver;
import org.jetbrains.annotations.Nullable;

public class Context {

    @Nullable
    private final Field argumentsField;
    @Nullable
    private final Field requiredPermissionField;
    @Nullable
    private final Field senderField;
    @Nullable
    private final Field receiverField;

    public Context(final ContextData contextData) {
        this.argumentsField = contextData.getArgumentsField();
        this.requiredPermissionField = contextData.getRequiredPermissionField();
        this.senderField = contextData.getSenderField();
        this.receiverField = contextData.getReceiverField();
    }

    public void setArgumentsField(final Object instance, final List<String> arguments) {
        if (this.argumentsField == null) {
            return;
        }
        Reflections.setFieldValue(this.argumentsField, instance, arguments);
    }

    public void setRequiredPermissionField(final Object instance, final Permission requiredPermission) {
        if (this.requiredPermissionField == null) {
            return;
        }
        Reflections.setFieldValue(this.requiredPermissionField, instance, requiredPermission);
    }

    public void setSenderField(final Object instance, final Object sender) {
        if (this.senderField == null) {
            return;
        }
        Reflections.setFieldValue(this.senderField, instance, sender);
    }

    public void setReceiverField(final Object instance, final MessageReceiver receiver) {
        if (this.receiverField == null) {
            return;
        }
        Reflections.setFieldValue(this.receiverField, instance, receiver);
    }
}

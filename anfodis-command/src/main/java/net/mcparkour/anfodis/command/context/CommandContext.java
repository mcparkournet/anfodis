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

package net.mcparkour.anfodis.command.context;

import java.util.List;
import net.mcparkour.anfodis.handler.RootContext;
import net.mcparkour.craftmon.permission.Permission;

public class CommandContext<T> extends RootContext {

    private final CommandSender<T> sender;
    private final List<String> arguments;
    private final Permission permission;
    private final boolean asynchronous;

    public CommandContext(
        final CommandSender<T> sender,
        final List<String> arguments,
        final Permission permission,
        final boolean asynchronous
    ) {
        this.sender = sender;
        this.arguments = arguments;
        this.permission = permission;
        this.asynchronous = asynchronous;
    }

    public CommandSender<T> getSender() {
        return this.sender;
    }

    public List<String> getArguments() {
        return List.copyOf(this.arguments);
    }

    public Permission getPermission() {
        return this.permission;
    }

    public boolean isAsynchronous() {
        return this.asynchronous;
    }
}

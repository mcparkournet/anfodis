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

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import net.mcparkour.anfodis.command.lexer.Token;
import net.mcparkour.anfodis.command.mapper.Command;
import net.mcparkour.anfodis.handler.RootContext;
import net.mcparkour.craftmon.permission.Permission;

public class CommandContext<T extends Command<T, ?, ?, ?, S>, S> extends RootContext {

    private final Sender<S> sender;
    private final List<Token> arguments;
    private final Deque<T> parents;
    private final Permission permission;
    private final boolean asynchronous;

    public CommandContext(
        final Sender<S> sender,
        final List<Token> arguments,
        final Deque<T> parents,
        final Permission permission,
        final boolean asynchronous
    ) {
        this.sender = sender;
        this.arguments = arguments;
        this.parents = parents;
        this.permission = permission;
        this.asynchronous = asynchronous;
    }

    public Sender<S> getSender() {
        return this.sender;
    }

    public List<Token> getArguments() {
        return List.copyOf(this.arguments);
    }

    public Deque<T> getParents() {
        return new LinkedList<>(this.parents);
    }

    public Permission getPermission() {
        return this.permission;
    }

    public boolean isAsynchronous() {
        return this.asynchronous;
    }
}

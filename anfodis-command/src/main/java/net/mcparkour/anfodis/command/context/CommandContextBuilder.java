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

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import net.mcparkour.anfodis.command.handler.CommandContextCreator;
import net.mcparkour.anfodis.command.lexer.Token;
import net.mcparkour.anfodis.command.mapper.Command;
import net.mcparkour.craftmon.permission.Permission;

public class CommandContextBuilder<C extends CommandContext<T, S>, T extends Command<T, ?, ?, ?>, S> {

    private final Sender<S> sender;
    private final Deque<Token> arguments;
    private final Deque<T> parents;
    private Permission permission;
    private final boolean asynchronous;

    public CommandContextBuilder(final Sender<S> sender, final T command, final List<Token> arguments, final Permission permission, final boolean asynchronous) {
        this.sender = sender;
        this.arguments = new LinkedList<>(arguments);
        Deque<T> parents = new LinkedList<>();
        parents.push(command);
        this.parents = parents;
        this.permission = permission;
        this.asynchronous = asynchronous;
    }

    public int getArgumentsSize() {
        return this.arguments.size();
    }

    public Optional<Token> peekArgument() {
        Token last = this.arguments.peek();
        return Optional.ofNullable(last);
    }

    public Optional<Token> pollArgument() {
        Token last = this.arguments.poll();
        return Optional.ofNullable(last);
    }

    public void pushParent(final T command) {
        this.parents.push(command);
    }

    public void permissionWithLast(final Permission permission) {
        this.permission = this.permission.withLast(permission);
    }

    public C build(final CommandContextCreator<T, C, S> commandContextCreator) {
        List<Token> arguments = new ArrayList<>(this.arguments);
        return commandContextCreator.create(this.sender, arguments, this.parents, this.permission, this.asynchronous);
    }

    public Sender<S> getSender() {
        return this.sender;
    }

    public Permission getPermission() {
        return this.permission;
    }

    public boolean isAsynchronous() {
        return this.asynchronous;
    }
}

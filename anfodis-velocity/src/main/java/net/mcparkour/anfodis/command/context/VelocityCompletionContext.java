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
import java.util.List;
import com.velocitypowered.api.command.CommandSource;
import net.mcparkour.anfodis.command.handler.CompletionContext;
import net.mcparkour.anfodis.command.lexer.Token;
import net.mcparkour.anfodis.command.mapper.VelocityCommand;
import net.mcparkour.craftmon.permission.Permission;

public class VelocityCompletionContext extends CompletionContext<VelocityCommand, CommandSource> {

    public VelocityCompletionContext(
        final Sender<CommandSource> sender,
        final List<Token> arguments,
        final Deque<VelocityCommand> parents,
        final Permission permission,
        final boolean asynchronous
    ) {
        super(sender, arguments, parents, permission, asynchronous);
    }
}

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

package net.mcparkour.anfodis.command.handler;

import java.util.Map;
import net.mcparkour.anfodis.TestCommandSender;
import net.mcparkour.anfodis.command.TestMessenger;
import net.mcparkour.anfodis.command.context.TestCommandContext;
import net.mcparkour.anfodis.command.context.TestCommandContextBuilder;
import net.mcparkour.anfodis.command.mapper.TestCommand;
import net.mcparkour.anfodis.handler.ContextHandler;
import org.jetbrains.annotations.Nullable;

public class TestCommandHandler
    extends CommandHandler<TestCommand, TestCommandContext, TestCommandContextBuilder, TestCommandSender, TestMessenger> {

    public TestCommandHandler(
        final TestCommand command,
        final Map<TestCommand, ? extends CommandContextBuilderHandler<TestCommandContextBuilder, TestCommandContext>> subCommandHandlers,
        @Nullable final ContextHandler<TestCommandContext> executorHandler,
        final CommandContextCreator<TestCommand, TestCommandContext, TestCommandSender> contextSupplier,
        final TestMessenger messenger
    ) {
        super(command, subCommandHandlers, executorHandler, contextSupplier, messenger);
    }
}

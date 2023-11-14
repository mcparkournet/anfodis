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

import net.mcparkour.anfodis.command.IoMessenger;
import net.mcparkour.anfodis.command.context.IoCommandContext;
import net.mcparkour.anfodis.command.context.IoCommandContextBuilder;
import net.mcparkour.anfodis.command.mapper.IoCommand;
import net.mcparkour.anfodis.handler.ContextHandler;
import org.jetbrains.annotations.Nullable;

import java.io.OutputStreamWriter;
import java.util.Map;

public class IoCommandHandler
    extends CommandHandler<IoCommand, IoCommandContext, IoCommandContextBuilder, OutputStreamWriter, IoMessenger> {

    public IoCommandHandler(
        final IoCommand command,
        final Map<IoCommand, ? extends CommandContextBuilderHandler<IoCommandContextBuilder, IoCommandContext>> subCommandHandlers,
        final @Nullable ContextHandler<? super IoCommandContext> executorHandler,
        final CommandContextCreator<IoCommand, IoCommandContext, OutputStreamWriter> contextSupplier,
        final IoMessenger messenger
    ) {
        super(command, subCommandHandlers, executorHandler, contextSupplier, messenger);
    }
}

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
import net.mcparkour.anfodis.command.PaperMessenger;
import net.mcparkour.anfodis.command.context.Sender;
import net.mcparkour.anfodis.command.context.PaperCommandContext;
import net.mcparkour.anfodis.command.context.PaperCommandContextBuilder;
import net.mcparkour.anfodis.command.mapper.PaperCommand;
import net.mcparkour.anfodis.command.mapper.properties.PaperCommandProperties;
import net.mcparkour.anfodis.handler.ContextHandler;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

public class PaperCommandHandler
    extends CommandHandler<PaperCommand, PaperCommandContext, PaperCommandContextBuilder, CommandSender, PaperMessenger> {

    public PaperCommandHandler(
        final PaperCommand command,
        final Map<PaperCommand, ? extends CommandContextBuilderHandler<PaperCommandContextBuilder, PaperCommandContext>> subCommandHandlers,
        final @Nullable ContextHandler<? super PaperCommandContext> executorHandler,
        final CommandContextCreator<PaperCommand, PaperCommandContext, CommandSender> contextSupplier,
        final PaperMessenger messenger
    ) {
        super(command, subCommandHandlers, executorHandler, contextSupplier, messenger);
    }

    @Override
    public void handle(final PaperCommandContextBuilder contextBuilder) {
        PaperCommand command = getCommand();
        PaperCommandProperties properties = command.getProperties();
        Sender<CommandSender> sender = contextBuilder.getSender();
        if (!properties.isValidSender(sender)) {
            PaperMessenger messenger = getMessenger();
            var contextCreator = getContextCreator();
            PaperCommandContext context = contextBuilder.build(contextCreator);
            var validSenders = properties.getSenderTypes();
            messenger.sendInvalidSenderMessage(context, validSenders);
            return;
        }
        super.handle(contextBuilder);
    }
}

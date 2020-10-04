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
import net.mcparkour.anfodis.command.WaterfallMessenger;
import net.mcparkour.anfodis.command.context.Sender;
import net.mcparkour.anfodis.command.context.WaterfallCommandContext;
import net.mcparkour.anfodis.command.context.WaterfallCommandContextBuilder;
import net.mcparkour.anfodis.command.mapper.WaterfallCommand;
import net.mcparkour.anfodis.command.mapper.properties.WaterfallCommandProperties;
import net.mcparkour.anfodis.handler.ContextHandler;
import net.md_5.bungee.api.CommandSender;
import org.jetbrains.annotations.Nullable;

public class WaterfallCommandHandler
    extends CommandHandler<WaterfallCommand, WaterfallCommandContext, WaterfallCommandContextBuilder, CommandSender, WaterfallMessenger> {

    public WaterfallCommandHandler(
        final WaterfallCommand command,
        final Map<WaterfallCommand, ? extends CommandContextBuilderHandler<WaterfallCommandContextBuilder, WaterfallCommandContext>> subCommandHandlers,
        final @Nullable ContextHandler<? super WaterfallCommandContext> executorHandler,
        final CommandContextCreator<WaterfallCommand, WaterfallCommandContext, CommandSender> contextSupplier,
        final WaterfallMessenger messenger
    ) {
        super(command, subCommandHandlers, executorHandler, contextSupplier, messenger);
    }

    @Override
    public void handle(final WaterfallCommandContextBuilder contextBuilder) {
        WaterfallCommand command = getCommand();
        WaterfallCommandProperties properties = command.getProperties();
        Sender<CommandSender> sender = contextBuilder.getSender();
        if (!properties.isValidSender(sender)) {
            WaterfallMessenger messenger = getMessenger();
            var senderTypes = properties.getSenderTypes();
            messenger.sendInvalidSenderMessage(sender, senderTypes);
            return;
        }
        super.handle(contextBuilder);
    }
}

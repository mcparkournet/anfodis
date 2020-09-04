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
import java.util.Set;
import com.velocitypowered.api.command.CommandSource;
import net.mcparkour.anfodis.command.VelocityMessenger;
import net.mcparkour.anfodis.command.context.CommandSender;
import net.mcparkour.anfodis.command.context.VelocityCommandContext;
import net.mcparkour.anfodis.command.mapper.VelocityCommand;
import net.mcparkour.anfodis.command.mapper.properties.VelocityCommandProperties;
import net.mcparkour.anfodis.handler.ContextHandler;
import net.mcparkour.intext.message.MessageReceiver;
import org.jetbrains.annotations.Nullable;

public class VelocityCommandHandler
    extends CommandHandler<VelocityCommand, VelocityCommandContext, CommandSource, VelocityMessenger> {

    public VelocityCommandHandler(
        final VelocityCommand command,
        final Map<VelocityCommand, ? extends CommandContextHandler<VelocityCommandContext>> subCommandHandlers,
        @Nullable final ContextHandler<VelocityCommandContext> executorHandler,
        final CommandContextSupplier<VelocityCommandContext, CommandSource> contextSupplier,
        final VelocityMessenger messenger
    ) {
        super(command, subCommandHandlers, executorHandler, contextSupplier, messenger);
    }

    @Override
    public void handle(final VelocityCommandContext context) {
        VelocityCommand command = getCommand();
        VelocityCommandProperties properties = command.getProperties();
        CommandSender<CommandSource> sender = context.getSender();
        if (!properties.isValidSender(sender)) {
            VelocityMessenger messenger = getMessenger();
            var senderTypes = properties.getSenderTypes();
            messenger.sendInvalidSenderMessage(sender, senderTypes);
            return;
        }
        super.handle(context);
    }
}

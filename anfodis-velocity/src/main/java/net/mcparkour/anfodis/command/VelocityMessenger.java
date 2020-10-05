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

package net.mcparkour.anfodis.command;

import java.util.Set;
import java.util.stream.Collectors;
import com.velocitypowered.api.command.CommandSource;
import net.mcparkour.anfodis.command.context.Sender;
import net.mcparkour.anfodis.command.context.VelocityCommandContext;
import net.mcparkour.anfodis.command.mapper.VelocityCommand;
import net.mcparkour.intext.message.MessageReceiver;

public interface VelocityMessenger
    extends Messenger<VelocityCommand, VelocityCommandContext, CommandSource> {

    default void sendInvalidSenderMessage(
        final VelocityCommandContext context,
        final Set<Class<? extends CommandSource>> validSenders
    ) {
        Sender<CommandSource> sender = context.getSender();
        MessageReceiver receiver = sender.getReceiver();
        String senderType = sender.getClass().getSimpleName();
        String validSendersString = validSenders.stream()
            .map(Class::getSimpleName)
            .collect(Collectors.joining(", "));
        receiver.receivePlain("You are not a valid sender: " + senderType + ", required: " + validSendersString);
    }
}

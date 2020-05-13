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
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.context.CommandSender;
import net.mcparkour.anfodis.command.context.VelocityCommandContext;
import net.mcparkour.anfodis.command.mapper.VelocityCommand;
import net.mcparkour.anfodis.command.mapper.properties.VelocityCommandProperties;
import net.mcparkour.intext.message.MessageReceiver;

public class VelocityCommandHandler extends CommandHandler<VelocityCommand, VelocityCommandContext> {

	public VelocityCommandHandler(VelocityCommand command, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, Map<VelocityCommand, ? extends CommandContextHandler<VelocityCommandContext>> subCommandHandlers) {
		super(command, injectionCodecRegistry, argumentCodecRegistry, subCommandHandlers);
	}

	@Override
	public void handle(VelocityCommandContext context) {
		if (!checkSenders(context)) {
			CommandSender<?> sender = context.getSender();
			MessageReceiver receiver = sender.getReceiver();
			receiver.receivePlain("You are not a valid sender.");
			return;
		}
		super.handle(context);
	}

	private boolean checkSenders(VelocityCommandContext context) {
		VelocityCommand command = getCommand();
		VelocityCommandProperties properties = command.getProperties();
		Set<Class<? extends CommandSource>> senders = properties.getSenderTypes();
		if (senders.isEmpty()) {
			return true;
		}
		CommandSender<CommandSource> commandSender = context.getSender();
		CommandSource source = commandSender.getSender();
		Class<?> sourceType = source.getClass();
		return senders.stream().anyMatch(sender -> sender.isAssignableFrom(sourceType));
	}
}

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

import java.util.List;
import java.util.Map;
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.context.CommandSender;
import net.mcparkour.anfodis.command.context.WaterfallCommandContext;
import net.mcparkour.anfodis.command.mapper.WaterfallCommand;
import net.mcparkour.anfodis.command.mapper.properties.WaterfallCommandProperties;
import net.mcparkour.intext.message.MessageReceiver;

public class WaterfallCommandHandler extends CommandHandler<WaterfallCommand, WaterfallCommandContext> {

	public WaterfallCommandHandler(WaterfallCommand command, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, Map<WaterfallCommand, ? extends CommandContextHandler<WaterfallCommandContext>> subCommandHandlers) {
		super(command, injectionCodecRegistry, argumentCodecRegistry, subCommandHandlers);
	}

	@Override
	public void handle(WaterfallCommandContext context) {
		if (!checkSenders(context)) {
			CommandSender<net.md_5.bungee.api.CommandSender> sender = context.getSender();
			MessageReceiver receiver = sender.getReceiver();
			receiver.receivePlain("You are not a valid sender.");
			return;
		}
		super.handle(context);
	}

	private boolean checkSenders(WaterfallCommandContext context) {
		WaterfallCommand command = getCommand();
		WaterfallCommandProperties properties = command.getProperties();
		List<Class<? extends net.md_5.bungee.api.CommandSender>> senders = properties.getSendersTypes();
		if (senders.isEmpty()) {
			return true;
		}
		CommandSender<net.md_5.bungee.api.CommandSender> commandSender = context.getSender();
		Object rawSender = commandSender.getSender();
		Class<?> rawSenderType = rawSender.getClass();
		return senders.stream()
			.anyMatch(sender -> sender.isAssignableFrom(rawSenderType));
	}
}

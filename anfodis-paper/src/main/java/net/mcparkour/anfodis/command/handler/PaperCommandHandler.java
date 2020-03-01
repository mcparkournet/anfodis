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
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.mapper.PaperCommand;
import net.mcparkour.anfodis.command.mapper.properties.PaperCommandProperties;
import net.mcparkour.intext.translation.Translations;

public class PaperCommandHandler extends CommandHandler<PaperCommand> {

	public PaperCommandHandler(PaperCommand command, CommandContext context, Translations translations, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry) {
		super(command, context, translations, injectionCodecRegistry, argumentCodecRegistry);
	}

	@Override
	public void handle() {
		CommandContext context = getContext();
		CommandSender sender = context.getSender();
		if (!checkSenders()) {
			sender.sendMessage("You are not a valid sender.");
			return;
		}
		super.handle();
	}

	private boolean checkSenders() {
		PaperCommand command = getCommand();
		PaperCommandProperties properties = command.getProperties();
		List<Class<? extends org.bukkit.command.CommandSender>> senders = properties.getSendersTypes();
		if (senders.isEmpty()) {
			return true;
		}
		CommandContext context = getContext();
		CommandSender commandSender = context.getSender();
		Object rawSender = commandSender.getRawSender();
		Class<?> rawSenderType = rawSender.getClass();
		return senders.stream().anyMatch(sender -> sender.isAssignableFrom(rawSenderType));
	}
}
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.mapper.JDACommand;
import net.mcparkour.anfodis.command.mapper.properties.JDACommandProperties;
import net.mcparkour.anfodis.handler.Handler;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.craftmon.permission.PermissionBuilder;
import net.mcparkour.intext.translation.Translations;
import org.jetbrains.annotations.Nullable;

public class PrivateMessageReceivedListener implements EventListener {

	private String prefix;
	private Translations translations;
	private Map<Long, List<Permission>> permissions;
	private Map<String, JDACommand> commands;
	private CodecRegistry<InjectionCodec<?>> injectionCodecRegistry;
	private CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry;

	public PrivateMessageReceivedListener(String prefix, Translations translations, Map<Long, List<Permission>> permissions, Map<String, JDACommand> commands, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry) {
		this.prefix = prefix;
		this.translations = translations;
		this.permissions = permissions;
		this.commands = commands;
		this.injectionCodecRegistry = injectionCodecRegistry;
		this.argumentCodecRegistry = argumentCodecRegistry;
	}

	@Override
	public void onEvent(@Nonnull GenericEvent event) {
		if (event instanceof PrivateMessageReceivedEvent) {
			PrivateMessageReceivedEvent privateMessageReceivedEvent = (PrivateMessageReceivedEvent) event;
			onPrivateMessageReceivedEvent(privateMessageReceivedEvent);
		}
	}

	private void onPrivateMessageReceivedEvent(PrivateMessageReceivedEvent event) {
		Message message = event.getMessage();
		String rawMessage = message.getContentRaw();
		if (rawMessage.isBlank()) {
			return;
		}
		if (rawMessage.charAt(0) != '/') {
			return;
		}
		String[] split = rawMessage.split(" ");
		String name = split[0];
		String nameWithoutSlash = name.substring(1);
		JDACommand command = this.commands.get(nameWithoutSlash);
		if (command == null) {
			return;
		}
		User sender = event.getAuthor();
		PrivateChannel channel = event.getChannel();
		JDACommandSender jdaCommandSender = new JDACommandSender(sender, channel, this.permissions);
		String[] argumentsArray = Arrays.copyOfRange(split, 1, split.length);
		List<String> arguments = List.of(argumentsArray);
		JDACommandProperties properties = command.getProperties();
		Permission permission = createPermission(properties);
		JDACommandContext context = new JDACommandContext(jdaCommandSender, arguments, permission, channel);
		Handler handler = new JDACommandHandler(command, context, this.translations, this.injectionCodecRegistry, this.argumentCodecRegistry);
		handler.handle();
	}

	@Nullable
	private Permission createPermission(JDACommandProperties properties) {
		String commandPermission = properties.getPermission();
		if (commandPermission == null) {
			return null;
		}
		return new PermissionBuilder()
			.node(this.prefix)
			.node(commandPermission)
			.build();
	}
}

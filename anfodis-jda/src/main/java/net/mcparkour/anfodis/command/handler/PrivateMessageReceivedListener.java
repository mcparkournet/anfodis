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
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.mcparkour.anfodis.command.mapper.JDACommand;
import net.mcparkour.anfodis.command.mapper.properties.JDACommandProperties;
import net.mcparkour.anfodis.command.registry.CommandMap;
import net.mcparkour.anfodis.command.registry.CommandMapEntry;
import net.mcparkour.anfodis.command.registry.PermissionMap;
import net.mcparkour.anfodis.handler.ContextHandler;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.craftmon.permission.PermissionBuilder;
import org.jetbrains.annotations.Nullable;

public class PrivateMessageReceivedListener implements EventListener {

	private String permissionPrefix;
	private PermissionMap permissionMap;
	private CommandMap commandMap;

	public PrivateMessageReceivedListener(String permissionPrefix, PermissionMap permissionMap, CommandMap commandMap) {
		this.permissionPrefix = permissionPrefix;
		this.permissionMap = permissionMap;
		this.commandMap = commandMap;
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
		CommandMapEntry entry = this.commandMap.getCommand(nameWithoutSlash);
		if (entry == null) {
			return;
		}
		ContextHandler<JDACommandContext> handler = entry.getHandler();
		JDACommand command = entry.getCommand();
		JDACommandContext context = createContext(event, split, command);
		Object commandInstance = command.createInstance();
		handler.handle(context, commandInstance);
	}

	private JDACommandContext createContext(PrivateMessageReceivedEvent event, String[] split, JDACommand command) {
		User sender = event.getAuthor();
		PrivateChannel channel = event.getChannel();
		JDACommandSender jdaCommandSender = new JDACommandSender(sender, channel, this.permissionMap);
		String[] argumentsArray = Arrays.copyOfRange(split, 1, split.length);
		List<String> arguments = List.of(argumentsArray);
		JDACommandProperties properties = command.getProperties();
		Permission permission = createPermission(properties);
		return new JDACommandContext(jdaCommandSender, arguments, permission, channel);
	}

	@Nullable
	private Permission createPermission(JDACommandProperties properties) {
		String commandPermission = properties.getPermission();
		if (commandPermission == null) {
			return null;
		}
		return new PermissionBuilder()
			.node(this.permissionPrefix)
			.node(commandPermission)
			.build();
	}
}

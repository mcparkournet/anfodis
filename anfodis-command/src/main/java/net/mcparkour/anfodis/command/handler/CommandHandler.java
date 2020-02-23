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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.mapper.Command;
import net.mcparkour.anfodis.command.mapper.argument.Argument;
import net.mcparkour.anfodis.command.mapper.properties.CommandProperties;
import net.mcparkour.anfodis.handler.Handler;
import net.mcparkour.anfodis.mapper.executor.Executor;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.craftmon.permission.PermissionBuilder;
import net.mcparkour.intext.translation.Translations;
import org.jetbrains.annotations.Nullable;

public class CommandHandler<T extends Command<?, ?, ?>> implements Handler {

	private T command;
	private CommandContext context;
	private Translations translations;
	private CodecRegistry<InjectionCodec<?>> injectionCodecRegistry;
	private CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry;

	public CommandHandler(T command, CommandContext context, Translations translations, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry) {
		this.command = command;
		this.context = context;
		this.translations = translations;
		this.injectionCodecRegistry = injectionCodecRegistry;
		this.argumentCodecRegistry = argumentCodecRegistry;
	}

	@Override
	public void handle() {
		CommandSender sender = this.context.getSender();
		if (!checkPermission()) {
			sender.sendMessage("You do not have permission.");
			return;
		}
		List<String> arguments = this.context.getArguments();
		if (!arguments.isEmpty()) {
			String firstArgument = arguments.get(0);
			for (Command<?, ?, ?> subCommand : this.command.getSubCommands()) {
				if (isMatching(firstArgument, subCommand)) {
					List<String> argumentsCopy = new ArrayList<>(arguments);
					argumentsCopy.remove(0);
					Permission permission = getPermission(subCommand);
					CommandContext context = new CommandContext(sender, argumentsCopy, permission);
					Handler handler = new CommandHandler<>(subCommand, context, this.translations, this.injectionCodecRegistry, this.argumentCodecRegistry);
					handler.handle();
					return;
				}
			}
		}
		Executor executor = this.command.getExecutor();
		if (executor == null) {
			sendHelpMessage();
			return;
		}
		if (!checkLength()) {
			sender.sendMessage("Invalid number of arguments.");
			return;
		}
		Handler executorHandler = new CommandExecutorHandler<>(this.command, this.context, this.translations, this.injectionCodecRegistry, this.argumentCodecRegistry, executor);
		executorHandler.handle();
	}

	private boolean checkLength() {
		List<String> arguments = this.context.getArguments();
		int entrySize = arguments.size();
		List<? extends Argument<?>> commandArguments = this.command.getArguments();
		int minSize = (int) commandArguments.stream()
			.filter(argument -> !argument.isOptional())
			.count();
		int maxSize = commandArguments.size();
		if (commandArguments.stream()
			.map(Argument::getFieldType)
			.anyMatch(type -> type.isAssignableFrom(List.class))) {
			return entrySize >= minSize;
		}
		return entrySize >= minSize && entrySize <= maxSize;
	}

	private boolean checkPermission() {
		Permission permission = this.context.getPermission();
		if (permission == null) {
			return true;
		}
		CommandSender sender = this.context.getSender();
		return sender.hasPermission(permission);
	}

	private boolean isMatching(String argument, Command<?, ?, ?> command) {
		CommandProperties<?> properties = command.getProperties();
		String name = properties.getName();
		if (argument.equalsIgnoreCase(name)) {
			return true;
		}
		List<String> aliases = properties.getAliases();
		return aliases.stream()
			.anyMatch(alias -> alias.equalsIgnoreCase(argument));
	}

	@Nullable
	private Permission getPermission(Command<?, ?, ?> command) {
		Permission permission = this.context.getPermission();
		if (permission == null) {
			return null;
		}
		CommandProperties<?> properties = command.getProperties();
		String permissionName = properties.getPermission();
		if (permissionName == null) {
			return null;
		}
		return new PermissionBuilder()
			.with(permission)
			.node(permissionName)
			.build();
	}

	private void sendHelpMessage() {
		StringBuilder usage = new StringBuilder();
		CommandProperties<?> properties = this.command.getProperties();
		String name = properties.getName();
		usage.append("Correct usage: " + name);
		String description = properties.getDescription();
		if (description != null) {
			usage.append(" - " + description);
		}
		usage.append(".");
		CommandSender sender = this.context.getSender();
		sender.sendMessage(usage.toString());
		for (Command<?, ?, ?> subCommand : this.command.getSubCommands()) {
			StringBuilder subCommandUsage = new StringBuilder();
			CommandProperties<?> subCommandProperties = subCommand.getProperties();
			String subCommandName = subCommandProperties.getName();
			subCommandUsage.append(subCommandName);
			List<? extends Argument<?>> subCommandArguments = subCommand.getArguments();
			if (!subCommandArguments.isEmpty()) {
				subCommandUsage.append(" ");
				String collect = subCommandArguments.stream()
					.map(Argument::getName)
					.collect(Collectors.joining(" ", "<", ">"));
				subCommandUsage.append(collect);
			}
			String subCommandDescription = subCommandProperties.getDescription();
			if (subCommandDescription != null) {
				subCommandUsage.append(" - " + subCommandDescription);
			}
			subCommandUsage.append(".");
			sender.sendMessage(subCommandUsage.toString());
		}
	}

	protected T getCommand() {
		return this.command;
	}

	protected CommandContext getContext() {
		return this.context;
	}

	protected Translations getTranslations() {
		return this.translations;
	}

	protected CodecRegistry<InjectionCodec<?>> getInjectionCodecRegistry() {
		return this.injectionCodecRegistry;
	}

	protected CodecRegistry<ArgumentCodec<?>> getArgumentCodecRegistry() {
		return this.argumentCodecRegistry;
	}
}

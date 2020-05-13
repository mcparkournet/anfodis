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
import java.util.Set;
import java.util.stream.Collectors;
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.context.CommandContext;
import net.mcparkour.anfodis.command.context.CommandSender;
import net.mcparkour.anfodis.command.mapper.Command;
import net.mcparkour.anfodis.command.mapper.argument.Argument;
import net.mcparkour.anfodis.command.mapper.properties.CommandProperties;
import net.mcparkour.anfodis.handler.ContextHandler;
import net.mcparkour.anfodis.mapper.executor.Executor;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.intext.message.MessageReceiver;

public class CommandHandler<T extends Command<T, ?, ?, ?>, C extends CommandContext<?>> implements CommandContextHandler<C> {

	private T command;
	private CodecRegistry<InjectionCodec<?>> injectionCodecRegistry;
	private CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry;
	private Map<T, ? extends CommandContextHandler<C>> subCommandHandlers;
	private ContextHandler<C> executorHandler;

	public CommandHandler(T command, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, Map<T, ? extends CommandContextHandler<C>> subCommandHandlers) {
		this(command, injectionCodecRegistry, argumentCodecRegistry, subCommandHandlers, new CommandExecutorHandler<>(command, injectionCodecRegistry, argumentCodecRegistry));
	}

	public CommandHandler(T command, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, Map<T, ? extends CommandContextHandler<C>> subCommandHandlers, ContextHandler<C> executorHandler) {
		this.command = command;
		this.injectionCodecRegistry = injectionCodecRegistry;
		this.argumentCodecRegistry = argumentCodecRegistry;
		this.subCommandHandlers = subCommandHandlers;
		this.executorHandler = executorHandler;
	}

	@Override
	public void handle(C context) {
		CommandSender<?> sender = context.getSender();
		MessageReceiver receiver = sender.getReceiver();
		if (!checkPermission(context)) {
			receiver.receivePlain("You do not have permission.");
			return;
		}
		List<String> arguments = context.getArguments();
		if (!arguments.isEmpty()) {
			String firstArgument = arguments.get(0);
			List<T> subCommands = this.command.getSubCommands();
			T subCommand = subCommands.stream()
				.filter(element -> isMatching(firstArgument, element))
				.findFirst()
				.orElse(null);
			if (subCommand != null) {
				CommandContextHandler<C> handler = this.subCommandHandlers.get(subCommand);
				if (handler != null) {
					context.removeFirstArgument();
					CommandProperties properties = subCommand.getProperties();
					String permissionName = properties.getPermission();
					if (permissionName != null) {
						context.appendPermissionNode(permissionName);
					}
					handler.handle(context);
					return;
				}
			}
		}
		Executor executor = this.command.getExecutor();
		if (!executor.hasExecutor()) {
			sendHelpMessage(context);
			return;
		}
		if (!checkLength(context)) {
			receiver.receivePlain("Invalid number of arguments.");
			return;
		}
		Object instance = this.command.createInstance();
		this.executorHandler.handle(context, instance);
	}

	private boolean checkLength(C context) {
		List<String> arguments = context.getArguments();
		int entrySize = arguments.size();
		List<? extends Argument> commandArguments = this.command.getArguments();
		int minSize = (int) commandArguments.stream()
			.filter(argument -> !argument.isOptional())
			.count();
		if (commandArguments.stream().anyMatch(Argument::isList)) {
			return entrySize >= minSize;
		}
		int maxSize = commandArguments.size();
		return entrySize >= minSize && entrySize <= maxSize;
	}

	private boolean checkPermission(C context) {
		Permission permission = context.getPermission();
		if (permission == null) {
			return true;
		}
		CommandSender<?> sender = context.getSender();
		return sender.hasPermission(permission);
	}

	private boolean isMatching(String argument, T command) {
		CommandProperties properties = command.getProperties();
		String name = properties.getName();
		if (argument.equalsIgnoreCase(name)) {
			return true;
		}
		String lowerCaseArgument = argument.toLowerCase();
		Set<String> aliases = properties.getLowerCaseAliases();
		return aliases.contains(lowerCaseArgument);
	}

	private void sendHelpMessage(C context) {
		StringBuilder usage = new StringBuilder();
		CommandProperties properties = this.command.getProperties();
		String name = properties.getName();
		usage.append("Correct usage: " + name);
		String description = properties.getDescription();
		if (description != null) {
			usage.append(" - " + description);
		}
		usage.append(".");
		CommandSender<?> sender = context.getSender();
		MessageReceiver receiver = sender.getReceiver();
		receiver.receivePlain(usage.toString());
		List<T> subCommands = this.command.getSubCommands();
		for (T subCommand : subCommands) {
			StringBuilder subCommandUsage = new StringBuilder();
			CommandProperties subCommandProperties = subCommand.getProperties();
			String subCommandName = subCommandProperties.getName();
			subCommandUsage.append(subCommandName);
			List<? extends Argument> subCommandArguments = subCommand.getArguments();
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
			receiver.receivePlain(subCommandUsage.toString());
		}
	}

	protected T getCommand() {
		return this.command;
	}

	protected CodecRegistry<InjectionCodec<?>> getInjectionCodecRegistry() {
		return this.injectionCodecRegistry;
	}

	protected CodecRegistry<ArgumentCodec<?>> getArgumentCodecRegistry() {
		return this.argumentCodecRegistry;
	}
}

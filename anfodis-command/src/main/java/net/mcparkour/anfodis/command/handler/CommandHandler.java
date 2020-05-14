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
import net.mcparkour.anfodis.command.context.CommandContext;
import net.mcparkour.anfodis.command.context.CommandSender;
import net.mcparkour.anfodis.command.mapper.Command;
import net.mcparkour.anfodis.command.mapper.argument.Argument;
import net.mcparkour.anfodis.command.mapper.properties.CommandProperties;
import net.mcparkour.anfodis.handler.ContextHandler;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.intext.message.MessageReceiver;
import org.jetbrains.annotations.Nullable;

public class CommandHandler<T extends Command<T, ?, ?, ?>, C extends CommandContext<S>, S> implements CommandContextHandler<C> {

	private T command;
	private Map<T, ? extends CommandContextHandler<C>> subCommandHandlers;
	@Nullable
	private ContextHandler<C> executorHandler;
	private CommandContextSupplier<C, S> contextSupplier;

	public CommandHandler(T command, Map<T, ? extends CommandContextHandler<C>> subCommandHandlers, @Nullable ContextHandler<C> executorHandler, CommandContextSupplier<C, S> contextSupplier) {
		this.command = command;
		this.subCommandHandlers = subCommandHandlers;
		this.executorHandler = executorHandler;
		this.contextSupplier = contextSupplier;
	}

	@Override
	public void handle(C context) {
		CommandSender<S> sender = context.getSender();
		MessageReceiver receiver = sender.getReceiver();
		Permission permission = context.getPermission();
		if (!sender.hasPermission(permission)) {
			receiver.receivePlain("You do not have permission.");
			return;
		}
		List<String> arguments = context.getArguments();
		int argumentsLength = arguments.size();
		if (arguments.isEmpty()) {
			execute(context, argumentsLength);
			return;
		}
		String firstArgument = arguments.get(0);
		List<T> subCommands = this.command.getSubCommands();
		T subCommand = subCommands.stream()
			.filter(element -> isMatching(firstArgument, element))
			.findFirst()
			.orElse(null);
		if (subCommand == null) {
			execute(context, argumentsLength);
			return;
		}
		CommandContextHandler<C> subCommandHandler = this.subCommandHandlers.get(subCommand);
		if (subCommandHandler == null) {
			execute(context, argumentsLength);
			return;
		}
		C subCommandContext = createSubCommandContext(arguments, context, subCommand);
		subCommandHandler.handle(subCommandContext);
	}

	private void execute(C context, int argumentsSize) {
		if (this.executorHandler == null) {
			getUsage(context);
			return;
		}
		if (!checkLength(argumentsSize)) {
			CommandSender<S> sender = context.getSender();
			MessageReceiver receiver = sender.getReceiver();
			String usage = getUsage(this.command);
			receiver.receivePlain(usage);
			return;
		}
		Object instance = this.command.createInstance();
		this.executorHandler.handle(context, instance);
	}

	private C createSubCommandContext(List<String> subCommandArguments, C context, T subCommand) {
		CommandSender<S> sender = context.getSender();
		int size = subCommandArguments.size();
		List<String> arguments = subCommandArguments.subList(1, size);
		Permission permission = createSubCommandPermission(context, subCommand);
		return this.contextSupplier.supply(sender, arguments, permission);
	}

	private Permission createSubCommandPermission(C context, T subCommand) {
		Permission permission = context.getPermission();
		CommandProperties properties = subCommand.getProperties();
		Permission subCommandPermission = properties.getPermission();
		return permission.withLast(subCommandPermission);
	}

	private boolean checkLength(int argumentsLength) {
		List<? extends Argument> commandArguments = this.command.getArguments();
		long minimumSize = commandArguments.stream()
			.filter(Argument::isNotOptional)
			.count();
		if (commandArguments.stream().anyMatch(Argument::isList)) {
			return argumentsLength >= minimumSize;
		}
		int maximumSize = commandArguments.size();
		return argumentsLength >= minimumSize && argumentsLength <= maximumSize;
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

	private void getUsage(C context) {
		StringBuilder builder = new StringBuilder();
		CommandProperties properties = this.command.getProperties();
		String name = properties.getName();
		builder.append(name);
		String description = properties.getDescription();
		if (description != null) {
			builder.append(" - " + description);
		}
		builder.append(".");
		String usage = builder.toString();
		CommandSender<S> sender = context.getSender();
		MessageReceiver receiver = sender.getReceiver();
		receiver.receivePlain(usage);
		List<T> subCommands = this.command.getSubCommands();
		Permission contextPermission = context.getPermission();
		for (T subCommand : subCommands) {
			CommandProperties subCommandProperties = subCommand.getProperties();
			Permission subCommandPermission = subCommandProperties.getPermission();
			Permission permission = contextPermission.withLast(subCommandPermission);
			if (sender.hasPermission(permission)) {
				String subCommandUsage = getUsage(subCommand);
				receiver.receivePlain(subCommandUsage);
			}
		}
	}

	private String getUsage(T command) {
		StringBuilder builder = new StringBuilder();
		CommandProperties properties = command.getProperties();
		String name = properties.getName();
		builder.append(name);
		List<? extends Argument> arguments = command.getArguments();
		for (Argument argument : arguments) {
			builder.append(" ");
			String usage = getUsage(argument);
			builder.append(usage);
		}
		String description = properties.getDescription();
		if (description != null) {
			builder.append(" - " + description);
		}
		builder.append(".");
		return builder.toString();
	}

	private String getUsage(Argument argument) {
		StringBuilder builder = new StringBuilder();
		String argumentName = argument.getName();
		boolean optional = argument.isOptional();
		builder.append(optional ? '[' : '<');
		builder.append(argumentName);
		if (argument.isList()) {
			builder.append("...");
		}
		builder.append(optional ? ']' : '>');
		return builder.toString();
	}

	protected T getCommand() {
		return this.command;
	}
}

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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.command.codec.completion.CompletionCodec;
import net.mcparkour.anfodis.command.context.CommandSender;
import net.mcparkour.anfodis.command.mapper.CompletionCommand;
import net.mcparkour.anfodis.command.mapper.argument.CompletionArgument;
import net.mcparkour.anfodis.command.mapper.properties.CommandProperties;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.craftmon.permission.PermissionBuilder;
import org.jetbrains.annotations.Nullable;

public class CompletionHandler<T extends CompletionCommand<T, ?, ?, ?>, C extends CompletionContext<S>, S> implements CompletionContextHandler<C> {

	private T command;
	private CodecRegistry<CompletionCodec> completionCodecRegistry;
	private Map<T, ? extends CompletionContextHandler<C>> subCommandHandlerMap;
	private CommandContextSupplier<C, S> contextSupplier;

	public CompletionHandler(T command, CodecRegistry<CompletionCodec> completionCodecRegistry, Map<T, ? extends CompletionContextHandler<C>> subCommandHandlerMap, CommandContextSupplier<C, S> contextSupplier) {
		this.command = command;
		this.completionCodecRegistry = completionCodecRegistry;
		this.subCommandHandlerMap = subCommandHandlerMap;
		this.contextSupplier = contextSupplier;
	}

	@Override
	public List<String> handle(C context) {
		List<String> arguments = context.getArguments();
		if (arguments.size() < 2) {
			return getCompletions(context);
		}
		String firstArgument = arguments.get(0);
		List<T> subCommands = this.command.getSubCommands();
		T subCommand = subCommands.stream()
			.filter(item -> isMatching(firstArgument, item))
			.findFirst()
			.orElse(null);
		if (subCommand == null) {
			return getCompletions(context);
		}
		CompletionContextHandler<C> handler = this.subCommandHandlerMap.get(subCommand);
		if (handler == null) {
			return getCompletions(context);
		}
		C subCommandContext = createSubCommandContext(arguments, context, subCommand);
		return handler.handle(subCommandContext);
	}

	private C createSubCommandContext(List<String> subCommandArguments, C context, T subCommand) {
		CommandSender<S> sender = context.getSender();
		int size = subCommandArguments.size();
		List<String> arguments = subCommandArguments.subList(1, size);
		Permission permission = createSubCommandPermission(context, subCommand);
		return this.contextSupplier.supply(sender, arguments, permission);
	}

	@Nullable
	private Permission createSubCommandPermission(C context, T subCommand) {
		Permission permission = context.getPermission();
		CommandProperties properties = subCommand.getProperties();
		String permissionName = properties.getPermission();
		if (permissionName == null || permission == null) {
			return permission;
		}
		return new PermissionBuilder()
			.with(permission)
			.node(permissionName)
			.build();
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

	private List<String> getCompletions(C context) {
		List<String> arguments = context.getArguments();
		List<String> completions = new ArrayList<>(0);
		if (arguments.size() == 1) {
			List<String> subCommandsCompletions = getSubCommandsCompletions(context);
			completions.addAll(subCommandsCompletions);
		}
		CommandSender<?> sender = context.getSender();
		Permission permission = context.getPermission();
		if (permission == null || sender.hasPermission(permission)) {
			List<String> executorCompletions = handleExecutor(context);
			completions.addAll(executorCompletions);
		}
		return completions;
	}

	private List<String> getSubCommandsCompletions(C context) {
		List<String> arguments = context.getArguments();
		String firstArgument = arguments.get(0);
		List<String> completions = new ArrayList<>(0);
		for (T subCommand : this.command.getSubCommands()) {
			CommandProperties properties = subCommand.getProperties();
			String commandPermission = properties.getPermission();
			if (commandPermission != null && hasPermission(context, commandPermission)) {
				String name = properties.getName();
				if (name.startsWith(firstArgument)) {
					completions.add(name);
				}
				properties.getAliases()
					.stream()
					.filter(alias -> alias.startsWith(firstArgument))
					.forEach(completions::add);
			}
		}
		return completions;
	}

	private boolean hasPermission(C context, String commandPermission) {
		Permission permission = context.getPermission();
		if (permission == null) {
			return true;
		}
		Permission newPermission = new PermissionBuilder()
			.with(permission)
			.node(commandPermission)
			.build();
		CommandSender<?> sender = context.getSender();
		return sender.hasPermission(newPermission);
	}

	private List<String> handleExecutor(C context) {
		List<String> arguments = context.getArguments();
		int argumentsCount = arguments.size();
		List<? extends CompletionArgument> commandArguments = this.command.getArguments();
		if (!arguments.isEmpty() && !commandArguments.isEmpty() && argumentsCount <= commandArguments.size()) {
			CompletionArgument commandArgument = commandArguments.get(argumentsCount - 1);
			CompletionCodec codec = commandArgument.getCompletionCodec(this.completionCodecRegistry);
			if (codec != null) {
				String argument = arguments.get(argumentsCount - 1);
				return codec.getCompletions(context)
					.stream()
					.filter(completion -> completion.startsWith(argument))
					.collect(Collectors.toUnmodifiableList());
			}
		}
		return new ArrayList<>(0);
	}
}

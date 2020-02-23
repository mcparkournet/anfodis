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
import net.mcparkour.anfodis.command.codec.completion.CompletionCodec;
import net.mcparkour.anfodis.command.mapper.Command;
import net.mcparkour.anfodis.command.mapper.argument.CompletionArgument;
import net.mcparkour.anfodis.command.mapper.properties.CommandProperties;
import net.mcparkour.anfodis.handler.ReturningHandler;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.craftmon.permission.PermissionBuilder;
import org.jetbrains.annotations.Nullable;

public class CompletionHandler implements ReturningHandler<List<String>> {

	private Command<? extends CompletionArgument<?>, ?, ?> command;
	private CompletionContext context;
	private CodecRegistry<CompletionCodec> completionCodecRegistry;

	public CompletionHandler(Command<? extends CompletionArgument<?>, ?, ?> command, CompletionContext context, CodecRegistry<CompletionCodec> completionCodecRegistry) {
		this.command = command;
		this.context = context;
		this.completionCodecRegistry = completionCodecRegistry;
	}

	@Override
	public List<String> handle() {
		CommandSender sender = this.context.getSender();
		List<String> arguments = this.context.getArguments();
		if (arguments.size() >= 2) {
			String firstArgument = arguments.get(0);
			for (Command<? extends CompletionArgument<?>, ?, ?> subCommand : this.command.getSubCommands()) {
				if (isMatching(firstArgument, subCommand)) {
					int size = arguments.size();
					List<String> subArguments = arguments.subList(1, size);
					Permission permission = getPermission(subCommand);
					CompletionContext context = new CompletionContext(sender, subArguments, permission);
					ReturningHandler<List<String>> handler = new CompletionHandler(subCommand, context, this.completionCodecRegistry);
					return handler.handle();
				}
			}
		}
		return getCompletions();
	}

	private boolean isMatching(String argument, Command<? extends CompletionArgument<?>, ?, ?> command) {
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
	private Permission getPermission(Command<? extends CompletionArgument<?>, ?, ?> command) {
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

	private List<String> getCompletions() {
		List<String> arguments = this.context.getArguments();
		List<String> completions = new ArrayList<>(0);
		if (arguments.size() == 1) {
			List<String> subCommandsCompletions = getSubCommandsCompletions();
			completions.addAll(subCommandsCompletions);
		}
		CommandSender sender = this.context.getSender();
		Permission permission = this.context.getPermission();
		if (permission == null || sender.hasPermission(permission)) {
			List<String> executorCompletions = handleExecutor();
			completions.addAll(executorCompletions);
		}
		return completions;
	}

	private List<String> getSubCommandsCompletions() {
		List<String> arguments = this.context.getArguments();
		String firstArgument = arguments.get(0);
		List<String> completions = new ArrayList<>(0);
		for (Command<?, ?, ?> subCommand : this.command.getSubCommands()) {
			CommandProperties<?> properties = subCommand.getProperties();
			String commandPermission = properties.getPermission();
			if (hasPermission(commandPermission)) {
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

	private boolean hasPermission(@Nullable String commandPermission) {
		Permission permission = this.context.getPermission();
		if (permission == null) {
			return true;
		}
		if (commandPermission == null) {
			return false;
		}
		Permission newPermission = new PermissionBuilder()
			.with(permission)
			.node(commandPermission)
			.build();
		CommandSender sender = this.context.getSender();
		return sender.hasPermission(newPermission);
	}

	private List<String> handleExecutor() {
		List<String> arguments = this.context.getArguments();
		int argumentsCount = arguments.size();
		List<? extends CompletionArgument<?>> commandArguments = this.command.getArguments();
		if (!arguments.isEmpty() && !commandArguments.isEmpty() && argumentsCount <= commandArguments.size()) {
			CompletionArgument<?> commandArgument = commandArguments.get(argumentsCount - 1);
			CompletionCodec codec = commandArgument.getCompletionCodec(this.completionCodecRegistry);
			if (codec != null) {
				String argument = arguments.get(argumentsCount - 1);
				return codec.getCompletions(this.context)
					.stream()
					.filter(completion -> completion.startsWith(argument))
					.collect(Collectors.toUnmodifiableList());
			}
		}
		return new ArrayList<>(0);
	}
}

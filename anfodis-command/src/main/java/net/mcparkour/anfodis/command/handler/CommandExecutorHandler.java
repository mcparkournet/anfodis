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
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.mapper.Command;
import net.mcparkour.anfodis.command.mapper.argument.Argument;
import net.mcparkour.anfodis.command.mapper.context.Context;
import net.mcparkour.anfodis.mapper.executor.Executor;
import net.mcparkour.anfodis.mapper.injection.Injection;
import net.mcparkour.anfodis.result.Result;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.intext.translation.Translations;

public class CommandExecutorHandler<T extends Command<?, ?, ?>> extends CommandHandler<T> {

	private Executor executor;
	private Object commandInstance;

	public CommandExecutorHandler(T command, CommandContext context, Translations translations, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, Executor executor) {
		super(command, context, translations, injectionCodecRegistry, argumentCodecRegistry);
		this.executor = executor;
		this.commandInstance = command.createInstance();
	}

	@Override
	public void handle() {
		try {
			setInjections();
			setArguments();
			setContext();
			execute();
		} catch (ArgumentParseException exception) {
			Argument<?> argument = exception.getArgument();
			CommandContext context = getContext();
			CommandSender sender = context.getSender();
			sender.sendMessage("Could not parse the argument " + argument.getName() + ".");
		}
	}

	private void setInjections() {
		Command<?, ?, ?> command = getCommand();
		CodecRegistry<InjectionCodec<?>> injectionCodecRegistry = getInjectionCodecRegistry();
		for (Injection injection : command.getInjections()) {
			InjectionCodec<?> codec = injection.getCodec(injectionCodecRegistry);
			Object codecInjection = codec.getInjection();
			injection.setInjectionField(this.commandInstance, codecInjection);
		}
	}

	private void setArguments() {
		CommandContext context = getContext();
		List<String> arguments = context.getArguments();
		Command<?, ?, ?> command = getCommand();
		List<? extends Argument<?>> commandArguments = command.getArguments();
		CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry = getArgumentCodecRegistry();
		for (int index = 0; index < arguments.size(); index++) {
			Argument<?> commandArgument = commandArguments.get(index);
			Class<?> type = commandArgument.getFieldType();
			if (type.isAssignableFrom(List.class)) {
				setListArgument(commandArgument, index);
				return;
			}
			String argument = arguments.get(index);
			ArgumentCodec<?> codec = commandArgument.getArgumentCodec(argumentCodecRegistry);
			Object parsedArgument = codec.parse(argument);
			if (parsedArgument == null) {
				throw new ArgumentParseException(commandArgument);
			}
			commandArgument.setArgumentField(this.commandInstance, parsedArgument);
		}
	}

	private void setListArgument(Argument<?> commandArgument, int startIndex) {
		CommandContext context = getContext();
		List<String> arguments = context.getArguments();
		int size = arguments.size();
		CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry = getArgumentCodecRegistry();
		ArgumentCodec<?> codec = commandArgument.getGenericTypeArgumentCodec(argumentCodecRegistry, 0);
		List<Object> list = new ArrayList<>(size - startIndex);
		for (int index = startIndex; index < size; index++) {
			String argument = arguments.get(index);
			Object parsedArgument = codec.parse(argument);
			if (parsedArgument == null) {
				throw new ArgumentParseException(commandArgument);
			}
			list.add(parsedArgument);
		}
		commandArgument.setArgumentField(this.commandInstance, list);
	}

	private void setContext() {
		Command<?, ?, ?> command = getCommand();
		Context<?> commandContext = command.getContext();
		CommandContext context = getContext();
		List<String> arguments = context.getArguments();
		commandContext.setArgumentsField(this.commandInstance, arguments);
		Permission permission = context.getPermission();
		commandContext.setRequiredPermissionField(this.commandInstance, permission);
		CommandSender sender = context.getSender();
		Object rawSender = sender.getRawSender();
		commandContext.setSenderField(this.commandInstance, rawSender);
	}

	private void execute() {
		this.executor.invokeBefore(this.commandInstance);
		Object invokeResult = this.executor.invokeExecutor(this.commandInstance);
		if (invokeResult instanceof Result) {
			Result result = (Result) invokeResult;
			result.onResult();
		}
		this.executor.invokeAfter(this.commandInstance);
	}
}

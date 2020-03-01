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
import net.mcparkour.anfodis.handler.Handler;
import net.mcparkour.anfodis.mapper.executor.Executor;
import net.mcparkour.anfodis.mapper.injection.Injection;
import net.mcparkour.anfodis.result.Result;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.intext.translation.Translations;

public class CommandExecutorHandler<T extends Command<?, ?, ?>, C extends CommandContext> implements Handler {

	private T command;
	private C context;
	private Translations translations;
	private CodecRegistry<InjectionCodec<?>> injectionCodecRegistry;
	private CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry;
	private Object commandInstance;

	public CommandExecutorHandler(T command, C context, Translations translations, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry) {
		this.command = command;
		this.context = context;
		this.translations = translations;
		this.injectionCodecRegistry = injectionCodecRegistry;
		this.argumentCodecRegistry = argumentCodecRegistry;
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
			CommandSender sender = this.context.getSender();
			sender.sendMessage("Could not parse the argument " + argument.getName() + ".");
		}
	}

	private void setInjections() {
		for (Injection injection : this.command.getInjections()) {
			InjectionCodec<?> codec = injection.getCodec(this.injectionCodecRegistry);
			Object codecInjection = codec.getInjection();
			injection.setInjectionField(this.commandInstance, codecInjection);
		}
	}

	private void setArguments() {
		List<String> arguments = this.context.getArguments();
		List<? extends Argument<?>> commandArguments = this.command.getArguments();
		for (int index = 0; index < arguments.size(); index++) {
			Argument<?> commandArgument = commandArguments.get(index);
			Class<?> type = commandArgument.getFieldType();
			if (type.isAssignableFrom(List.class)) {
				setListArgument(commandArgument, index);
				return;
			}
			String argument = arguments.get(index);
			ArgumentCodec<?> codec = commandArgument.getArgumentCodec(this.argumentCodecRegistry);
			Object parsedArgument = codec.parse(argument);
			if (parsedArgument == null) {
				throw new ArgumentParseException(commandArgument);
			}
			commandArgument.setArgumentField(this.commandInstance, parsedArgument);
		}
	}

	private void setListArgument(Argument<?> commandArgument, int startIndex) {
		List<String> arguments = this.context.getArguments();
		int size = arguments.size();
		ArgumentCodec<?> codec = commandArgument.getGenericTypeArgumentCodec(this.argumentCodecRegistry, 0);
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
		Context<?> commandContext = this.command.getContext();
		List<String> arguments = this.context.getArguments();
		commandContext.setArgumentsField(this.commandInstance, arguments);
		Permission permission = this.context.getPermission();
		commandContext.setRequiredPermissionField(this.commandInstance, permission);
		CommandSender sender = this.context.getSender();
		Object rawSender = sender.getRawSender();
		commandContext.setSenderField(this.commandInstance, rawSender);
	}

	private void execute() {
		Executor executor = this.command.getExecutor();
		executor.invokeBefore(this.commandInstance);
		Object invokeResult = executor.invokeExecutor(this.commandInstance);
		if (invokeResult instanceof Result) {
			Result result = (Result) invokeResult;
			result.onResult();
		}
		executor.invokeAfter(this.commandInstance);
	}

	public T getCommand() {
		return this.command;
	}

	public C getContext() {
		return this.context;
	}

	public Translations getTranslations() {
		return this.translations;
	}

	public CodecRegistry<InjectionCodec<?>> getInjectionCodecRegistry() {
		return this.injectionCodecRegistry;
	}

	public CodecRegistry<ArgumentCodec<?>> getArgumentCodecRegistry() {
		return this.argumentCodecRegistry;
	}

	public Object getCommandInstance() {
		return this.commandInstance;
	}
}

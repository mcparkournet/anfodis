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
import net.mcparkour.anfodis.handler.RootHandler;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.intext.translation.Translations;

public class CommandExecutorHandler<T extends Command<?, ?, ?>, C extends CommandContext> extends RootHandler<T> {

	private C context;
	private Translations translations;
	private CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry;

	public CommandExecutorHandler(T root, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, C context, Translations translations, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry) {
		super(root, injectionCodecRegistry);
		this.context = context;
		this.translations = translations;
		this.argumentCodecRegistry = argumentCodecRegistry;
	}

	@Override
	public void handle() {
		try {
			setArguments();
			setContext();
			super.handle();
		} catch (ArgumentParseException exception) {
			Argument<?> argument = exception.getArgument();
			CommandSender sender = this.context.getSender();
			sender.sendMessage("Could not parse the argument " + argument.getName() + ".");
		}
	}

	private void setArguments() {
		List<String> arguments = this.context.getArguments();
		T command = getRoot();
		List<? extends Argument<?>> commandArguments = command.getArguments();
		Object commandInstance = getInstance();
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
			commandArgument.setArgumentField(commandInstance, parsedArgument);
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
		Object commandInstance = getInstance();
		commandArgument.setArgumentField(commandInstance, list);
	}

	private void setContext() {
		T command = getRoot();
		Context<?> commandContext = command.getContext();
		Object commandInstance = getInstance();
		List<String> arguments = this.context.getArguments();
		commandContext.setArgumentsField(commandInstance, arguments);
		Permission permission = this.context.getPermission();
		commandContext.setRequiredPermissionField(commandInstance, permission);
		CommandSender sender = this.context.getSender();
		Object rawSender = sender.getRawSender();
		commandContext.setSenderField(commandInstance, rawSender);
	}

	protected C getContext() {
		return this.context;
	}

	protected Translations getTranslations() {
		return this.translations;
	}

	protected CodecRegistry<ArgumentCodec<?>> getArgumentCodecRegistry() {
		return this.argumentCodecRegistry;
	}
}

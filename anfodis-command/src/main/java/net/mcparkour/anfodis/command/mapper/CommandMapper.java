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

package net.mcparkour.anfodis.command.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import net.mcparkour.anfodis.command.mapper.argument.Argument;
import net.mcparkour.anfodis.command.mapper.argument.ArgumentMapper;
import net.mcparkour.anfodis.command.mapper.context.Context;
import net.mcparkour.anfodis.command.mapper.context.ContextMapper;
import net.mcparkour.anfodis.command.mapper.properties.CommandProperties;
import net.mcparkour.anfodis.command.mapper.properties.CommandPropertiesMapper;
import net.mcparkour.anfodis.command.mapper.subcommand.SubCommand;
import net.mcparkour.anfodis.command.mapper.subcommand.SubCommandMapper;
import net.mcparkour.anfodis.mapper.RootMapper;
import net.mcparkour.anfodis.mapper.executor.Executor;
import net.mcparkour.anfodis.mapper.injection.Injection;

public class CommandMapper<T extends Command<T, A, C, P>, A extends Argument<?>, C extends Context<?>, P extends CommandProperties<?>> implements RootMapper<T> {

	private static final SubCommandMapper SUB_COMMAND_MAPPER = new SubCommandMapper();

	private ArgumentMapper<A, ?> argumentMapper;
	private ContextMapper<C, ?> contextMapper;
	private CommandPropertiesMapper<P, ?> propertiesMapper;
	private CommandMerger<T, A, C, P> commandMerger;

	public CommandMapper(ArgumentMapper<A, ?> argumentMapper, ContextMapper<C, ?> contextMapper, CommandPropertiesMapper<P, ?> propertiesMapper, CommandMerger<T, A, C, P> commandMerger) {
		this.argumentMapper = argumentMapper;
		this.contextMapper = contextMapper;
		this.propertiesMapper = propertiesMapper;
		this.commandMerger = commandMerger;
	}

	@Override
	public T map(Class<?> annotatedClass) {
		Field[] fields = annotatedClass.getDeclaredFields();
		Method[] methods = annotatedClass.getDeclaredMethods();
		Constructor<?> constructor = getConstructor(annotatedClass);
		List<Injection> injections = getInjections(fields);
		Executor executor = getExecutor(methods);
		List<A> arguments = getArguments(fields);
		C context = getContext(fields);
		P properties = getProperties(annotatedClass);
		List<T> subCommands = getSubCommands(fields);
		return this.commandMerger.merge(constructor, injections, executor, arguments, context, properties, subCommands);
	}

	private List<A> getArguments(Field[] fields) {
		return this.argumentMapper.map(fields);
	}

	private C getContext(Field[] fields) {
		return this.contextMapper.map(fields);
	}

	private P getProperties(Class<?> commandClass) {
		return this.propertiesMapper.map(commandClass);
	}

	private List<T> getSubCommands(Field[] fields) {
		return SUB_COMMAND_MAPPER.map(fields)
			.stream()
			.map(SubCommand::getFieldType)
			.map(this::map)
			.collect(Collectors.toUnmodifiableList());
	}
}

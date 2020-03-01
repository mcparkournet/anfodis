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
import java.util.List;
import net.mcparkour.anfodis.command.mapper.argument.Argument;
import net.mcparkour.anfodis.command.mapper.context.Context;
import net.mcparkour.anfodis.command.mapper.properties.CommandProperties;
import net.mcparkour.anfodis.mapper.Root;
import net.mcparkour.anfodis.mapper.executor.Executor;
import net.mcparkour.anfodis.mapper.injection.Injection;

public class Command<A extends Argument<?>, C extends Context<?>, P extends CommandProperties<?>> extends Root {

	private List<A> arguments;
	private C context;
	private P properties;
	private List<? extends Command<A, C, P>> subCommands;

	public Command(Constructor<?> constructor, List<Injection> injections, Executor executor, List<A> arguments, C context, P properties, List<? extends Command<A, C, P>> subCommands) {
		super(constructor, injections, executor);
		this.arguments = arguments;
		this.context = context;
		this.properties = properties;
		this.subCommands = subCommands;
	}

	public List<A> getArguments() {
		return this.arguments;
	}

	public C getContext() {
		return this.context;
	}

	public P getProperties() {
		return this.properties;
	}

	public List<? extends Command<A, C, P>> getSubCommands() {
		return this.subCommands;
	}
}
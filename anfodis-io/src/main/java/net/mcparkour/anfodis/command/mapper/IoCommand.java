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

import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.util.List;

import net.mcparkour.anfodis.command.context.IoCommandContext;
import net.mcparkour.anfodis.command.mapper.argument.IoArgument;
import net.mcparkour.anfodis.command.mapper.properties.IoCommandProperties;
import net.mcparkour.anfodis.mapper.executor.Executor;
import net.mcparkour.anfodis.mapper.injection.Injection;
import net.mcparkour.anfodis.mapper.transform.Transform;

public class IoCommand extends Command<IoCommand, IoArgument, IoCommandProperties, IoCommandContext, OutputStreamWriter> {

    public IoCommand(
        final Constructor<?> constructor,
        final List<Injection> injections,
        final List<Transform<IoCommandContext>> transforms,
        final Executor executor,
        final List<IoArgument> arguments,
        final IoCommandProperties properties,
        final List<IoCommand> subCommands
    ) {
        super(constructor, injections, transforms, executor, arguments, properties, subCommands);
    }
}

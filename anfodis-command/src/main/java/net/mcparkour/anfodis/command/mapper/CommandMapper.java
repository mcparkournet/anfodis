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
import net.mcparkour.anfodis.command.context.CommandContext;
import net.mcparkour.anfodis.command.mapper.argument.Argument;
import net.mcparkour.anfodis.command.mapper.argument.ArgumentMapper;
import net.mcparkour.anfodis.command.mapper.properties.CommandProperties;
import net.mcparkour.anfodis.command.mapper.properties.CommandPropertiesMapper;
import net.mcparkour.anfodis.command.mapper.subcommand.SubCommand;
import net.mcparkour.anfodis.command.mapper.subcommand.SubCommandMapper;
import net.mcparkour.anfodis.mapper.AbstractRootMapper;
import net.mcparkour.anfodis.mapper.executor.Executor;
import net.mcparkour.anfodis.mapper.injection.Injection;
import net.mcparkour.anfodis.mapper.transform.Transform;

public class CommandMapper<T extends Command<T, A, P, C, S>, A extends Argument, P extends CommandProperties, C extends CommandContext<T, S>, S> extends AbstractRootMapper<T, C> {

    private static final SubCommandMapper SUB_COMMAND_MAPPER = new SubCommandMapper();

    private final ArgumentMapper<A, ?> argumentMapper;
    private final CommandPropertiesMapper<P, ?> propertiesMapper;
    private final CommandMerger<T, A, P, C, S> commandMerger;

    public CommandMapper(
        final ArgumentMapper<A, ?> argumentMapper,
        final CommandPropertiesMapper<P, ?> propertiesMapper,
        final CommandMerger<T, A, P, C, S> commandMerger
    ) {
        this.argumentMapper = argumentMapper;
        this.propertiesMapper = propertiesMapper;
        this.commandMerger = commandMerger;
    }

    @Override
    public T map(final Class<?> annotatedClass) {
        Field[] fields = annotatedClass.getDeclaredFields();
        Method[] methods = annotatedClass.getDeclaredMethods();
        Constructor<?> constructor = getConstructor(annotatedClass);
        List<Injection> injections = getInjections(fields);
        List<Transform<C>> transforms = getTransforms(fields);
        Executor executor = getExecutor(methods);
        List<A> arguments = getArguments(fields);
        P properties = getProperties(annotatedClass);
        List<T> subCommands = getSubCommands(fields);
        return this.commandMerger.merge(constructor, injections, transforms, executor, arguments, properties, subCommands);
    }

    private List<A> getArguments(final Field[] fields) {
        return this.argumentMapper.map(fields);
    }

    private P getProperties(final Class<?> commandClass) {
        return this.propertiesMapper.map(commandClass);
    }

    private List<T> getSubCommands(final Field[] fields) {
        return SUB_COMMAND_MAPPER.map(fields)
            .stream()
            .map(SubCommand::getFieldType)
            .map(this::map)
            .collect(Collectors.toUnmodifiableList());
    }
}

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
import java.util.stream.Collectors;
import net.mcparkour.anfodis.command.mapper.argument.Argument;
import net.mcparkour.anfodis.command.mapper.context.Context;
import net.mcparkour.anfodis.command.mapper.properties.CommandProperties;
import net.mcparkour.anfodis.mapper.Root;
import net.mcparkour.anfodis.mapper.executor.Executor;
import net.mcparkour.anfodis.mapper.injection.Injection;
import net.mcparkour.craftmon.permission.Permission;

public class Command<T extends Command<T, A, C, P>, A extends Argument, C extends Context, P extends CommandProperties> extends Root {

    private final List<A> arguments;
    private final C context;
    private final P properties;
    private final List<T> subCommands;

    public Command(final Constructor<?> constructor, final List<Injection> injections, final Executor executor, final List<A> arguments, final C context, final P properties, final List<T> subCommands) {
        super(constructor, injections, executor);
        this.arguments = arguments;
        this.context = context;
        this.properties = properties;
        this.subCommands = subCommands;
    }

    public String getUsageHeader() {
        String name = this.properties.getName();
        String descriptionUsage = getDescriptionUsage();
        return name + descriptionUsage;
    }

    public String getUsage() {
        String name = this.properties.getName();
        String argumentsUsage = getArgumentsUsage();
        String descriptionUsage = getDescriptionUsage();
        return name + " " + argumentsUsage + descriptionUsage;
    }

    private String getArgumentsUsage() {
        return this.arguments.stream()
            .map(Argument::getUsage)
            .collect(Collectors.joining(" "));
    }

    private String getDescriptionUsage() {
        String description = this.properties.getDescription();
        return description.isEmpty() ? "" : " - " + description + ".";
    }

    public Permission getPermission(final Permission prefix) {
        Permission permission = this.properties.getPermission();
        return prefix.withLast(permission);
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

    public List<T> getSubCommands() {
        return List.copyOf(this.subCommands);
    }
}

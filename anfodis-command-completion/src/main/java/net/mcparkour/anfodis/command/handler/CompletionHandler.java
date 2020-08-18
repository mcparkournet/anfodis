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
import net.mcparkour.anfodis.command.context.Permissible;
import net.mcparkour.anfodis.command.mapper.CompletionCommand;
import net.mcparkour.anfodis.command.mapper.argument.CompletionArgument;
import net.mcparkour.anfodis.command.mapper.properties.CommandProperties;
import net.mcparkour.craftmon.permission.Permission;

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
        Permissible permissible = context.getSender();
        Permission permission = context.getPermission();
        if (!permissible.hasPermission(permission)) {
            return List.of();
        }
        List<String> arguments = context.getArguments();
        if (arguments.size() <= 1) {
            return getCompletions(context, arguments);
        }
        String firstArgument = arguments.get(0);
        List<T> subCommands = this.command.getSubCommands();
        T subCommand = subCommands.stream()
            .filter(item -> isMatching(firstArgument, item))
            .findFirst()
            .orElse(null);
        if (subCommand == null) {
            return getCompletions(context, arguments);
        }
        CompletionContextHandler<C> handler = this.subCommandHandlerMap.get(subCommand);
        if (handler == null) {
            return getCompletions(context, arguments);
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

    private Permission createSubCommandPermission(C context, T subCommand) {
        Permission permission = context.getPermission();
        CommandProperties properties = subCommand.getProperties();
        Permission subCommandPermission = properties.getPermission();
        return permission.withLast(subCommandPermission);
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

    private List<String> getCompletions(C context, List<String> arguments) {
        List<String> completions = new ArrayList<>(0);
        if (arguments.size() == 1) {
            List<String> subCommandsCompletions = getSubCommandsCompletions(context, arguments);
            completions.addAll(subCommandsCompletions);
        }
        Permissible permissible = context.getSender();
        Permission permission = context.getPermission();
        if (!arguments.isEmpty() && permissible.hasPermission(permission)) {
            List<String> executorCompletions = handleExecutor(context, arguments);
            completions.addAll(executorCompletions);
        }
        return completions;
    }

    private List<String> getSubCommandsCompletions(C context, List<String> arguments) {
        Permission contextPermission = context.getPermission();
        Permissible permissible = context.getSender();
        String firstArgument = arguments.get(0);
        List<String> completions = new ArrayList<>(0);
        for (T subCommand : this.command.getSubCommands()) {
            CommandProperties properties = subCommand.getProperties();
            Permission subCommandPermission = properties.getPermission();
            Permission permission = contextPermission.withLast(subCommandPermission);
            if (permissible.hasPermission(permission)) {
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

    private List<String> handleExecutor(C context, List<String> arguments) {
        List<? extends CompletionArgument> commandArguments = this.command.getArguments();
        int argumentsCount = arguments.size();
        if (commandArguments.isEmpty() || argumentsCount > commandArguments.size()) {
            return List.of();
        }
        CompletionArgument commandArgument = commandArguments.get(argumentsCount - 1);
        CompletionCodec codec = commandArgument.getCompletionCodec(this.completionCodecRegistry);
        if (codec == null) {
            return List.of();
        }
        List<String> completions = codec.getCompletions(context);
        String argument = arguments.get(argumentsCount - 1);
        return completions.stream()
            .filter(completion -> completion.startsWith(argument))
            .collect(Collectors.toUnmodifiableList());
    }
}

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
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.mcparkour.anfodis.codec.registry.CodecRegistry;
import net.mcparkour.anfodis.command.argument.ArgumentContext;
import net.mcparkour.anfodis.command.codec.completion.CompletionCodec;
import net.mcparkour.anfodis.command.context.Permissible;
import net.mcparkour.anfodis.command.context.Sender;
import net.mcparkour.anfodis.command.lexer.Token;
import net.mcparkour.anfodis.command.mapper.CompletionCommand;
import net.mcparkour.anfodis.command.mapper.argument.Argument;
import net.mcparkour.anfodis.command.mapper.argument.CompletionArgument;
import net.mcparkour.anfodis.command.mapper.properties.CommandProperties;
import net.mcparkour.craftmon.permission.Permission;
import org.jetbrains.annotations.Nullable;

public class CompletionHandler<T extends CompletionCommand<T, ?, ?, ?, S>, C extends CompletionContext<T, S>, B extends CompletionContextBuilder<T, C, S>, S>
    implements CompletionContextBuilderHandler<B, C> {

    private final T command;
    private final CodecRegistry<CompletionCodec> completionCodecRegistry;
    private final Map<T, ? extends CompletionContextBuilderHandler<B, C>> subCommandHandlerMap;
    private final CommandContextCreator<T, C, S> contextCreator;

    public CompletionHandler(
        final T command,
        final CodecRegistry<CompletionCodec> completionCodecRegistry,
        final Map<T, ? extends CompletionContextBuilderHandler<B, C>> subCommandHandlerMap,
        final CommandContextCreator<T, C, S> contextCreator
    ) {
        this.command = command;
        this.completionCodecRegistry = completionCodecRegistry;
        this.subCommandHandlerMap = subCommandHandlerMap;
        this.contextCreator = contextCreator;
    }

    @Override
    public List<String> handle(final B contextBuilder) {
        Permissible permissible = contextBuilder.getSender();
        Permission permission = contextBuilder.getPermission();
        if (!permissible.hasPermission(permission)) {
            return List.of();
        }
        Optional<Token> firstTokenOptional = contextBuilder.peekArgument();
        int argumentsSize = contextBuilder.getArgumentsSize();
        if (firstTokenOptional.isEmpty() || argumentsSize == 1) {
            return getCompletions(contextBuilder);
        }
        Token firstToken = firstTokenOptional.get();
        String firstArgument = firstToken.getString();
        List<T> subCommands = this.command.getSubCommands();
        T subCommand = subCommands.stream()
            .filter(item -> isMatching(firstArgument, item))
            .findFirst()
            .orElse(null);
        if (subCommand == null) {
            return getCompletions(contextBuilder);
        }
        CompletionContextBuilderHandler<B, C> handler = this.subCommandHandlerMap.get(subCommand);
        if (handler == null) {
            return getCompletions(contextBuilder);
        }
        contextBuilder.pollArgument();
        contextBuilder.pushParent(subCommand);
        CommandProperties subCommandProperties = subCommand.getProperties();
        Permission subCommandPermission = subCommandProperties.getPermission();
        contextBuilder.permissionWithLast(subCommandPermission);
        return handler.handle(contextBuilder);
    }

    private boolean isMatching(final String argument, final T command) {
        CommandProperties properties = command.getProperties();
        String name = properties.getName();
        if (argument.equalsIgnoreCase(name)) {
            return true;
        }
        String lowerCaseArgument = argument.toLowerCase();
        Set<String> aliases = properties.getLowerCaseAliases();
        return aliases.contains(lowerCaseArgument);
    }

    private List<String> getCompletions(final B contextBuilder) {
        List<String> completions = new ArrayList<>(0);
        int argumentsSize = contextBuilder.getArgumentsSize();
        if (argumentsSize == 1) {
            List<String> subCommandsCompletions = getSubCommandsCompletions(contextBuilder);
            completions.addAll(subCommandsCompletions);
        }
        Permissible permissible = contextBuilder.getSender();
        Permission permission = contextBuilder.getPermission();
        if (argumentsSize != 0 && permissible.hasPermission(permission)) {
            List<String> executorCompletions = handleExecutor(contextBuilder);
            completions.addAll(executorCompletions);
        }
        return completions;
    }

    private List<String> getSubCommandsCompletions(final B contextBuilder) {
        Permission contextPermission = contextBuilder.getPermission();
        Permissible permissible = contextBuilder.getSender();
        Optional<Token> firstTokenOptional = contextBuilder.peekArgument();
        if (firstTokenOptional.isEmpty()) {
            return List.of();
        }
        Token firstToken = firstTokenOptional.get();
        String firstArgument = firstToken.getString();
        List<String> completions = new ArrayList<>(0);
        for (final T subCommand : this.command.getSubCommands()) {
            CommandProperties properties = subCommand.getProperties();
            Permission subCommandPermission = properties.getPermission();
            Permission permission = contextPermission.withLast(subCommandPermission);
            if (!permissible.hasPermission(permission)) {
                continue;
            }
            String name = properties.getName();
            if (name.startsWith(firstArgument)) {
                completions.add(name);
            }
            properties.getAliases()
                .stream()
                .filter(alias -> alias.startsWith(firstArgument))
                .forEach(completions::add);
        }
        return completions;
    }

    private List<String> handleExecutor(final B contextBuilder) {
        List<? extends CompletionArgument> completionArguments = this.command.getArguments();
        int argumentsSize = contextBuilder.getArgumentsSize();
        if (completionArguments.isEmpty() || argumentsSize > completionArguments.size()) {
            return List.of();
        }
        CompletionArgument completionArgument = completionArguments.get(argumentsSize - 1);
        if (!checkPermission(contextBuilder, completionArgument)) {
            return List.of();
        }
        Optional<CompletionCodec> optionalCodec = completionArgument.getCompletionCodec(this.completionCodecRegistry);
        if (optionalCodec.isEmpty()) {
            return List.of();
        }
        CompletionCodec codec = optionalCodec.get();
        C context = contextBuilder.build(this.contextCreator);
        ArgumentContext argumentContext = completionArgument.getContext();
        List<String> completions = codec.getCompletions(context, argumentContext);
        List<Token> arguments = context.getArguments();
        Token token = arguments.get(argumentsSize - 1);
        String argument = token.getString();
        return completions.stream()
            .filter(completion -> completion.startsWith(argument))
            .collect(Collectors.toUnmodifiableList());
    }

    private boolean checkPermission(final B contextBuilder, final CompletionArgument completionArgument) {
        ArgumentContext argumentContext = completionArgument.getContext();
        Permission argumentPermission = argumentContext.getPermission();
        if (argumentPermission.isEmpty()) {
            return true;
        }
        Permission contextPermission = contextBuilder.getPermission();
        Permission permission = contextPermission.withLast(argumentPermission);
        Permissible permissible = contextBuilder.getSender();
        return permissible.hasPermission(permission);
    }
}

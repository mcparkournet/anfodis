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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import net.mcparkour.anfodis.command.Messenger;
import net.mcparkour.anfodis.command.argument.ArgumentContext;
import net.mcparkour.anfodis.command.context.CommandContext;
import net.mcparkour.anfodis.command.context.CommandContextBuilder;
import net.mcparkour.anfodis.command.context.Permissible;
import net.mcparkour.anfodis.command.context.Sender;
import net.mcparkour.anfodis.command.lexer.Token;
import net.mcparkour.anfodis.command.mapper.Command;
import net.mcparkour.anfodis.command.mapper.argument.Argument;
import net.mcparkour.anfodis.command.mapper.properties.CommandProperties;
import net.mcparkour.anfodis.handler.ContextHandler;
import net.mcparkour.craftmon.permission.Permission;
import org.jetbrains.annotations.Nullable;

public class CommandHandler<T extends Command<T, ?, ?, C, S>, C extends CommandContext<T, S>, B extends CommandContextBuilder<T, C, S>, S, M extends Messenger<T, C, S>>
    implements CommandContextBuilderHandler<B, C> {

    private final T command;
    private final Map<T, ? extends CommandContextBuilderHandler<B, C>> subCommandHandlers;
    private final @Nullable ContextHandler<? super C> executorHandler;
    private final CommandContextCreator<T, C, S> contextCreator;
    private final M messenger;

    public CommandHandler(
        final T command,
        final Map<T, ? extends CommandContextBuilderHandler<B, C>> subCommandHandlers,
        final @Nullable ContextHandler<? super C> executorHandler,
        final CommandContextCreator<T, C, S> contextCreator,
        final M messenger
    ) {
        this.command = command;
        this.subCommandHandlers = subCommandHandlers;
        this.executorHandler = executorHandler;
        this.contextCreator = contextCreator;
        this.messenger = messenger;
    }

    @Override
    public void handle(final B contextBuilder) {
        Sender<S> sender = contextBuilder.getSender();
        Permission permission = contextBuilder.getPermission();
        if (!sender.hasPermission(permission)) {
            C context = contextBuilder.build(this.contextCreator);
            this.messenger.sendNoPermissionMessage(context, permission);
            return;
        }
        Optional<Token> firstTokenOptional = contextBuilder.peekArgument();
        if (firstTokenOptional.isEmpty()) {
            execute(contextBuilder);
            return;
        }
        Token firstToken = firstTokenOptional.get();
        String firstArgument = firstToken.getString();
        List<T> subCommands = this.command.getSubCommands();
        T subCommand = subCommands.stream()
            .filter(element -> isMatching(firstArgument, element))
            .findFirst()
            .orElse(null);
        if (subCommand == null) {
            execute(contextBuilder);
            return;
        }
        CommandContextBuilderHandler<B, C> subCommandHandler = this.subCommandHandlers.get(subCommand);
        if (subCommandHandler == null) {
            execute(contextBuilder);
            return;
        }
        contextBuilder.pollArgument();
        contextBuilder.pushParent(subCommand);
        CommandProperties subCommandProperties = subCommand.getProperties();
        Permission subCommandPermission = subCommandProperties.getPermission();
        contextBuilder.permissionWithLast(subCommandPermission);
        subCommandHandler.handle(contextBuilder);
    }

    private void execute(final B contextBuilder) {
        C context = contextBuilder.build(this.contextCreator);
        if (this.executorHandler == null) {
            this.messenger.sendSubCommandsUsageMessage(context, this.command);
            return;
        }
        int argumentsSize = contextBuilder.getArgumentsSize();
        if (!checkLength(argumentsSize)) {
            this.messenger.sendCommandUsageMessage(context, this.command);
            return;
        }
        Optional<Permission> argumentPermissionOptional = getMissingArgumentPermission(contextBuilder);
        if (argumentPermissionOptional.isPresent()) {
            Permission argumentPermission = argumentPermissionOptional.get();
            this.messenger.sendNoPermissionMessage(context, argumentPermission);
            return;
        }
        Object instance = this.command.createInstance();
        this.executorHandler.handle(context, instance);
    }

    private boolean checkLength(final int argumentsLength) {
        List<? extends Argument> commandArguments = this.command.getArguments();
        long minimumSize = commandArguments.stream()
            .filter(Argument::isNotOptional)
            .count();
        if (commandArguments.stream().anyMatch(Argument::isVariadicArgument)) {
            return argumentsLength >= minimumSize;
        }
        int maximumSize = commandArguments.size();
        return argumentsLength >= minimumSize && argumentsLength <= maximumSize;
    }

    private Optional<Permission> getMissingArgumentPermission(final B contextBuilder) {
        Permissible permissible = contextBuilder.getSender();
        Permission contextPermission = contextBuilder.getPermission();
        int argumentsSize = contextBuilder.getArgumentsSize();
        List<? extends Argument> commandArguments = this.command.getArguments();
        int commandArgumentSize = commandArguments.size();
        for (int index = 0; index < Math.min(argumentsSize, commandArgumentSize); index++) {
            Argument argument = commandArguments.get(index);
            ArgumentContext argumentContext = argument.getContext();
            Optional<Permission> argumentPermissionOptional = argumentContext.getPermission();
            if (argumentPermissionOptional.isEmpty()) {
                continue;
            }
            Permission argumentPermission = argumentPermissionOptional.get();
            Permission permission = contextPermission.withLast(argumentPermission);
            if (!permissible.hasPermission(permission)) {
                return Optional.of(permission);
            }
        }
        return Optional.empty();
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

    public T getCommand() {
        return this.command;
    }

    public CommandContextCreator<T, C, S> getContextCreator() {
        return this.contextCreator;
    }

    public M getMessenger() {
        return this.messenger;
    }
}

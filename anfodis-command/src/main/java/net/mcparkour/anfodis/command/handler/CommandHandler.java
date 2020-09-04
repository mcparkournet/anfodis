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
import java.util.Set;
import java.util.stream.Collectors;
import net.mcparkour.anfodis.command.Messenger;
import net.mcparkour.anfodis.command.context.CommandContext;
import net.mcparkour.anfodis.command.context.CommandSender;
import net.mcparkour.anfodis.command.context.Permissible;
import net.mcparkour.anfodis.command.lexer.Token;
import net.mcparkour.anfodis.command.mapper.Command;
import net.mcparkour.anfodis.command.mapper.argument.Argument;
import net.mcparkour.anfodis.command.mapper.properties.CommandProperties;
import net.mcparkour.anfodis.handler.ContextHandler;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.intext.message.MessageReceiver;
import org.jetbrains.annotations.Nullable;

public class CommandHandler<T extends Command<T, ?, ?, ?>, C extends CommandContext<S>, S, M extends Messenger<T, S>> implements CommandContextHandler<C> {

    private final T command;
    private final Map<T, ? extends CommandContextHandler<C>> subCommandHandlers;
    private final @Nullable ContextHandler<C> executorHandler;
    private final CommandContextSupplier<C, S> contextSupplier;
    private final M messenger;

    public CommandHandler(
        final T command,
        final Map<T, ? extends CommandContextHandler<C>> subCommandHandlers,
        @Nullable final ContextHandler<C> executorHandler,
        final CommandContextSupplier<C, S> contextSupplier,
        final M messenger
    ) {
        this.command = command;
        this.subCommandHandlers = subCommandHandlers;
        this.executorHandler = executorHandler;
        this.contextSupplier = contextSupplier;
        this.messenger = messenger;
    }

    @Override
    public void handle(final C context) {
        CommandSender<S> sender = context.getSender();
        Permission permission = context.getPermission();
        if (!sender.hasPermission(permission)) {
            this.messenger.sendNoPermissionMessage(sender, permission);
            return;
        }
        List<Token> arguments = context.getArguments();
        int argumentsLength = arguments.size();
        if (arguments.isEmpty()) {
            execute(context, argumentsLength);
            return;
        }
        Token firstToken = arguments.get(0);
        String firstArgument = firstToken.getString();
        List<T> subCommands = this.command.getSubCommands();
        T subCommand = subCommands.stream()
            .filter(element -> isMatching(firstArgument, element))
            .findFirst()
            .orElse(null);
        if (subCommand == null) {
            execute(context, argumentsLength);
            return;
        }
        CommandContextHandler<C> subCommandHandler = this.subCommandHandlers.get(subCommand);
        if (subCommandHandler == null) {
            execute(context, argumentsLength);
            return;
        }
        C subCommandContext = createSubCommandContext(subCommand, context);
        subCommandHandler.handle(subCommandContext);
    }

    private void execute(final C context, final int argumentsSize) {
        CommandSender<S> sender = context.getSender();
        Permission permission = context.getPermission();
        if (this.executorHandler == null) {
            this.messenger.sendSubCommandsUsageMessage(sender, permission, this.command);
            return;
        }
        if (!checkLength(argumentsSize)) {
            this.messenger.sendCommandUsageMessage(sender, this.command);
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

    private C createSubCommandContext(final T subCommand, final C context) {
        CommandSender<S> sender = context.getSender();
        List<Token> contextArguments = context.getArguments();
        int size = contextArguments.size();
        List<Token> arguments = contextArguments.subList(1, size);
        Permission contextPermission = context.getPermission();
        Permission permission = subCommand.getPermission(contextPermission);
        boolean asynchronous = context.isAsynchronous();
        return this.contextSupplier.supply(sender, arguments, permission, asynchronous);
    }

    public T getCommand() {
        return this.command;
    }

    public M getMessenger() {
        return this.messenger;
    }
}

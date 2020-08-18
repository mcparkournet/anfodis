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
import net.mcparkour.anfodis.command.context.CommandContext;
import net.mcparkour.anfodis.command.context.CommandSender;
import net.mcparkour.anfodis.command.context.Permissible;
import net.mcparkour.anfodis.command.mapper.Command;
import net.mcparkour.anfodis.command.mapper.argument.Argument;
import net.mcparkour.anfodis.command.mapper.properties.CommandProperties;
import net.mcparkour.anfodis.handler.ContextHandler;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.intext.message.MessageReceiver;
import org.jetbrains.annotations.Nullable;

public class CommandHandler<T extends Command<T, ?, ?, ?>, C extends CommandContext<S>, S> implements CommandContextHandler<C> {

    private T command;
    private Map<T, ? extends CommandContextHandler<C>> subCommandHandlers;
    @Nullable
    private ContextHandler<C> executorHandler;
    private CommandContextSupplier<C, S> contextSupplier;

    public CommandHandler(T command, Map<T, ? extends CommandContextHandler<C>> subCommandHandlers, @Nullable ContextHandler<C> executorHandler, CommandContextSupplier<C, S> contextSupplier) {
        this.command = command;
        this.subCommandHandlers = subCommandHandlers;
        this.executorHandler = executorHandler;
        this.contextSupplier = contextSupplier;
    }

    @Override
    public void handle(C context) {
        CommandSender<S> sender = context.getSender();
        MessageReceiver receiver = sender.getReceiver();
        Permission permission = context.getPermission();
        if (!sender.hasPermission(permission)) {
            receiver.receivePlain("You do not have permission.");
            return;
        }
        List<String> arguments = context.getArguments();
        int argumentsLength = arguments.size();
        if (arguments.isEmpty()) {
            execute(context, argumentsLength);
            return;
        }
        String firstArgument = arguments.get(0);
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

    private void execute(C context, int argumentsSize) {
        CommandSender<S> sender = context.getSender();
        MessageReceiver receiver = sender.getReceiver();
        Permission permission = context.getPermission();
        if (this.executorHandler == null) {
            String usage = getUsage(sender, permission);
            receiver.receivePlain(usage);
            return;
        }
        if (!checkLength(argumentsSize)) {
            String usage = this.command.getUsage();
            receiver.receivePlain(usage);
            return;
        }
        Object instance = this.command.createInstance();
        this.executorHandler.handle(context, instance);
    }

    private String getUsage(Permissible permissible, Permission contextPermission) {
        String header = this.command.getUsageHeader();
        String subCommandsUsage = getSubCommandsUsage(permissible, contextPermission);
        return header + '\n' + subCommandsUsage;
    }

    private String getSubCommandsUsage(Permissible permissible, Permission contextPermission) {
        List<T> subCommands = this.command.getSubCommands();
        return subCommands.stream()
            .filter(subCommand -> {
                Permission permission = subCommand.getPermission(contextPermission);
                return permissible.hasPermission(permission);
            })
            .map(T::getUsage)
            .collect(Collectors.joining("\n"));
    }

    private boolean checkLength(int argumentsLength) {
        List<? extends Argument> commandArguments = this.command.getArguments();
        long minimumSize = commandArguments.stream()
            .filter(Argument::isNotOptional)
            .count();
        if (commandArguments.stream().anyMatch(Argument::isList)) {
            return argumentsLength >= minimumSize;
        }
        int maximumSize = commandArguments.size();
        return argumentsLength >= minimumSize && argumentsLength <= maximumSize;
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

    private C createSubCommandContext(T subCommand, C context) {
        CommandSender<S> sender = context.getSender();
        List<String> contextArguments = context.getArguments();
        int size = contextArguments.size();
        List<String> arguments = contextArguments.subList(1, size);
        Permission contextPermission = context.getPermission();
        Permission permission = subCommand.getPermission(contextPermission);
        return this.contextSupplier.supply(sender, arguments, permission);
    }

    protected T getCommand() {
        return this.command;
    }
}

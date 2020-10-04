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

package net.mcparkour.anfodis.command.registry;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.mcparkour.anfodis.codec.registry.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.PaperMessenger;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.codec.completion.CompletionCodec;
import net.mcparkour.anfodis.command.context.PaperCommandContext;
import net.mcparkour.anfodis.command.context.PaperCommandContextBuilder;
import net.mcparkour.anfodis.command.context.PaperSender;
import net.mcparkour.anfodis.command.context.PaperCompletionContext;
import net.mcparkour.anfodis.command.context.PaperCompletionContextBuilder;
import net.mcparkour.anfodis.command.handler.CommandContextBuilderHandler;
import net.mcparkour.anfodis.command.handler.CommandExecutorHandler;
import net.mcparkour.anfodis.command.handler.CompletionContextBuilderHandler;
import net.mcparkour.anfodis.command.handler.CompletionHandler;
import net.mcparkour.anfodis.command.handler.PaperCommandHandler;
import net.mcparkour.anfodis.command.mapper.PaperCommand;
import net.mcparkour.anfodis.command.mapper.PaperCommandMapper;
import net.mcparkour.anfodis.command.mapper.properties.PaperCommandProperties;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.craftmon.scheduler.PaperAsyncScheduler;
import net.mcparkour.craftmon.scheduler.Scheduler;
import net.mcparkour.intext.message.MessageReceiver;
import net.mcparkour.intext.message.MessageReceiverFactory;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class PaperCommandRegistry
    extends AbstractCompletionRegistry<PaperCommand, PaperCommandContext, PaperCommandContextBuilder, PaperCompletionContext, PaperCompletionContextBuilder, CommandSender, PaperMessenger> {

    private static final PaperCommandMapper COMMAND_MAPPER = new PaperCommandMapper();

    private final CommandMap commandMap;
    private final Scheduler asyncScheduler;
    private final String fallbackCommandPrefix;

    public PaperCommandRegistry(
        final CodecRegistry<InjectionCodec<?>> injectionCodecRegistry,
        final CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry,
        final CodecRegistry<CompletionCodec> completionCodecRegistry,
        final MessageReceiverFactory<CommandSender> messageReceiverFactory,
        final PaperMessenger messenger,
        final Plugin plugin
    ) {
        this(
            injectionCodecRegistry,
            argumentCodecRegistry,
            completionCodecRegistry,
            messageReceiverFactory,
            messenger,
            plugin,
            plugin.getName()
        );
    }

    public PaperCommandRegistry(
        final CodecRegistry<InjectionCodec<?>> injectionCodecRegistry,
        final CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry,
        final CodecRegistry<CompletionCodec> completionCodecRegistry,
        final MessageReceiverFactory<CommandSender> messageReceiverFactory,
        final PaperMessenger messenger,
        final Plugin plugin,
        final String commandPrefix
    ) {
        this(
            injectionCodecRegistry,
            argumentCodecRegistry,
            completionCodecRegistry,
            messageReceiverFactory,
            messenger,
            Permission.of(commandPrefix.toLowerCase()),
            plugin.getServer().getCommandMap(),
            new PaperAsyncScheduler(plugin),
            commandPrefix.toLowerCase()
        );
    }

    public PaperCommandRegistry(
        final CodecRegistry<InjectionCodec<?>> injectionCodecRegistry,
        final CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry,
        final CodecRegistry<CompletionCodec> completionCodecRegistry,
        final MessageReceiverFactory<CommandSender> messageReceiverFactory,
        final PaperMessenger messenger,
        final Permission basePermission,
        final CommandMap commandMap,
        final Scheduler asyncScheduler,
        final String fallbackCommandPrefix
    ) {
        super(
            COMMAND_MAPPER,
            PaperCommandHandler::new,
            CommandExecutorHandler::new,
            PaperCommandContext::new,
            CompletionHandler::new,
            PaperCompletionContext::new,
            injectionCodecRegistry,
            argumentCodecRegistry,
            completionCodecRegistry,
            messageReceiverFactory,
            messenger,
            basePermission
        );
        this.commandMap = commandMap;
        this.asyncScheduler = asyncScheduler;
        this.fallbackCommandPrefix = fallbackCommandPrefix;
    }

    @Override
    public void register(
        final PaperCommand command,
        final CommandContextBuilderHandler<PaperCommandContextBuilder, PaperCommandContext> commandHandler,
        final CompletionContextBuilderHandler<PaperCompletionContextBuilder, PaperCompletionContext> completionHandler
    ) {
        PaperCommandProperties properties = command.getProperties();
        String name = properties.getName();
        String description = properties.getDescription();
        String usage = properties.getDefaultUsage();
        Set<String> aliases = properties.getAliases();
        Permission basePermission = getBasePermission();
        Permission commandPermission = properties.getPermission();
        Permission permission = commandPermission.withFirst(basePermission);
        boolean asynchronous = properties.isAsynchronous();
        register(command, name, description, usage, aliases, permission, asynchronous, commandHandler, completionHandler);
    }

    private void register(
        final PaperCommand command,
        final String name,
        final String description,
        final String usage,
        final Collection<String> aliases,
        final Permission permission,
        final boolean asynchronous,
        final CommandContextBuilderHandler<PaperCommandContextBuilder, PaperCommandContext> commandHandler,
        final CompletionContextBuilderHandler<PaperCompletionContextBuilder, PaperCompletionContext> completionHandler
    ) {
        MessageReceiverFactory<CommandSender> receiverFactory = getMessageReceiverFactory();
        PaperCommandExecutor commandExecutor = (sender, arguments) -> {
            MessageReceiver receiver = receiverFactory.createMessageReceiver(sender);
            PaperSender paperSender = new PaperSender(sender, receiver);
            PaperCommandContextBuilder contextBuilder = new PaperCommandContextBuilder(paperSender, command, arguments, permission, asynchronous);
            if (asynchronous) {
                this.asyncScheduler.run(() -> commandHandler.handle(contextBuilder));
            } else {
                commandHandler.handle(contextBuilder);
            }
        };
        PaperCompletionExecutor completionExecutor = (sender, arguments) -> {
            MessageReceiver receiver = receiverFactory.createMessageReceiver(sender);
            PaperSender paperSender = new PaperSender(sender, receiver);
            PaperCompletionContextBuilder contextBuilder = new PaperCompletionContextBuilder(paperSender, command, arguments, permission, asynchronous);
            return completionHandler.handle(contextBuilder);
        };
        register(name, description, usage, aliases, permission, commandExecutor, completionExecutor);
    }

    public void register(
        final String name,
        final String description,
        final String usage,
        final Collection<String> aliases,
        final PaperCommandExecutor commandExecutor,
        final PaperCompletionExecutor completionExecutor
    ) {
        register(name, description, usage, aliases, Permission.empty(), commandExecutor, completionExecutor);
    }

    public void register(
        final String name,
        final String description,
        final String usage,
        final Collection<String> aliases,
        final Permission permission,
        final PaperCommandExecutor commandExecutor,
        final PaperCompletionExecutor completionExecutor
    ) {
        List<String> aliasesList = List.copyOf(aliases);
        String permissionName = permission.getName();
        Command command = new CommandWrapper(name, description, usage, aliasesList, permissionName, commandExecutor, completionExecutor);
        this.commandMap.register(this.fallbackCommandPrefix, command);
    }
}

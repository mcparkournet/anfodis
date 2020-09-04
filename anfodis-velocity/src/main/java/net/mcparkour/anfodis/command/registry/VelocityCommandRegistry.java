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
import java.util.Set;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.ProxyServer;
import net.mcparkour.anfodis.codec.registry.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.VelocityMessenger;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.codec.completion.CompletionCodec;
import net.mcparkour.anfodis.command.context.VelocityCommandContext;
import net.mcparkour.anfodis.command.context.VelocityCommandSender;
import net.mcparkour.anfodis.command.context.VelocityCompletionContext;
import net.mcparkour.anfodis.command.handler.CommandContextHandler;
import net.mcparkour.anfodis.command.handler.CommandExecutorHandler;
import net.mcparkour.anfodis.command.handler.CompletionContextHandler;
import net.mcparkour.anfodis.command.handler.CompletionHandler;
import net.mcparkour.anfodis.command.handler.VelocityCommandHandler;
import net.mcparkour.anfodis.command.mapper.VelocityCommand;
import net.mcparkour.anfodis.command.mapper.VelocityCommandMapper;
import net.mcparkour.anfodis.command.mapper.properties.VelocityCommandProperties;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.craftmon.scheduler.Scheduler;
import net.mcparkour.craftmon.scheduler.VelocityScheduler;
import net.mcparkour.intext.message.MessageReceiver;
import net.mcparkour.intext.message.MessageReceiverFactory;

public class VelocityCommandRegistry
    extends AbstractCompletionRegistry<VelocityCommand, VelocityCommandContext, VelocityCompletionContext, CommandSource, VelocityMessenger> {

    private static final VelocityCommandMapper COMMAND_MAPPER = new VelocityCommandMapper();

    private final CommandManager commandManager;
    private final Scheduler asyncScheduler;

    public VelocityCommandRegistry(
        final CodecRegistry<InjectionCodec<?>> injectionCodecRegistry,
        final CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry,
        final CodecRegistry<CompletionCodec> completionCodecRegistry,
        final MessageReceiverFactory<CommandSource> messageReceiverFactory,
        final VelocityMessenger messenger,
        final Permission basePermission,
        final ProxyServer server,
        final Object plugin
    ) {
        this(
            injectionCodecRegistry,
            argumentCodecRegistry,
            completionCodecRegistry,
            messageReceiverFactory,
            messenger,
            basePermission,
            server.getCommandManager(),
            new VelocityScheduler(server, plugin)
        );
    }

    public VelocityCommandRegistry(
        final CodecRegistry<InjectionCodec<?>> injectionCodecRegistry,
        final CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry,
        final CodecRegistry<CompletionCodec> completionCodecRegistry,
        final MessageReceiverFactory<CommandSource> messageReceiverFactory,
        final VelocityMessenger messenger,
        final Permission basePermission,
        final CommandManager commandManager,
        final Scheduler asyncScheduler
    ) {
        super(
            COMMAND_MAPPER,
            VelocityCommandHandler::new,
            CommandExecutorHandler::new,
            VelocityCommandContext::new,
            CompletionHandler::new,
            VelocityCompletionContext::new,
            injectionCodecRegistry,
            argumentCodecRegistry,
            completionCodecRegistry,
            messageReceiverFactory,
            messenger,
            basePermission
        );
        this.commandManager = commandManager;
        this.asyncScheduler = asyncScheduler;
    }

    @Override
    public void register(
        final VelocityCommand command,
        final CommandContextHandler<VelocityCommandContext> commandHandler,
        final CompletionContextHandler<VelocityCompletionContext> completionHandler
    ) {
        VelocityCommandProperties properties = command.getProperties();
        Set<String> names = properties.getAllNames();
        Permission basePermission = getBasePermission();
        Permission commandPermission = properties.getPermission();
        Permission permission = commandPermission.withFirst(basePermission);
        boolean asynchronous = properties.isAsynchronous();
        register(names, permission, asynchronous, commandHandler, completionHandler);
    }

    private void register(
        final Collection<String> aliases,
        final Permission permission,
        final boolean asynchronous,
        final CommandContextHandler<VelocityCommandContext> commandHandler,
        final CompletionContextHandler<VelocityCompletionContext> completionHandler
    ) {
        MessageReceiverFactory<CommandSource> messageReceiverFactory = getMessageReceiverFactory();
        VelocityCommandExecutor commandExecutor = (sender, arguments) -> {
            MessageReceiver receiver = messageReceiverFactory.createMessageReceiver(sender);
            VelocityCommandSender velocitySender = new VelocityCommandSender(sender, receiver);
            VelocityCommandContext context = new VelocityCommandContext(velocitySender, arguments, permission, asynchronous);
            commandHandler.handleAsync(context, this.asyncScheduler);
        };
        VelocityCompletionExecutor completionExecutor = (sender, arguments) -> {
            MessageReceiver receiver = messageReceiverFactory.createMessageReceiver(sender);
            VelocityCommandSender velocitySender = new VelocityCommandSender(sender, receiver);
            VelocityCompletionContext context = new VelocityCompletionContext(velocitySender, arguments, permission, asynchronous);
            return completionHandler.handle(context);
        };
        register(aliases, commandExecutor, completionExecutor);
    }

    public void register(
        final Collection<String> aliases,
        final VelocityCommandExecutor commandExecutor,
        final VelocityCompletionExecutor completionExecutor
    ) {
        String[] aliasesArray = aliases.toArray(String[]::new);
        register(aliasesArray, commandExecutor, completionExecutor);
    }

    public void register(
        final String[] aliases,
        final VelocityCommandExecutor commandExecutor,
        final VelocityCompletionExecutor completionExecutor
    ) {
        Command command = new CommandWrapper(commandExecutor, completionExecutor);
        this.commandManager.register(command, aliases);
    }
}

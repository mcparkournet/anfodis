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
import net.mcparkour.anfodis.codec.registry.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.codec.completion.CompletionCodec;
import net.mcparkour.anfodis.command.context.WaterfallCommandContext;
import net.mcparkour.anfodis.command.context.WaterfallCommandSender;
import net.mcparkour.anfodis.command.context.WaterfallCompletionContext;
import net.mcparkour.anfodis.command.handler.CommandContextHandler;
import net.mcparkour.anfodis.command.handler.CommandExecutorHandler;
import net.mcparkour.anfodis.command.handler.CompletionContextHandler;
import net.mcparkour.anfodis.command.handler.CompletionHandler;
import net.mcparkour.anfodis.command.handler.WaterfallCommandHandler;
import net.mcparkour.anfodis.command.mapper.WaterfallCommand;
import net.mcparkour.anfodis.command.mapper.WaterfallCommandMapper;
import net.mcparkour.anfodis.command.mapper.properties.WaterfallCommandProperties;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.intext.message.MessageReceiver;
import net.mcparkour.intext.message.MessageReceiverFactory;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class WaterfallCommandRegistry extends AbstractCompletionRegistry<WaterfallCommand, WaterfallCommandContext, WaterfallCompletionContext, CommandSender> {

    private static final WaterfallCommandMapper COMMAND_MAPPER = new WaterfallCommandMapper();

    private PluginManager pluginManager;
    private Plugin plugin;

    public WaterfallCommandRegistry(final CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, final CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, final CodecRegistry<CompletionCodec> completionCodecRegistry, final MessageReceiverFactory<CommandSender> messageReceiverFactory, final Plugin plugin) {
        this(injectionCodecRegistry, argumentCodecRegistry, completionCodecRegistry, messageReceiverFactory, plugin.getProxy(), plugin);
    }

    public WaterfallCommandRegistry(final CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, final CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, final CodecRegistry<CompletionCodec> completionCodecRegistry, final MessageReceiverFactory<CommandSender> messageReceiverFactory, final ProxyServer server, final Plugin plugin) {
        this(injectionCodecRegistry, argumentCodecRegistry, completionCodecRegistry, messageReceiverFactory, Permission.of(plugin.getDescription().getName().toLowerCase()), server.getPluginManager(), plugin);
    }

    public WaterfallCommandRegistry(final CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, final CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, final CodecRegistry<CompletionCodec> completionCodecRegistry, final MessageReceiverFactory<CommandSender> messageReceiverFactory, final Permission basePermission, final PluginManager pluginManager, final Plugin plugin) {
        super(COMMAND_MAPPER, WaterfallCommandHandler::new, CommandExecutorHandler::new, WaterfallCommandContext::new, CompletionHandler::new, WaterfallCompletionContext::new, injectionCodecRegistry, argumentCodecRegistry, completionCodecRegistry, messageReceiverFactory, basePermission);
        this.pluginManager = pluginManager;
        this.plugin = plugin;
    }

    @Override
    public void register(final WaterfallCommand command, final CommandContextHandler<WaterfallCommandContext> commandHandler, final CompletionContextHandler<WaterfallCompletionContext> completionHandler) {
        WaterfallCommandProperties properties = command.getProperties();
        String name = properties.getName();
        Set<String> aliases = properties.getAliases();
        Permission basePermission = getBasePermission();
        Permission commandPermission = properties.getPermission();
        Permission permission = commandPermission.withFirst(basePermission);
        register(name, aliases, permission, commandHandler, completionHandler);
    }

    private void register(final String name, final Collection<String> aliases, final Permission permission, final CommandContextHandler<WaterfallCommandContext> commandHandler, final CompletionContextHandler<WaterfallCompletionContext> completionHandler) {
        MessageReceiverFactory<CommandSender> messageReceiverFactory = getMessageReceiverFactory();
        WaterfallCommandExecutor commandExecutor = (sender, arguments) -> {
            MessageReceiver receiver = messageReceiverFactory.createMessageReceiver(sender);
            WaterfallCommandSender waterfallSender = new WaterfallCommandSender(sender, receiver);
            WaterfallCommandContext context = new WaterfallCommandContext(waterfallSender, arguments, permission);
            commandHandler.handle(context);
        };
        WaterfallCompletionExecutor completionExecutor = (sender, arguments) -> {
            MessageReceiver receiver = messageReceiverFactory.createMessageReceiver(sender);
            WaterfallCommandSender waterfallSender = new WaterfallCommandSender(sender, receiver);
            WaterfallCompletionContext context = new WaterfallCompletionContext(waterfallSender, arguments, permission);
            return completionHandler.handle(context);
        };
        register(name, aliases, permission, commandExecutor, completionExecutor);
    }

    public void register(final String name, final Collection<String> aliases, final WaterfallCommandExecutor commandExecutor, final WaterfallCompletionExecutor completionExecutor) {
        register(name, aliases, Permission.empty(), commandExecutor, completionExecutor);
    }

    public void register(final String name, final Collection<String> aliases, final Permission permission, final WaterfallCommandExecutor commandExecutor, final WaterfallCompletionExecutor completionExecutor) {
        String permissionName = permission.getName();
        String[] aliasesArray = aliases.toArray(String[]::new);
        CommandWrapper command = new CommandWrapper(name, permissionName, aliasesArray, commandExecutor, completionExecutor);
        this.pluginManager.registerCommand(this.plugin, command);
    }
}

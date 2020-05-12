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

import java.util.List;
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.codec.completion.CompletionCodec;
import net.mcparkour.anfodis.command.context.PaperCommandContext;
import net.mcparkour.anfodis.command.context.PaperCommandSender;
import net.mcparkour.anfodis.command.context.PaperCompletionContext;
import net.mcparkour.anfodis.command.handler.CompletionContextHandler;
import net.mcparkour.anfodis.command.handler.PaperCommandHandler;
import net.mcparkour.anfodis.command.mapper.PaperCommand;
import net.mcparkour.anfodis.command.mapper.PaperCommandMapper;
import net.mcparkour.anfodis.command.mapper.properties.PaperCommandProperties;
import net.mcparkour.anfodis.handler.ContextHandler;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.intext.message.MessageReceiver;
import net.mcparkour.intext.message.MessageReceiverFactory;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

public class PaperCommandRegistry extends AbstractCompletionRegistry<PaperCommand, PaperCommandContext, PaperCompletionContext, CommandSender> {

	private static final PaperCommandMapper COMMAND_MAPPER = new PaperCommandMapper();

	private CommandMap commandMap;

	public PaperCommandRegistry(CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, CodecRegistry<CompletionCodec> completionCodecRegistry, MessageReceiverFactory<CommandSender> messageReceiverFactory, Plugin plugin) {
		this(injectionCodecRegistry, argumentCodecRegistry, completionCodecRegistry, messageReceiverFactory, plugin.getServer(), plugin.getName());
	}

	public PaperCommandRegistry(CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, CodecRegistry<CompletionCodec> completionCodecRegistry, MessageReceiverFactory<CommandSender> messageReceiverFactory, Server server, String pluginName) {
		this(injectionCodecRegistry, argumentCodecRegistry, completionCodecRegistry, messageReceiverFactory, pluginName.toLowerCase(), server.getCommandMap());
	}

	public PaperCommandRegistry(CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, CodecRegistry<CompletionCodec> completionCodecRegistry, MessageReceiverFactory<CommandSender> messageReceiverFactory, String permissionPrefix, CommandMap commandMap) {
		super(COMMAND_MAPPER, PaperCommandHandler::new, injectionCodecRegistry, argumentCodecRegistry, completionCodecRegistry, messageReceiverFactory, permissionPrefix);
		this.commandMap = commandMap;
	}

	@Override
	public void register(PaperCommand command, ContextHandler<PaperCommandContext> handler, CompletionContextHandler<PaperCompletionContext> completionHandler) {
		PaperCommandProperties properties = command.getProperties();
		String name = properties.getName();
		String description = properties.getDescription();
		String usage = properties.getDefaultUsage();
		List<String> aliases = properties.getAliases();
		Permission permission = createPermission(properties);
		register(command, name, description, usage, aliases, permission, handler, completionHandler);
	}

	private void register(PaperCommand command, String name, String description, String usage, List<String> aliases, @Nullable Permission permission, ContextHandler<PaperCommandContext> handler, CompletionContextHandler<PaperCompletionContext> completionHandler) {
		MessageReceiverFactory<CommandSender> receiverFactory = getMessageReceiverFactory();
		PaperCommandExecutor commandExecutor = (sender, arguments) -> {
			MessageReceiver receiver = receiverFactory.createMessageReceiver(sender);
			PaperCommandSender paperSender = new PaperCommandSender(sender, receiver);
			PaperCommandContext context = new PaperCommandContext(paperSender, arguments, permission);
			Object commandInstance = command.createInstance();
			handler.handle(context, commandInstance);
		};
		PaperCompletionExecutor completionExecutor = (sender, arguments) -> {
			MessageReceiver receiver = receiverFactory.createMessageReceiver(sender);
			PaperCommandSender paperSender = new PaperCommandSender(sender, receiver);
			PaperCompletionContext context = new PaperCompletionContext(paperSender, arguments, permission);
			return completionHandler.handle(context);
		};
		register(name, description, usage, aliases, permission, commandExecutor, completionExecutor);
	}

	public void register(String name, String description, String usage, List<String> aliases, PaperCommandExecutor commandExecutor, PaperCompletionExecutor completionExecutor) {
		register(name, description, usage, aliases, null, commandExecutor, completionExecutor);
	}

	public void register(String name, String description, String usage, List<String> aliases, @Nullable Permission permission, PaperCommandExecutor commandExecutor, PaperCompletionExecutor completionExecutor) {
		String permissionPrefix = getPermissionPrefix();
		Command command = new CommandWrapper(name, description, usage, aliases, permission, commandExecutor, completionExecutor);
		this.commandMap.register(permissionPrefix, command);
	}
}

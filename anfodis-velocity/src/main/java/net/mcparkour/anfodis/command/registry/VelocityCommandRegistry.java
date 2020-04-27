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
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.proxy.ProxyServer;
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.codec.completion.CompletionCodec;
import net.mcparkour.anfodis.command.handler.CommandContext;
import net.mcparkour.anfodis.command.handler.CompletionContext;
import net.mcparkour.anfodis.command.handler.CompletionContextHandler;
import net.mcparkour.anfodis.command.handler.VelocityCommandHandler;
import net.mcparkour.anfodis.command.handler.VelocityCommandSender;
import net.mcparkour.anfodis.command.mapper.VelocityCommand;
import net.mcparkour.anfodis.command.mapper.VelocityCommandMapper;
import net.mcparkour.anfodis.command.mapper.properties.VelocityCommandProperties;
import net.mcparkour.anfodis.handler.ContextHandler;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.intext.translation.Translations;
import org.jetbrains.annotations.Nullable;

public class VelocityCommandRegistry extends AbstractCompletionRegistry<VelocityCommand, CommandContext, CompletionContext> {

	private static final VelocityCommandMapper COMMAND_MAPPER = new VelocityCommandMapper();

	private CommandManager commandManager;

	public VelocityCommandRegistry(CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, CodecRegistry<CompletionCodec> completionCodecRegistry, Translations translations, String permissionPrefix, ProxyServer server) {
		this(injectionCodecRegistry, argumentCodecRegistry, completionCodecRegistry, translations, permissionPrefix, server.getCommandManager());
	}

	public VelocityCommandRegistry(CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, CodecRegistry<CompletionCodec> completionCodecRegistry, Translations translations, String permissionPrefix, CommandManager commandManager) {
		super(COMMAND_MAPPER, VelocityCommandHandler::new, injectionCodecRegistry, argumentCodecRegistry, completionCodecRegistry, translations, permissionPrefix);
		this.commandManager = commandManager;
	}

	@Override
	public void register(VelocityCommand command, ContextHandler<CommandContext> handler, CompletionContextHandler<CompletionContext> completionHandler) {
		VelocityCommandProperties properties = command.getProperties();
		List<String> names = properties.getAllNames();
		Permission permission = createPermission(properties);
		register(command, names, permission, handler, completionHandler);
	}

	private void register(VelocityCommand command, List<String> aliases, @Nullable Permission permission, ContextHandler<CommandContext> handler, CompletionContextHandler<CompletionContext> completionHandler) {
		VelocityCommandExecutor commandExecutor = (sender, arguments) -> {
			VelocityCommandSender velocitySender = new VelocityCommandSender(sender);
			CommandContext context = new CommandContext(velocitySender, arguments, permission);
			Object commandInstance = command.createInstance();
			handler.handle(context, commandInstance);
		};
		VelocityCompletionExecutor completionExecutor = (sender, arguments) -> {
			VelocityCommandSender velocitySender = new VelocityCommandSender(sender);
			CompletionContext context = new CompletionContext(velocitySender, arguments, permission);
			return completionHandler.handle(context);
		};
		register(aliases, commandExecutor, completionExecutor);
	}

	public void register(List<String> aliases, VelocityCommandExecutor commandExecutor, VelocityCompletionExecutor completionExecutor) {
		String[] aliasesArray = aliases.toArray(String[]::new);
		register(aliasesArray, commandExecutor, completionExecutor);
	}

	public void register(String[] aliases, VelocityCommandExecutor commandExecutor, VelocityCompletionExecutor completionExecutor) {
		Command command = new CommandWrapper(commandExecutor, completionExecutor);
		this.commandManager.register(command, aliases);
	}
}

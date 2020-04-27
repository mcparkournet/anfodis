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
import net.mcparkour.anfodis.command.handler.CommandContext;
import net.mcparkour.anfodis.command.handler.CompletionContext;
import net.mcparkour.anfodis.command.handler.CompletionContextHandler;
import net.mcparkour.anfodis.command.handler.WaterfallCommandHandler;
import net.mcparkour.anfodis.command.handler.WaterfallCommandSender;
import net.mcparkour.anfodis.command.mapper.WaterfallCommand;
import net.mcparkour.anfodis.command.mapper.WaterfallCommandMapper;
import net.mcparkour.anfodis.command.mapper.properties.WaterfallCommandProperties;
import net.mcparkour.anfodis.handler.ContextHandler;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.intext.translation.Translations;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import org.jetbrains.annotations.Nullable;

public class WaterfallCommandRegistry extends AbstractCompletionRegistry<WaterfallCommand, CommandContext, CompletionContext> {

	private static final WaterfallCommandMapper COMMAND_MAPPER = new WaterfallCommandMapper();

	private PluginManager pluginManager;
	private Plugin plugin;

	public WaterfallCommandRegistry(CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, CodecRegistry<CompletionCodec> completionCodecRegistry, Translations translations, Plugin plugin) {
		this(injectionCodecRegistry, argumentCodecRegistry, completionCodecRegistry, translations, plugin.getProxy(), plugin);
	}

	private WaterfallCommandRegistry(CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, CodecRegistry<CompletionCodec> completionCodecRegistry, Translations translations, ProxyServer server, Plugin plugin) {
		this(injectionCodecRegistry, argumentCodecRegistry, completionCodecRegistry, translations, plugin.getDescription().getName().toLowerCase(), server.getPluginManager(), plugin);
	}

	private WaterfallCommandRegistry(CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, CodecRegistry<CompletionCodec> completionCodecRegistry, Translations translations, String permissionPrefix, PluginManager pluginManager, Plugin plugin) {
		super(COMMAND_MAPPER, WaterfallCommandHandler::new, injectionCodecRegistry, argumentCodecRegistry, completionCodecRegistry, translations, permissionPrefix);
		this.pluginManager = pluginManager;
		this.plugin = plugin;
	}

	@Override
	public void register(WaterfallCommand command, ContextHandler<CommandContext> handler, CompletionContextHandler<CompletionContext> completionHandler) {
		WaterfallCommandProperties properties = command.getProperties();
		String name = properties.getName();
		List<String> aliases = properties.getAliases();
		Permission permission = createPermission(properties);
		register(command, name, aliases, permission, handler, completionHandler);
	}

	private void register(WaterfallCommand command, String name, List<String> aliases, @Nullable Permission permission, ContextHandler<CommandContext> handler, CompletionContextHandler<CompletionContext> completionHandler) {
		WaterfallCommandExecutor commandExecutor = (sender, arguments) -> {
			WaterfallCommandSender waterfallSender = new WaterfallCommandSender(sender);
			CommandContext context = new CommandContext(waterfallSender, arguments, permission);
			Object commandInstance = command.createInstance();
			handler.handle(context, commandInstance);
		};
		WaterfallCompletionExecutor completionExecutor = (sender, arguments) -> {
			WaterfallCommandSender waterfallSender = new WaterfallCommandSender(sender);
			CompletionContext context = new CompletionContext(waterfallSender, arguments, permission);
			return completionHandler.handle(context);
		};
		register(name, aliases, permission, commandExecutor, completionExecutor);
	}

	public void register(String name, List<String> aliases, WaterfallCommandExecutor commandExecutor, WaterfallCompletionExecutor completionExecutor) {
		register(name, aliases, null, commandExecutor, completionExecutor);
	}

	public void register(String name, List<String> aliases, @Nullable Permission permission, WaterfallCommandExecutor commandExecutor, WaterfallCompletionExecutor completionExecutor) {
		String permissionName = permission == null ? null : permission.getName();
		String[] aliasesArray = aliases.toArray(String[]::new);
		CommandWrapper command = new CommandWrapper(name, permissionName, aliasesArray, commandExecutor, completionExecutor);
		this.pluginManager.registerCommand(this.plugin, command);
	}
}

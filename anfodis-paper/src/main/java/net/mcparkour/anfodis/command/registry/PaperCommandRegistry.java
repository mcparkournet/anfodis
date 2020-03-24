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
import net.mcparkour.anfodis.command.handler.PaperCommandHandler;
import net.mcparkour.anfodis.command.mapper.PaperCommand;
import net.mcparkour.anfodis.command.mapper.PaperCommandMapper;
import net.mcparkour.anfodis.command.mapper.properties.PaperCommandProperties;
import net.mcparkour.anfodis.handler.ContextHandler;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.intext.translation.Translations;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

public class PaperCommandRegistry extends AbstractCompletionRegistry<PaperCommand, CommandContext, CompletionContext> {

	private static final PaperCommandMapper COMMAND_MAPPER = new PaperCommandMapper();

	private CommandMap commandMap;

	public PaperCommandRegistry(CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, CodecRegistry<CompletionCodec> completionCodecRegistry, Translations translations, Plugin plugin) {
		this(injectionCodecRegistry, argumentCodecRegistry, completionCodecRegistry, translations, plugin.getServer(), plugin.getName());
	}

	private PaperCommandRegistry(CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, CodecRegistry<CompletionCodec> completionCodecRegistry, Translations translations, Server server, String pluginName) {
		this(injectionCodecRegistry, argumentCodecRegistry, completionCodecRegistry, translations, pluginName.toLowerCase(), server.getCommandMap());
	}

	private PaperCommandRegistry(CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, CodecRegistry<CompletionCodec> completionCodecRegistry, Translations translations, String permissionPrefix, CommandMap commandMap) {
		super(COMMAND_MAPPER, PaperCommandHandler::new, injectionCodecRegistry, argumentCodecRegistry, completionCodecRegistry, translations, permissionPrefix);
		this.commandMap = commandMap;
	}

	@Override
	public void register(PaperCommand command, ContextHandler<CommandContext> handler, CompletionContextHandler<CompletionContext> completionHandler) {
		PaperCommandProperties properties = command.getProperties();
		String name = properties.getName();
		String description = properties.getDescription();
		String usage = properties.getDefaultUsage();
		List<String> aliases = properties.getAliases();
		Permission permission = createPermission(properties);
		register(name, description, usage, aliases, permission, handler, completionHandler);
	}

	public void register(String name, String description, String usage, List<String> aliases, @Nullable Permission permission, ContextHandler<CommandContext> handler, CompletionContextHandler<CompletionContext> completionHandler) {
		CommandWrapper command = new CommandWrapper(name, description, usage, aliases, permission, handler, completionHandler);
		if (permission != null) {
			String permissionName = permission.getName();
			command.setPermission(permissionName);
		}
		String permissionPrefix = getPermissionPrefix();
		this.commandMap.register(permissionPrefix, command);
	}
}

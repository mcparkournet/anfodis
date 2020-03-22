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
import net.mcparkour.anfodis.command.handler.CompletionHandler;
import net.mcparkour.anfodis.command.handler.PaperCommandHandler;
import net.mcparkour.anfodis.command.mapper.PaperCommand;
import net.mcparkour.anfodis.command.mapper.PaperCommandMapper;
import net.mcparkour.anfodis.command.mapper.properties.PaperCommandProperties;
import net.mcparkour.anfodis.handler.Handler;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.craftmon.permission.PermissionBuilder;
import net.mcparkour.intext.translation.Translations;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

public class PaperCommandRegistry extends AbstractCommandRegistry<PaperCommand, CommandContext> {

	private static final PaperCommandMapper COMMAND_MAPPER = new PaperCommandMapper();

	private CommandMap commandMap;
	private String prefix;
	private Translations translations;
	private CodecRegistry<CompletionCodec> completionCodecRegistry;

	public PaperCommandRegistry(CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, Plugin plugin, Translations translations, CodecRegistry<CompletionCodec> completionCodecRegistry) {
		this(injectionCodecRegistry, argumentCodecRegistry, plugin.getServer(), plugin.getName(), translations, completionCodecRegistry);
	}

	private PaperCommandRegistry(CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, Server server, String pluginName, Translations translations, CodecRegistry<CompletionCodec> completionCodecRegistry) {
		this(injectionCodecRegistry, argumentCodecRegistry, server.getCommandMap(), pluginName.toLowerCase(), translations, completionCodecRegistry);
	}

	public PaperCommandRegistry(CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, CommandMap commandMap, String prefix, Translations translations, CodecRegistry<CompletionCodec> completionCodecRegistry) {
		super(COMMAND_MAPPER, injectionCodecRegistry, argumentCodecRegistry);
		this.commandMap = commandMap;
		this.prefix = prefix;
		this.translations = translations;
		this.completionCodecRegistry = completionCodecRegistry;
	}

	@Override
	protected void register(PaperCommand root) {
		CodecRegistry<InjectionCodec<?>> injectionCodecRegistry = getInjectionCodecRegistry();
		CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry = getArgumentCodecRegistry();
		registerDirect(root, context -> {
			Handler handler = new PaperCommandHandler(root, context, this.translations, injectionCodecRegistry, argumentCodecRegistry);
			handler.handle();
		});
	}

	@Override
	public void registerDirect(PaperCommand root, DirectCommandHandler<CommandContext> directHandler) {
		registerDirect(root, directHandler, context -> {
			CompletionHandler handler = new CompletionHandler(root, context, this.completionCodecRegistry);
			return handler.handle();
		});
	}

	public void registerDirect(PaperCommand root, DirectCommandHandler<CommandContext> directHandler, DirectCompletionHandler<CompletionContext> directCompletionHandler) {
		PaperCommandProperties properties = root.getProperties();
		String name = properties.getName();
		String description = properties.getDescription();
		String usage = properties.getDefaultUsage();
		List<String> aliases = properties.getAliases();
		Permission permission = createPermission(properties);
		CommandWrapper command = new CommandWrapper(name, description, usage, aliases, permission, directHandler, directCompletionHandler);
		if (permission != null) {
			String permissionName = permission.getName();
			command.setPermission(permissionName);
		}
		this.commandMap.register(this.prefix, command);
	}

	@Nullable
	private Permission createPermission(PaperCommandProperties properties) {
		String commandPermission = properties.getPermission();
		if (commandPermission == null) {
			return null;
		}
		return new PermissionBuilder()
			.node(this.prefix)
			.node(commandPermission)
			.build();
	}
}

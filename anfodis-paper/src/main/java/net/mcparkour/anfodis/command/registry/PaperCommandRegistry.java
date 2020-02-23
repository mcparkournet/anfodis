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
import net.mcparkour.anfodis.command.mapper.PaperCommand;
import net.mcparkour.anfodis.command.mapper.PaperCommandMapper;
import net.mcparkour.anfodis.command.mapper.properties.PaperCommandProperties;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.craftmon.permission.PermissionBuilder;
import net.mcparkour.intext.translation.Translations;
import org.bukkit.Server;
import org.bukkit.command.CommandMap;
import org.jetbrains.annotations.Nullable;

public class PaperCommandRegistry extends AbstractCommandRegistry<PaperCommand> {

	private static final PaperCommandMapper COMMAND_MAPPER = new PaperCommandMapper();

	private CommandMap commandMap;
	private String prefix;
	private Translations translations;
	private CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry;
	private CodecRegistry<CompletionCodec> completionCodecRegistry;

	public PaperCommandRegistry(CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, Server server, String prefix, Translations translations, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, CodecRegistry<CompletionCodec> completionCodecRegistry) {
		super(COMMAND_MAPPER, injectionCodecRegistry);
		this.commandMap = server.getCommandMap();
		this.prefix = prefix;
		this.translations = translations;
		this.argumentCodecRegistry = argumentCodecRegistry;
		this.completionCodecRegistry = completionCodecRegistry;
	}

	@Override
	protected void register(PaperCommand root) {
		PaperCommandProperties properties = root.getProperties();
		String name = properties.getName();
		String description = properties.getDescription();
		String usage = properties.getDefaultUsage();
		List<String> aliases = properties.getAliases();
		Permission permission = createPermission(properties);
		CodecRegistry<InjectionCodec<?>> injectionCodecRegistry = getInjectionCodecRegistry();
		CommandWrapper command = new CommandWrapper(name, description, usage, aliases, root, permission, this.translations, injectionCodecRegistry, this.argumentCodecRegistry, this.completionCodecRegistry);
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

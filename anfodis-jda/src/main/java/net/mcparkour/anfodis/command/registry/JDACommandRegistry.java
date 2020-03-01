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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.dv8tion.jda.api.JDA;
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.handler.PrivateMessageReceivedListener;
import net.mcparkour.anfodis.command.mapper.JDACommand;
import net.mcparkour.anfodis.command.mapper.properties.JDACommandProperties;
import net.mcparkour.anfodis.mapper.RootMapper;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.intext.translation.Translations;

public class JDACommandRegistry extends AbstractCommandRegistry<JDACommand> {

	private JDA jda;
	private String prefix;
	private Translations translations;
	private Map<Long, List<Permission>> permissions;
	private Map<String, JDACommand> commands = new HashMap<>(16);

	public JDACommandRegistry(RootMapper<JDACommand> mapper, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, JDA jda, String prefix, Translations translations, Map<Long, List<Permission>> permissions) {
		super(mapper, injectionCodecRegistry, argumentCodecRegistry);
		this.jda = jda;
		this.prefix = prefix;
		this.translations = translations;
		this.permissions = permissions;
		registerCommandListener();
	}

	private void registerCommandListener() {
		CodecRegistry<InjectionCodec<?>> injectionCodecRegistry = getInjectionCodecRegistry();
		CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry = getArgumentCodecRegistry();
		PrivateMessageReceivedListener listener = new PrivateMessageReceivedListener(this.prefix, this.translations, this.permissions, this.commands, injectionCodecRegistry, argumentCodecRegistry);
		this.jda.addEventListener(listener);
	}

	@Override
	protected void register(JDACommand root) {
		JDACommandProperties properties = root.getProperties();
		List<String> names = getCommandNames(properties);
		for (String name : names) {
			this.commands.put(name, root);
		}
	}

	private List<String> getCommandNames(JDACommandProperties properties) {
		List<String> names = new ArrayList<>(1);
		String commandName = properties.getName();
		names.add(commandName);
		List<String> aliases = properties.getAliases();
		names.addAll(aliases);
		return names;
	}
}

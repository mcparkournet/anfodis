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

import net.dv8tion.jda.api.JDA;
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.handler.JDACommandContext;
import net.mcparkour.anfodis.command.handler.JDACommandHandler;
import net.mcparkour.anfodis.command.handler.PrivateMessageReceivedListener;
import net.mcparkour.anfodis.command.mapper.JDACommand;
import net.mcparkour.anfodis.handler.Handler;
import net.mcparkour.anfodis.mapper.RootMapper;
import net.mcparkour.intext.translation.Translations;

public class JDACommandRegistry extends AbstractCommandRegistry<JDACommand, JDACommandContext> {

	private JDA jda;
	private String prefix;
	private Translations translations;
	private PermissionMap permissionMap;
	private CommandMap commandMap;

	public JDACommandRegistry(RootMapper<JDACommand> mapper, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, JDA jda, String prefix, Translations translations, PermissionMap permissionMap) {
		super(mapper, injectionCodecRegistry, argumentCodecRegistry);
		this.jda = jda;
		this.prefix = prefix;
		this.translations = translations;
		this.permissionMap = permissionMap;
		this.commandMap = new CommandMap();
		registerCommandListener();
	}

	private void registerCommandListener() {
		PrivateMessageReceivedListener listener = new PrivateMessageReceivedListener(this.prefix, this.permissionMap, this.commandMap);
		this.jda.addEventListener(listener);
	}

	@Override
	protected void register(JDACommand root) {
		CodecRegistry<InjectionCodec<?>> injectionCodecRegistry = getInjectionCodecRegistry();
		CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry = getArgumentCodecRegistry();
		registerDirect(root, context -> {
			Handler handler = new JDACommandHandler(root, context, this.translations, injectionCodecRegistry, argumentCodecRegistry);
			handler.handle();
		});
	}

	@Override
	public void registerDirect(JDACommand root, DirectCommandHandler<JDACommandContext> directHandler) {
		this.commandMap.register(root, directHandler);
	}
}

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

package net.mcparkour.anfodis.channel.registry;

import java.util.List;
import net.mcparkour.anfodis.channel.ChannelMessage;
import net.mcparkour.anfodis.channel.handler.ChannelListenerContext;
import net.mcparkour.anfodis.channel.handler.PaperChannelListenerHandler;
import net.mcparkour.anfodis.channel.mapper.PaperChannelListener;
import net.mcparkour.anfodis.channel.mapper.PaperChannelListenerMapper;
import net.mcparkour.anfodis.channel.mapper.properties.PaperChannelListenerProperties;
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.handler.ContextHandler;
import net.mcparkour.anfodis.registry.AbstractRegistry;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class PaperChannelListenerRegistry extends AbstractRegistry<PaperChannelListener, ChannelListenerContext> {

	private static final PaperChannelListenerMapper CHANNEL_LISTENER_MAPPER = new PaperChannelListenerMapper();

	private Plugin plugin;
	private Messenger messenger;

	public PaperChannelListenerRegistry(CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, Plugin plugin) {
		super(net.mcparkour.anfodis.channel.annotation.properties.ChannelListener.class, CHANNEL_LISTENER_MAPPER, injectionCodecRegistry);
		this.plugin = plugin;
		Server server = plugin.getServer();
		this.messenger = server.getMessenger();
	}

	@Override
	public void register(PaperChannelListener root) {
		CodecRegistry<InjectionCodec<?>> injectionCodecRegistry = getInjectionCodecRegistry();
		PaperChannelListenerHandler handler = new PaperChannelListenerHandler(root, injectionCodecRegistry);
		register(root, handler);
	}

	@Override
	public void register(PaperChannelListener root, ContextHandler<ChannelListenerContext> handler) {
		PaperChannelListenerProperties properties = root.getProperties();
		List<String> channels = properties.getChannels();
		for (String channel : channels) {
			register(root, channel, handler);
		}
	}

	private void register(PaperChannelListener channelListener, String channel, ContextHandler<ChannelListenerContext> handler) {
		ChannelListener listener = (source, message) -> {
			ChannelListenerContext context = new ChannelListenerContext(source, message);
			Object channelListenerInstance = channelListener.createInstance();
			handler.handle(context, channelListenerInstance);
		};
		register(channel, listener);
	}

	public void register(String channel, ChannelListener channelListener) {
		PluginMessageListener messageListener = createPluginMessageListener(channel, channelListener);
		this.messenger.registerIncomingPluginChannel(this.plugin, channel, messageListener);
	}

	private PluginMessageListener createPluginMessageListener(String channel, ChannelListener channelListener) {
		return (incomingChannel, player, message) -> {
			if (channel.equals(incomingChannel)) {
				ChannelMessage channelMessage = new ChannelMessage(message);
				channelListener.listen(player, channelMessage);
			}
		};
	}
}

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

import net.mcparkour.anfodis.channel.ChannelMessage;
import net.mcparkour.anfodis.channel.handler.ChannelListenerContext;
import net.mcparkour.anfodis.channel.handler.PaperChannelListenerHandler;
import net.mcparkour.anfodis.channel.mapper.PaperChannelListener;
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.handler.Handler;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public class PluginMessageListenerWrapper implements PluginMessageListener {

	private PaperChannelListener channelListener;
	private String channel;
	private CodecRegistry<InjectionCodec<?>> injectionCodecRegistry;

	public PluginMessageListenerWrapper(PaperChannelListener channelListener, String channel, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry) {
		this.channelListener = channelListener;
		this.channel = channel;
		this.injectionCodecRegistry = injectionCodecRegistry;
	}

	@Override
	public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] message) {
		if (this.channel.equals(channel)) {
			ChannelMessage channelMessage = new ChannelMessage(message);
			ChannelListenerContext context = new ChannelListenerContext(player, channelMessage);
			Handler handler = new PaperChannelListenerHandler(this.channelListener, context, this.injectionCodecRegistry);
			handler.handle();
		}
	}
}
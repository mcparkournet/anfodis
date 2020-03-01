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

package net.mcparkour.anfodis.channel.handler;

import java.util.List;
import net.mcparkour.anfodis.channel.ChannelMessage;
import net.mcparkour.anfodis.channel.mapper.PaperChannelListener;
import net.mcparkour.anfodis.channel.mapper.context.PaperChannelListenerContext;
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.handler.Handler;
import net.mcparkour.anfodis.mapper.executor.Executor;
import net.mcparkour.anfodis.mapper.injection.Injection;
import net.mcparkour.anfodis.result.Result;
import org.bukkit.entity.Player;

public class PaperChannelListenerHandler implements Handler {

	private PaperChannelListener channelListener;
	private ChannelListenerContext context;
	private CodecRegistry<InjectionCodec<?>> injectionCodecRegistry;
	private Object channelListenerInstance;

	public PaperChannelListenerHandler(PaperChannelListener channelListener, ChannelListenerContext context, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry) {
		this.channelListener = channelListener;
		this.context = context;
		this.injectionCodecRegistry = injectionCodecRegistry;
		this.channelListenerInstance = channelListener.createInstance();
	}

	@Override
	public void handle() {
		setInjections();
		setContext();
		execute();
	}

	private void setInjections() {
		List<Injection> injections = this.channelListener.getInjections();
		for (Injection injection : injections) {
			InjectionCodec<?> codec = injection.getCodec(this.injectionCodecRegistry);
			Object codecInjection = codec.getInjection();
			injection.setInjectionField(this.channelListenerInstance, codecInjection);
		}
	}

	private void setContext() {
		PaperChannelListenerContext channelListenerContext = this.channelListener.getContext();
		ChannelMessage message = this.context.getMessage();
		channelListenerContext.setMessageField(this.channelListenerInstance, message);
		Player source = this.context.getSource();
		channelListenerContext.setSourceField(this.channelListenerInstance, source);
	}

	private void execute() {
		Executor executor = this.channelListener.getExecutor();
		executor.invokeBefore(this.channelListenerInstance);
		Object invokeResult = executor.invokeExecutor(this.channelListenerInstance);
		if (invokeResult instanceof Result) {
			Result result = (Result) invokeResult;
			result.onResult();
		}
		executor.invokeAfter(this.channelListenerInstance);
	}
}

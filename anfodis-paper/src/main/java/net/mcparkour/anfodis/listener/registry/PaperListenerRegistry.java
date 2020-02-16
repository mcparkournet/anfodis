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

package net.mcparkour.anfodis.listener.registry;

import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.handler.Handler;
import net.mcparkour.anfodis.listener.annotation.properties.Listener;
import net.mcparkour.anfodis.listener.handler.ListenerHandler;
import net.mcparkour.anfodis.listener.mapper.PaperListener;
import net.mcparkour.anfodis.listener.mapper.PaperListenerMapper;
import net.mcparkour.anfodis.listener.mapper.properties.PaperListenerProperties;
import net.mcparkour.anfodis.registry.AbstractRegistry;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

public class PaperListenerRegistry extends AbstractRegistry<PaperListener> {

	private static final PaperListenerMapper LISTENER_MAPPER = new PaperListenerMapper();
	private static final org.bukkit.event.Listener EMPTY_LISTENER = new org.bukkit.event.Listener() {};

	private Plugin plugin;

	public PaperListenerRegistry(CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, Plugin plugin) {
		super(Listener.class, LISTENER_MAPPER, injectionCodecRegistry);
		this.plugin = plugin;
	}

	@Override
	protected void register(PaperListener mapped) {
		CodecRegistry<InjectionCodec<?>> injectionCodecRegistry = getInjectionCodecRegistry();
		Server server = this.plugin.getServer();
		PluginManager pluginManager = server.getPluginManager();
		PaperListenerProperties properties = mapped.getListenerProperties();
		EventPriority priority = properties.getPriority();
		boolean ignoreCancelled = properties.isIgnoreCancelled();
		Iterable<Class<? extends Event>> eventTypes = properties.getListenedEvents();
		for (Class<? extends Event> eventType : eventTypes) {
			EventExecutor executor = (listener, event) -> {
				Handler handler = new ListenerHandler(eventType, event, mapped, injectionCodecRegistry);
				handler.handle();
			};
			pluginManager.registerEvent(eventType, EMPTY_LISTENER, priority, executor, this.plugin, ignoreCancelled);
		}
	}
}

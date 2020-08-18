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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import com.google.common.collect.Multimap;
import net.mcparkour.common.reflection.Reflections;
import net.md_5.bungee.api.plugin.Event;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.event.EventBus;

public class ReflectedPluginManager {

    private ReflectedEventBus eventBus;
    private Multimap<Plugin, Listener> listenersByPlugin;

    public ReflectedPluginManager(final PluginManager pluginManager) {
        EventBus eventBus = getFieldValue("eventBus", pluginManager);
        this.eventBus = new ReflectedEventBus(eventBus);
        this.listenersByPlugin = getFieldValue("listenersByPlugin", pluginManager);
    }

    @SuppressWarnings("unchecked")
    private static <T> T getFieldValue(final String fieldName, final PluginManager pluginManager) {
        Field field = Reflections.getField(PluginManager.class, fieldName);
        Object fieldValue = Reflections.getFieldValue(field, pluginManager);
        Objects.requireNonNull(fieldValue, "Value of field " + fieldName + " is null");
        return (T) fieldValue;
    }

    public void registerListener(final Plugin plugin, final Listener listener, final Method listenMethod, final Class<? extends Event> listenedEventType, final byte priority) {
        this.eventBus.register(listener, listenMethod, listenedEventType, priority);
        this.listenersByPlugin.put(plugin, listener);
    }
}

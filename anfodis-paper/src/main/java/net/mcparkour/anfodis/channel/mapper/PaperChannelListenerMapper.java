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

package net.mcparkour.anfodis.channel.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import net.mcparkour.anfodis.channel.mapper.context.PaperChannelListenerContext;
import net.mcparkour.anfodis.channel.mapper.context.PaperChannelListenerContextMapper;
import net.mcparkour.anfodis.channel.mapper.properties.PaperChannelListenerProperties;
import net.mcparkour.anfodis.channel.mapper.properties.PaperChannelListenerPropertiesMapper;
import net.mcparkour.anfodis.mapper.RootMapper;
import net.mcparkour.anfodis.mapper.executor.Executor;
import net.mcparkour.anfodis.mapper.injection.Injection;

public class PaperChannelListenerMapper implements RootMapper<PaperChannelListener> {

    private static final PaperChannelListenerContextMapper CONTEXT_MAPPER = new PaperChannelListenerContextMapper();
    private static final PaperChannelListenerPropertiesMapper PROPERTIES_MAPPER = new PaperChannelListenerPropertiesMapper();

    @Override
    public PaperChannelListener map(final Class<?> annotatedClass) {
        Field[] fields = annotatedClass.getDeclaredFields();
        Method[] methods = annotatedClass.getDeclaredMethods();
        Constructor<?> constructor = getConstructor(annotatedClass);
        List<Injection> injections = getInjections(fields);
        Executor executor = getExecutor(methods);
        PaperChannelListenerContext context = CONTEXT_MAPPER.map(fields);
        PaperChannelListenerProperties properties = PROPERTIES_MAPPER.map(annotatedClass);
        return new PaperChannelListener(constructor, injections, executor, context, properties);
    }
}

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

package net.mcparkour.anfodis.listener.mapper;

import net.mcparkour.anfodis.listener.mapper.context.VelocityContext;
import net.mcparkour.anfodis.listener.mapper.context.VelocityContextMapper;
import net.mcparkour.anfodis.listener.mapper.properties.VelocityListenerProperties;
import net.mcparkour.anfodis.listener.mapper.properties.VelocityListenerPropertiesMapper;

public class VelocityListenerMapper extends ListenerMapper<VelocityListener, VelocityContext, VelocityListenerProperties> {

	private static final VelocityContextMapper CONTEXT_MAPPER = new VelocityContextMapper();
	private static final VelocityListenerPropertiesMapper PROPERTIES_MAPPER = new VelocityListenerPropertiesMapper();

	public VelocityListenerMapper() {
		super(CONTEXT_MAPPER, PROPERTIES_MAPPER, VelocityListener::new);
	}
}

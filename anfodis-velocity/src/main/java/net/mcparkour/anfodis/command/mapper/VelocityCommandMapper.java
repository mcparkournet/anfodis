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

package net.mcparkour.anfodis.command.mapper;

import net.mcparkour.anfodis.command.mapper.argument.VelocityArgument;
import net.mcparkour.anfodis.command.mapper.argument.VelocityArgumentMapper;
import net.mcparkour.anfodis.command.mapper.context.VelocityContext;
import net.mcparkour.anfodis.command.mapper.context.VelocityContextMapper;
import net.mcparkour.anfodis.command.mapper.properties.VelocityCommandProperties;
import net.mcparkour.anfodis.command.mapper.properties.VelocityCommandPropertiesMapper;

public class VelocityCommandMapper extends CommandMapper<VelocityCommand, VelocityArgument, VelocityContext, VelocityCommandProperties> {

	private static final VelocityArgumentMapper ARGUMENT_MAPPER = new VelocityArgumentMapper();
	private static final VelocityContextMapper CONTEXT_MAPPER = new VelocityContextMapper();
	private static final VelocityCommandPropertiesMapper PROPERTIES_MAPPER = new VelocityCommandPropertiesMapper();

	public VelocityCommandMapper() {
		super(ARGUMENT_MAPPER, CONTEXT_MAPPER, PROPERTIES_MAPPER, VelocityCommand::new);
	}
}

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

import net.mcparkour.anfodis.command.mapper.argument.WaterfallArgument;
import net.mcparkour.anfodis.command.mapper.argument.WaterfallArgumentMapper;
import net.mcparkour.anfodis.command.mapper.context.WaterfallContext;
import net.mcparkour.anfodis.command.mapper.context.WaterfallContextMapper;
import net.mcparkour.anfodis.command.mapper.properties.WaterfallCommandProperties;
import net.mcparkour.anfodis.command.mapper.properties.WaterfallCommandPropertiesMapper;

public class WaterfallCommandMapper extends CommandMapper<WaterfallCommand, WaterfallArgument, WaterfallContext, WaterfallCommandProperties> {

	private static final WaterfallArgumentMapper ARGUMENT_MAPPER = new WaterfallArgumentMapper();
	private static final WaterfallContextMapper CONTEXT_MAPPER = new WaterfallContextMapper();
	private static final WaterfallCommandPropertiesMapper PROPERTIES_MAPPER = new WaterfallCommandPropertiesMapper();

	public WaterfallCommandMapper() {
		super(ARGUMENT_MAPPER, CONTEXT_MAPPER, PROPERTIES_MAPPER, WaterfallCommand::new);
	}
}

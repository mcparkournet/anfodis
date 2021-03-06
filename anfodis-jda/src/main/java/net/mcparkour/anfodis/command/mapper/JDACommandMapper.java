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

import net.mcparkour.anfodis.command.ChannelSender;
import net.mcparkour.anfodis.command.context.JDACommandContext;
import net.mcparkour.anfodis.command.mapper.argument.JDAArgument;
import net.mcparkour.anfodis.command.mapper.argument.JDAArgumentMapper;
import net.mcparkour.anfodis.command.mapper.properties.JDACommandProperties;
import net.mcparkour.anfodis.command.mapper.properties.JDACommandPropertiesMapper;

public class JDACommandMapper
    extends CommandMapper<JDACommand, JDAArgument, JDACommandProperties, JDACommandContext, ChannelSender> {

    private static final JDAArgumentMapper ARGUMENT_MAPPER = new JDAArgumentMapper();
    private static final JDACommandPropertiesMapper PROPERTIES_MAPPER = new JDACommandPropertiesMapper();

    public JDACommandMapper() {
        super(ARGUMENT_MAPPER, PROPERTIES_MAPPER, JDACommand::new);
    }
}

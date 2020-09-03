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

package net.mcparkour.anfodis.channel.mapper.context;

import java.lang.reflect.Field;
import java.util.Collection;
import net.mcparkour.anfodis.channel.annotation.context.Message;
import net.mcparkour.anfodis.channel.annotation.context.Source;
import net.mcparkour.anfodis.mapper.ElementsMapperBuilder;
import net.mcparkour.anfodis.mapper.Mapper;

public class PaperChannelListenerContextMapper implements Mapper<Field, PaperChannelListenerContext> {

    @Override
    public PaperChannelListenerContext map(final Collection<Field> elements) {
        var mapper = new ElementsMapperBuilder<Field, PaperChannelListenerContextData>()
            .data(PaperChannelListenerContextData::new)
            .element((builder, data) -> builder
                .required(Message.class)
                .elementConsumer(data::setMessageField))
            .element((builder, data) -> builder
                .required(Source.class)
                .elementConsumer(data::setSourceField))
            .build();
        PaperChannelListenerContextData contextData = mapper.mapToSingle(elements);
        return new PaperChannelListenerContext(contextData);
    }
}

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

package net.mcparkour.anfodis.channel.mapper.properties;

import net.mcparkour.anfodis.channel.annotation.properties.ChannelListener;
import net.mcparkour.anfodis.mapper.ElementsMapperBuilder;
import net.mcparkour.anfodis.mapper.Mapper;
import net.mcparkour.anfodis.mapper.SingleElementMapperBuilder;

public class PaperChannelListenerPropertiesMapper implements Mapper<Class<?>, PaperChannelListenerProperties> {

	@Override
	public PaperChannelListenerProperties map(Iterable<Class<?>> elements) {
		return new ElementsMapperBuilder<Class<?>, PaperChannelListenerPropertiesData>()
			.data(PaperChannelListenerPropertiesData::new)
			.singleElement(data -> new SingleElementMapperBuilder<Class<?>>()
				.annotation(ChannelListener.class, channelListener -> data.setChannels(channelListener.value()))
				.build())
			.build()
			.mapFirstOptional(elements)
			.map(PaperChannelListenerProperties::new)
			.get();
	}
}

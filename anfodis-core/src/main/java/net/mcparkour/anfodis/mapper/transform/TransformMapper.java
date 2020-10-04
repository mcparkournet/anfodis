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

package net.mcparkour.anfodis.mapper.transform;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.mcparkour.anfodis.annotation.transform.TransformCodec;
import net.mcparkour.anfodis.handler.RootContext;
import net.mcparkour.anfodis.mapper.ElementsMapper;
import net.mcparkour.anfodis.mapper.ElementsMapperBuilder;
import net.mcparkour.anfodis.mapper.Mapper;

public class TransformMapper<C extends RootContext> implements Mapper<Field, List<Transform<C>>> {

    private static final ElementsMapper<Field, TransformData> MAPPER = new ElementsMapperBuilder<Field, TransformData>()
        .data(TransformData::new)
        .element((builder, data) -> builder
            .required(net.mcparkour.anfodis.annotation.transform.Transform.class)
            .additional(TransformCodec.class, injectionCodec -> data.setCodecType(injectionCodec.value()))
            .elementConsumer(data::setTransformField))
        .build();

    @Override
    public List<Transform<C>> map(final Collection<Field> elements) {
        Stream<Transform<C>> transformStream = MAPPER.mapToMultiple(elements).map(Transform::new);
        return transformStream.collect(Collectors.toUnmodifiableList());
    }
}

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

package net.mcparkour.anfodis.mapper.injection;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.mcparkour.anfodis.annotation.injection.Inject;
import net.mcparkour.anfodis.annotation.injection.InjectionCodec;
import net.mcparkour.anfodis.mapper.ElementsMapper;
import net.mcparkour.anfodis.mapper.Mapper;
import net.mcparkour.anfodis.mapper.ElementsMapperBuilder;

public class InjectionMapper implements Mapper<Field, List<Injection>> {

    private static final ElementsMapper<Field, InjectionData> MAPPER = new ElementsMapperBuilder<Field, InjectionData>()
        .data(InjectionData::new)
        .element((builder, data) -> builder
            .required(Inject.class)
            .additional(InjectionCodec.class, injectionCodec -> data.setCodecType(injectionCodec.value()))
            .elementConsumer(data::setInjectionField))
        .build();

    @Override
    public List<Injection> map(final Collection<Field> elements) {
        return MAPPER.mapToMultiple(elements)
            .map(Injection::new)
            .collect(Collectors.toUnmodifiableList());
    }
}

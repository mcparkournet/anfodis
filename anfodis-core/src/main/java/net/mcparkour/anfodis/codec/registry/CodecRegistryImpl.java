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

package net.mcparkour.anfodis.codec.registry;

import java.util.Map;
import java.util.Optional;
import net.mcparkour.anfodis.codec.Codec;

class CodecRegistryImpl<T extends Codec> implements CodecRegistry<T> {

    private final Map<Class<?>, ? extends T> typedCodecs;
    private final Map<Class<? extends T>, ? extends T> selfCodecs;

    CodecRegistryImpl(final Map<Class<?>, ? extends T> typedCodecs, final Map<Class<? extends T>, ? extends T> selfCodecs) {
        this.typedCodecs = typedCodecs;
        this.selfCodecs = selfCodecs;
    }

    @Override
    public Optional<T> getTypedCodec(final Class<?> type) {
        T found = this.typedCodecs.get(type);
        return Optional.ofNullable(found);
    }

    @Override
    public Optional<T> getSelfCodec(final Class<? extends T> type) {
        T found = this.selfCodecs.get(type);
        return Optional.ofNullable(found);
    }
}

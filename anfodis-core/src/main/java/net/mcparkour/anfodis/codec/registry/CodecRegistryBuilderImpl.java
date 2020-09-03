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

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.mcparkour.anfodis.codec.Codec;
import net.mcparkour.anfodis.codec.CodecProvider;

class CodecRegistryBuilderImpl<T extends Codec> implements CodecRegistryBuilder<T> {

    private Map<Class<?>, T> typedCodecs = new HashMap<>(0);
    private Map<Class<? extends T>, T> selfCodecs = new HashMap<>(0);

    @Override
    public CodecRegistryBuilder<T> typed(final Class<?> type, final T codec) {
        this.typedCodecs.put(type, codec);
        return this;
    }

    @Override
    public <S extends T> CodecRegistryBuilder<T> self(final Class<? extends S> type, final S codec) {
        this.selfCodecs.put(type, codec);
        return this;
    }

    @Override
    public CodecRegistryBuilder<T> with(final CodecProvider<T> provider) {
        putTypedCodecs(provider);
        putSelfCodecs(provider);
        return this;
    }

    private void putTypedCodecs(final CodecProvider<T> provider) {
        Set<Entry<Class<?>, T>> codecs = provider.getTypedCodecs();
        for (final Entry<Class<?>, T> entry : codecs) {
            Class<?> codedType = entry.getKey();
            T codec = entry.getValue();
            this.typedCodecs.put(codedType, codec);
        }
    }

    private void putSelfCodecs(final CodecProvider<T> provider) {
        Set<Entry<Class<? extends T>, T>> codecs = provider.getSelfCodecs();
        for (final Entry<Class<? extends T>, T> entry : codecs) {
            Class<? extends T> codecType = entry.getKey();
            T codec = entry.getValue();
            this.selfCodecs.put(codecType, codec);
        }
    }

    @Override
    public CodecRegistry<T> build() {
        return new CodecRegistryImpl<>(this.typedCodecs, this.selfCodecs);
    }
}

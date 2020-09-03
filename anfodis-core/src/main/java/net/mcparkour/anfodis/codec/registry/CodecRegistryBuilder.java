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

import net.mcparkour.anfodis.codec.Codec;
import net.mcparkour.anfodis.codec.CodecProvider;

public interface CodecRegistryBuilder<T extends Codec> {

    /**
     * Typed codecs are selected by type of the member they coded. For example
     * if coded field's type is {@link String} and the following codecs are registered
     * {@code typed(Integer.class, new FooCodec()).typed(String.class, new BarCodec())}
     * then the {@code BarCodec} will be used.
     *
     * @param type type of the member to code
     * @param codec instance of the codec
     */
    CodecRegistryBuilder<T> typed(Class<?> type, T codec);

    /**
     * Self codecs are selected by the type of registered codec. Fox example
     * if coded field has annotation with {@code BarCodec.class} and the following
     * codecs are registered
     * {@code self(FooCodec.class, new FooCodec()).self(BarCodec.class, new BarCodec())}
     * then the {@code BarCodec} will be used.
     *
     * @param type type of the codec
     * @param codec instance of the codec
     */
    <S extends T> CodecRegistryBuilder<T> self(Class<? extends S> type, S codec);

    CodecRegistryBuilder<T> with(CodecProvider<T> provider);

    CodecRegistry<T> build();
}

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

package net.mcparkour.anfodis.command.codec.argument;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import net.mcparkour.anfodis.codec.CodecProvider;
import net.mcparkour.anfodis.command.codec.argument.basic.BooleanArgumentCodec;
import net.mcparkour.anfodis.command.codec.argument.basic.ByteArgumentCodec;
import net.mcparkour.anfodis.command.codec.argument.basic.DoubleArgumentCodec;
import net.mcparkour.anfodis.command.codec.argument.basic.FloatArgumentCodec;
import net.mcparkour.anfodis.command.codec.argument.basic.IntegerArgumentCodec;
import net.mcparkour.anfodis.command.codec.argument.basic.LongArgumentCodec;
import net.mcparkour.anfodis.command.codec.argument.basic.ShortArgumentCodec;
import net.mcparkour.anfodis.command.codec.argument.basic.UUIDArgumentCodec;

class BasicArgumentCodecsProvider implements CodecProvider<ArgumentCodec<?>> {

    private static final Set<Entry<Class<?>, ArgumentCodec<?>>> TYPED_CODECS = Set.of(
        Map.entry(String.class, ArgumentCodec.identity()),
        Map.entry(boolean.class, new BooleanArgumentCodec()),
        Map.entry(Boolean.class, new BooleanArgumentCodec()),
        Map.entry(byte.class, new ByteArgumentCodec()),
        Map.entry(Byte.class, new ByteArgumentCodec()),
        Map.entry(short.class, new ShortArgumentCodec()),
        Map.entry(Short.class, new ShortArgumentCodec()),
        Map.entry(int.class, new IntegerArgumentCodec()),
        Map.entry(Integer.class, new IntegerArgumentCodec()),
        Map.entry(long.class, new LongArgumentCodec()),
        Map.entry(Long.class, new LongArgumentCodec()),
        Map.entry(float.class, new FloatArgumentCodec()),
        Map.entry(Float.class, new FloatArgumentCodec()),
        Map.entry(double.class, new DoubleArgumentCodec()),
        Map.entry(Double.class, new DoubleArgumentCodec()),
        Map.entry(UUID.class, new UUIDArgumentCodec())
    );

    @Override
    public Set<Entry<Class<?>, ArgumentCodec<?>>> getTypedCodecs() {
        return TYPED_CODECS;
    }

    @Override
    public Set<Entry<Class<? extends ArgumentCodec<?>>, ArgumentCodec<?>>> getSelfCodecs() {
        return Set.of();
    }
}

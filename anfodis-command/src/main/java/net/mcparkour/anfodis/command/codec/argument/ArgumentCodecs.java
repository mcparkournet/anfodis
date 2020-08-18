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

import java.util.UUID;
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.CodecRegistryBuilder;
import net.mcparkour.anfodis.command.codec.argument.basic.BooleanArgumentCodec;
import net.mcparkour.anfodis.command.codec.argument.basic.ByteArgumentCodec;
import net.mcparkour.anfodis.command.codec.argument.basic.DoubleArgumentCodec;
import net.mcparkour.anfodis.command.codec.argument.basic.FloatArgumentCodec;
import net.mcparkour.anfodis.command.codec.argument.basic.IntegerArgumentCodec;
import net.mcparkour.anfodis.command.codec.argument.basic.LongArgumentCodec;
import net.mcparkour.anfodis.command.codec.argument.basic.ShortArgumentCodec;
import net.mcparkour.anfodis.command.codec.argument.basic.UUIDArgumentCodec;

public final class ArgumentCodecs {

    public static final CodecRegistry<ArgumentCodec<?>> BASIC_ARGUMENT_CODEC_REGISTRY = new CodecRegistryBuilder<ArgumentCodec<?>>()
        .typed(String.class, ArgumentCodec.identity())
        .typed(boolean.class, new BooleanArgumentCodec())
        .typed(Boolean.class, new BooleanArgumentCodec())
        .typed(byte.class, new ByteArgumentCodec())
        .typed(Byte.class, new ByteArgumentCodec())
        .typed(short.class, new ShortArgumentCodec())
        .typed(Short.class, new ShortArgumentCodec())
        .typed(int.class, new IntegerArgumentCodec())
        .typed(Integer.class, new IntegerArgumentCodec())
        .typed(long.class, new LongArgumentCodec())
        .typed(Long.class, new LongArgumentCodec())
        .typed(float.class, new FloatArgumentCodec())
        .typed(Float.class, new FloatArgumentCodec())
        .typed(double.class, new DoubleArgumentCodec())
        .typed(Double.class, new DoubleArgumentCodec())
        .typed(UUID.class, new UUIDArgumentCodec())
        .build();

    private ArgumentCodecs() {
        throw new UnsupportedOperationException("Cannot create an instance of this class");
    }
}

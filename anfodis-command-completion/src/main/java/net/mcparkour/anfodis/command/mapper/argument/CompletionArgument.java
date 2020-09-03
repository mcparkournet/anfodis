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

package net.mcparkour.anfodis.command.mapper.argument;

import java.util.Optional;
import net.mcparkour.anfodis.codec.registry.CodecRegistry;
import net.mcparkour.anfodis.codec.UnknownCodecException;
import net.mcparkour.anfodis.command.codec.completion.CompletionCodec;
import org.jetbrains.annotations.Nullable;

public class CompletionArgument extends Argument {

    private final boolean hasCompletion;
    private final @Nullable Class<? extends CompletionCodec> codecType;

    public CompletionArgument(final CompletionArgumentData argumentData) {
        super(argumentData);
        this.hasCompletion = argumentData.hasCompletion();
        this.codecType = argumentData.getCompletionCodecType();
    }

    public Optional<CompletionCodec> getCompletionCodec(final CodecRegistry<CompletionCodec> registry) {
        if (!this.hasCompletion) {
            return Optional.empty();
        }
        Class<?> argumentClass = getArgumentClass();
        CompletionCodec codec = this.codecType == null ?
            getTypedCodec(registry, argumentClass) :
            getSelfCodec(registry, this.codecType);
        return Optional.of(codec);
    }

    private CompletionCodec getTypedCodec(final CodecRegistry<CompletionCodec> registry, final Class<?> type) {
        Optional<CompletionCodec> optionalCodec = registry.getTypedCodec(type);
        if (optionalCodec.isEmpty()) {
            throw new UnknownCodecException("Cannot find completion codec for type " + type);
        }
        return optionalCodec.get();
    }

    private CompletionCodec getSelfCodec(final CodecRegistry<CompletionCodec> registry, final Class<? extends CompletionCodec> type) {
        Optional<CompletionCodec> optionalCodec = registry.getSelfCodec(type);
        if (optionalCodec.isEmpty()) {
            throw new UnknownCodecException("Cannot find completion codec for key '" + type + "'");
        }
        return optionalCodec.get();
    }
}

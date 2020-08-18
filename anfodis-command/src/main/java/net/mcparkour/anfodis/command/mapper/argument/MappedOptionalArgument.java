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

import java.util.NoSuchElementException;
import net.mcparkour.anfodis.command.OptionalArgument;
import org.jetbrains.annotations.Nullable;

final class MappedOptionalArgument<T> implements OptionalArgument<T> {

    static final OptionalArgument<?> EMPTY_OPTIONAL_ARGUMENT = new MappedOptionalArgument<>(null, false);

    @Nullable
    private final T value;
    private final boolean present;

    static <T> OptionalArgument<T> of(@Nullable final T value) {
        return new MappedOptionalArgument<>(value, true);
    }

    private MappedOptionalArgument(@Nullable final T value, final boolean present) {
        this.value = value;
        this.present = present;
    }

    @Override
    public boolean isPresent() {
        return this.present;
    }

    @Override
    @Nullable
    public T orElse(@Nullable final T other) {
        return this.present ? this.value : other;
    }

    @Override
    @Nullable
    public T get() {
        if (!this.present) {
            throw new NoSuchElementException("Argument value is not present");
        }
        return this.value;
    }
}

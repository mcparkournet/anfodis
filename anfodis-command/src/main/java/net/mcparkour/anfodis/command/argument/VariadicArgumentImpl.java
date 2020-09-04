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

package net.mcparkour.anfodis.command.argument;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import net.mcparkour.anfodis.command.argument.VariadicArgument;

class VariadicArgumentImpl<T> implements VariadicArgument<T> {

    private final List<T> arguments;

    VariadicArgumentImpl(final List<T> arguments) {
        this.arguments = arguments;
    }

    @Override
    public boolean isEmpty() {
        return this.arguments.isEmpty();
    }

    @Override
    public int getSize() {
        return this.arguments.size();
    }

    @Override
    public Optional<T> get(final int index) {
        if (index < 0 || index >= getSize()) {
            return Optional.empty();
        }
        T argument = this.arguments.get(index);
        return Optional.of(argument);
    }

    @Override
    public Stream<T> stream() {
        return this.arguments.stream();
    }

    @Override
    public List<T> toList() {
        return List.copyOf(this.arguments);
    }

    @Override
    public Iterator<T> iterator() {
        return this.arguments.iterator();
    }
}

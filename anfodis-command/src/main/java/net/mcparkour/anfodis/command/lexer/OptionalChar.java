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

package net.mcparkour.anfodis.command.lexer;

import java.util.NoSuchElementException;

final class OptionalChar {

    private static final OptionalChar EMPTY = new OptionalChar('\0', false);

    private final char value;
    private final boolean present;

    public static OptionalChar of(final char character) {
        return new OptionalChar(character, true);
    }

    public static OptionalChar empty() {
        return EMPTY;
    }

    private OptionalChar(final char value, final boolean present) {
        this.value = value;
        this.present = present;
    }

    public boolean isPresent() {
        return this.present;
    }

    public boolean isEmpty() {
        return !this.present;
    }

    public char get() {
        if (!this.present) {
            throw new NoSuchElementException("Char value is not present");
        }
        return this.value;
    }
}

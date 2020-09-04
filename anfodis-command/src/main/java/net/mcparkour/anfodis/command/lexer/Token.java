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

import java.util.Objects;

public class Token {

    private static final Token EMPTY_STRING = string("");

    private final String value;
    private final TokenType type;

    public static Token emptyString() {
        return EMPTY_STRING;
    }

    public static Token string(final String value) {
        return new Token(value, TokenType.STRING);
    }

    public Token(final String value, final TokenType type) {
        this.value = value;
        this.type = type;
    }

    public String getString() {
        if (this.type != TokenType.STRING) {
            throw new RuntimeException("Invalid token type: " + this.type + ", expected String");
        }
        return this.value;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        Token token = (Token) object;
        return Objects.equals(this.value, token.value) &&
            this.type == token.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value, this.type);
    }

    @Override
    public String toString() {
        return "Token{" +
            "value='" + this.value + "'" +
            ", type=" + this.type +
            "}";
    }

    public String getValue() {
        return this.value;
    }

    public TokenType getType() {
        return this.type;
    }
}

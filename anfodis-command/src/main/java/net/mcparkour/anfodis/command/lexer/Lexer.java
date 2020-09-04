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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.jetbrains.annotations.Nullable;

public class Lexer {

    private static final char STRING_SINGLE_QUOTE = '\'';
    private static final char STRING_DOUBLE_QUOTE = '\"';
    private static final char ESCAPING = '\\';

    public List<Token> tokenizeCollection(final Collection<String> arguments) {
        if (arguments.isEmpty()) {
            return List.of();
        }
        String argumentsString = String.join(" ", arguments);
        return tokenize(argumentsString);
    }

    public List<Token> tokenizeArray(final String[] arguments) {
        if (arguments.length == 0) {
            return List.of();
        }
        String argumentsString = String.join(" ", arguments);
        return tokenize(argumentsString);
    }

    public List<Token> tokenize(final @Nullable String arguments) {
        if (arguments == null) {
            return List.of();
        }
        List<Token> tokens = new ArrayList<>(arguments.length());
        CharacterIterator iterator = new CharacterIterator(arguments);
        OptionalChar current = iterator.current();
        boolean lastWhitespace = true;
        while (current.isPresent()) {
            char character = current.get();
            if (Character.isWhitespace(character)) {
                lastWhitespace = true;
            } else {
                lastWhitespace = false;
                Token token = tokenize(character, iterator);
                tokens.add(token);
            }
            iterator.next();
            current = iterator.current();
        }
        if (lastWhitespace) {
            tokens.add(Token.emptyString());
        }
        return tokens;
    }

    private Token tokenize(final char character, final CharacterIterator iterator) {
        switch (character) {
            case STRING_SINGLE_QUOTE:
            case STRING_DOUBLE_QUOTE:
                return tokenizeQuotedString(iterator);
            default:
                return tokenizeString(character, iterator);
        }
    }

    private Token tokenizeString(final char firstCharacter, final CharacterIterator iterator) {
        StringBuilder builder = new StringBuilder();
        builder.append(firstCharacter);
        iterator.next();
        OptionalChar current = iterator.current();
        while (current.isPresent()) {
            char character = current.get();
            if (Character.isWhitespace(character)) {
                iterator.previous();
                break;
            }
            builder.append(character);
            iterator.next();
            current = iterator.current();
        }
        String string = builder.toString();
        return Token.string(string);
    }

    private Token tokenizeQuotedString(final CharacterIterator iterator) {
        iterator.next();
        OptionalChar current = iterator.current();
        StringBuilder builder = new StringBuilder();
        boolean escaping = false;
        while (current.isPresent()) {
            char character = current.get();
            if (!escaping && character == ESCAPING) {
                escaping = true;
            } else if (!escaping && isQuote(character)) {
                break;
            } else {
                builder.append(character);
                escaping = false;
            }
            iterator.next();
            current = iterator.current();
        }
        String string = builder.toString();
        return Token.string(string);
    }

    private static boolean isQuote(final char character) {
        return character == STRING_SINGLE_QUOTE || character == STRING_DOUBLE_QUOTE;
    }
}

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

package net.mcparkour.anfodis;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

public class TestCommandSender {

    private Locale language;
    private Set<String> permissions;
    private boolean operator;
    private Queue<String> messages;

    public TestCommandSender() {
        this(Locale.US, Set.of(), true);
    }

    public TestCommandSender(final Locale language, final Set<String> permissions, final boolean operator) {
        this.language = language;
        this.permissions = permissions;
        this.operator = operator;
        this.messages = new LinkedList<>();
    }

    public boolean hasPermission(final String permission) {
        return this.operator || this.permissions.contains(permission);
    }

    public void receiveMessage(final String message) {
        this.messages.add(message);
    }

    @Nullable
    public String getLastMessage() {
        return this.messages.poll();
    }

    public List<String> getMessages() {
        int size = this.messages.size();
        List<String> messages = new ArrayList<>(size);
        while (!this.messages.isEmpty()) {
            String message = this.messages.poll();
            messages.add(message);
        }
        return messages;
    }

    public Locale getLanguage() {
        return this.language;
    }

    public void setLanguage(final Locale language) {
        this.language = language;
    }
}

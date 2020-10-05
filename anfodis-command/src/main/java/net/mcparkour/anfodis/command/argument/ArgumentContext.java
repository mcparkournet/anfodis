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

import net.mcparkour.craftmon.permission.Permission;

public class ArgumentContext {

    private final String name;
    private final boolean optional;
    private final boolean variadic;
    private final Permission permission;

    public ArgumentContext(
        final String name,
        final boolean optional,
        final boolean variadic,
        final Permission permission
    ) {
        this.name = name;
        this.optional = optional;
        this.variadic = variadic;
        this.permission = permission;
    }

    public String getName() {
        return this.name;
    }

    public boolean isOptional() {
        return this.optional;
    }

    public boolean isVariadic() {
        return this.variadic;
    }

    public Permission getPermission() {
        return this.permission;
    }
}

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

import java.lang.reflect.Field;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import org.jetbrains.annotations.Nullable;

class ArgumentData {

    private @Nullable Field argumentField;
    private @Nullable Class<? extends ArgumentCodec<?>> argumentCodecType;
    private @Nullable String name;
    private @Nullable Boolean optional;
    private @Nullable Boolean variadic;
    private @Nullable String permission;

    public @Nullable Field getArgumentField() {
        return this.argumentField;
    }

    public void setArgumentField(@Nullable final Field argumentField) {
        this.argumentField = argumentField;
    }

    public @Nullable String getName() {
        return this.name;
    }

    public void setName(@Nullable final String name) {
        this.name = name;
    }

    public @Nullable Class<? extends ArgumentCodec<?>> getArgumentCodecType() {
        return this.argumentCodecType;
    }

    public void setArgumentCodecType(final @Nullable Class<? extends ArgumentCodec<?>> argumentCodecType) {
        this.argumentCodecType = argumentCodecType;
    }

    public @Nullable Boolean getOptional() {
        return this.optional;
    }

    public void setOptional(final @Nullable Boolean optional) {
        this.optional = optional;
    }

    public @Nullable Boolean getVariadic() {
        return this.variadic;
    }

    public void setVariadic(final @Nullable Boolean variadic) {
        this.variadic = variadic;
    }

    public @Nullable String getPermission() {
        return this.permission;
    }

    public void setPermission(final @Nullable String permission) {
        this.permission = permission;
    }
}

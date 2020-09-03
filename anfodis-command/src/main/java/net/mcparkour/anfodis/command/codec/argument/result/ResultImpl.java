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

package net.mcparkour.anfodis.command.codec.argument.result;

import java.util.Optional;
import org.jetbrains.annotations.Nullable;

class ResultImpl<T> implements Result<T> {

    private final OkResult<T> okResult;
    private final ErrorResult errorResult;

    ResultImpl(final OkResult<T> okResult) {
        this(okResult, null);
    }

    ResultImpl(final ErrorResult errorResult) {
        this(null, errorResult);
    }

    private ResultImpl(final @Nullable OkResult<T> okResult, final @Nullable ErrorResult errorResult) {
        this.okResult = okResult;
        this.errorResult = errorResult;
    }

    @Override
    public boolean isOk() {
        return this.okResult != null;
    }

    @Override
    public boolean isError() {
        return this.errorResult != null;
    }

    @Override
    public Optional<OkResult<T>> getOkResult() {
        return Optional.ofNullable(this.okResult);
    }

    @Override
    public Optional<ErrorResult> getErrorResult() {
        return Optional.ofNullable(this.errorResult);
    }
}

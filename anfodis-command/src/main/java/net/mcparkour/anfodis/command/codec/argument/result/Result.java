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

public interface Result<T> {

    static <T> Result<T> ok(final @Nullable T result) {
        OkResult<T> okResult = OkResult.of(result);
        return new ResultImpl<>(okResult);
    }

    static <T> Result<T> error() {
        ErrorResult errorResult = ErrorResult.empty();
        return new ResultImpl<>(errorResult);
    }

    static <T> Result<T> error(final Runnable action) {
        ErrorResult errorResult = ErrorResult.of(action);
        return new ResultImpl<>(errorResult);
    }

    boolean isOk();

    boolean isError();

    Optional<OkResult<T>> getOkResult();

    Optional<ErrorResult> getErrorResult();
}

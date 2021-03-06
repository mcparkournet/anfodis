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

package net.mcparkour.anfodis.mapper;

import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public class ClassData {

    private @Nullable String first;
    private String second;
    private @Nullable String third;

    public ClassData() {}

    public ClassData(@Nullable final String first, final String second, @Nullable final String third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        ClassData classData = (ClassData) object;
        return Objects.equals(this.first, classData.first) &&
            Objects.equals(this.second, classData.second) &&
            Objects.equals(this.third, classData.third);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.first, this.second, this.third);
    }

    @Override
    public String toString() {
        return "ClassData{" +
            "first='" + this.first + "'" +
            ", second='" + this.second + "'" +
            ", third='" + this.third + "'" +
            "}";
    }

    public @Nullable String getFirst() {
        return this.first;
    }

    public void setFirst(@Nullable final String first) {
        this.first = first;
    }

    public String getSecond() {
        return this.second;
    }

    public void setSecond(final String second) {
        this.second = second;
    }

    public @Nullable String getThird() {
        return this.third;
    }

    public void setThird(@Nullable final String third) {
        this.third = third;
    }
}

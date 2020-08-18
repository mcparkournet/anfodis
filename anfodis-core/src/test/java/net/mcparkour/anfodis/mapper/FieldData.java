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

import java.lang.reflect.Field;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public class FieldData {

    private Field field;
    private String string1;
    @Nullable
    private String string2;

    public FieldData() {}

    public FieldData(Field field, String string1, @Nullable String string2) {
        this.field = field;
        this.string1 = string1;
        this.string2 = string2;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        FieldData fieldData = (FieldData) object;
        return Objects.equals(this.field, fieldData.field) &&
            Objects.equals(this.string1, fieldData.string1) &&
            Objects.equals(this.string2, fieldData.string2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.field, this.string1, this.string2);
    }

    @Override
    public String toString() {
        return "FieldData{" +
            "field=" + this.field +
            ", string1='" + this.string1 + "'" +
            ", string2='" + this.string2 + "'" +
            "}";
    }

    public Field getField() {
        return this.field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getString1() {
        return this.string1;
    }

    public void setString1(String string1) {
        this.string1 = string1;
    }

    @Nullable
    public String getString2() {
        return this.string2;
    }

    public void setString2(@Nullable String string2) {
        this.string2 = string2;
    }
}

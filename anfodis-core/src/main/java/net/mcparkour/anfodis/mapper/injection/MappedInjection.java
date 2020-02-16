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

package net.mcparkour.anfodis.mapper.injection;

import java.lang.reflect.Field;
import java.util.Objects;
import org.jetbrains.annotations.Nullable;

public class MappedInjection {

	@Nullable
	private Field injectionField;
	@Nullable
	private String codecKey;

	public MappedInjection() {}

	public MappedInjection(@Nullable Field injectionField, @Nullable String codecKey) {
		this.injectionField = injectionField;
		this.codecKey = codecKey;
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		MappedInjection that = (MappedInjection) object;
		return Objects.equals(this.injectionField, that.injectionField) &&
			Objects.equals(this.codecKey, that.codecKey);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.injectionField, this.codecKey);
	}

	@Override
	public String toString() {
		return "MappedInjection{" +
			"injectionField=" + this.injectionField +
			", codecKey='" + this.codecKey + "'" +
			"}";
	}

	@Nullable
	public Field getInjectionField() {
		return this.injectionField;
	}

	public void setInjectionField(@Nullable Field injectionField) {
		this.injectionField = injectionField;
	}

	@Nullable
	public String getCodecKey() {
		return this.codecKey;
	}

	public void setCodecKey(@Nullable String codecKey) {
		this.codecKey = codecKey;
	}
}

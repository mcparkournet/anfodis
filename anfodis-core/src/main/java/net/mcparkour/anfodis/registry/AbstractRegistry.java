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

package net.mcparkour.anfodis.registry;

import java.lang.annotation.Annotation;
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.mapper.ClassMapper;

public abstract class AbstractRegistry<T> implements Registry {

	private Class<? extends Annotation> annotationClass;
	private ClassMapper<T> mapper;
	private CodecRegistry<InjectionCodec<?>> injectionCodecRegistry;

	public AbstractRegistry(Class<? extends Annotation> annotation, ClassMapper<T> mapper, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry) {
		this.annotationClass = annotation;
		this.mapper = mapper;
		this.injectionCodecRegistry = injectionCodecRegistry;
	}

	@Override
	public void register(Class<?> annotatedClass) {
		if (!annotatedClass.isAnnotationPresent(this.annotationClass)) {
			throw new IllegalArgumentException("Class " + annotatedClass.getName() + " is not annotated with " + this.annotationClass.getName() + " annotation");
		}
		T listener = this.mapper.map(annotatedClass);
		register(listener);
	}

	protected abstract void register(T mapped);

	protected CodecRegistry<InjectionCodec<?>> getInjectionCodecRegistry() {
		return this.injectionCodecRegistry;
	}
}

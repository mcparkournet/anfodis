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
import java.lang.reflect.Method;
import java.util.List;
import net.mcparkour.anfodis.annotation.Inject;
import net.mcparkour.anfodis.annotation.executor.After;
import net.mcparkour.anfodis.annotation.executor.Before;
import net.mcparkour.anfodis.annotation.executor.Executor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MapperTest {

    @Test
    public void testMapFields() throws NoSuchFieldException {
        Field[] fields = TestClass.class.getDeclaredFields();
        List<FieldData> list = new ElementsMapperBuilder<Field, FieldData>()
            .data(FieldData::new)
            .singleElement(fieldData -> new SingleElementMapperBuilder<Field>()
                .annotation(Inject.class, inject -> fieldData.setString1(inject.value()))
                .annotation(TestAnnotation.class, testAnnotation -> fieldData.setString2(testAnnotation.value()))
                .elementConsumer(fieldData::setField)
                .build())
            .build()
            .map(fields);
        Assertions.assertEquals(List.of(new FieldData(TestClass.class.getDeclaredField("string1"), "foo1", "bar1"), new FieldData(TestClass.class.getDeclaredField("string2"), "foo2", null)), list);
    }

    @Test
    public void testMapMethods() throws NoSuchMethodException {
        Method[] methods = TestClass.class.getDeclaredMethods();
        MethodData data = new ElementsMapperBuilder<Method, MethodData>()
            .data(MethodData::new)
            .singleElement(methodData -> new SingleElementMapperBuilder<Method>()
                .annotation(Before.class)
                .elementConsumer(methodData::setBeforeMethod)
                .build())
            .singleElement(methodData -> new SingleElementMapperBuilder<Method>()
                .annotation(Executor.class)
                .elementConsumer(methodData::setExecutorMethod)
                .build())
            .singleElement(methodData -> new SingleElementMapperBuilder<Method>()
                .annotation(After.class)
                .elementConsumer(methodData::setAfterMethod)
                .build())
            .build()
            .mapFirst(methods);
        Assertions.assertEquals(new MethodData(TestClass.class.getDeclaredMethod("before"), TestClass.class.getDeclaredMethod("executor"), TestClass.class.getDeclaredMethod("after")), data);
    }

    @Test
    public void testMapClass() {
        List<ClassData> list = new ElementsMapperBuilder<Class<?>, ClassData>()
            .data(ClassData::new)
            .singleElement(classData -> new SingleElementMapperBuilder<Class<?>>()
                .annotation(TestClassAnnotation.class)
                .annotation(TestClassAnnotationTwo.class, testClassAnnotationTwo -> classData.setSecond(testClassAnnotationTwo.value()))
                .annotation(TestClassAnnotationThree.class, testClassAnnotationThree -> classData.setThird(testClassAnnotationThree.value()))
                .build())
            .build()
            .map(new Class[] {TestClass.class});
        Assertions.assertEquals(List.of(new ClassData(null, "foobar", null)), list);
    }
}

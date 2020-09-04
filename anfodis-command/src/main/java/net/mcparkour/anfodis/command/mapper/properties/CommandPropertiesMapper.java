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

package net.mcparkour.anfodis.command.mapper.properties;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.mcparkour.anfodis.command.annotation.properties.Aliases;
import net.mcparkour.anfodis.command.annotation.properties.AliasesTranslation;
import net.mcparkour.anfodis.command.annotation.properties.Asynchronous;
import net.mcparkour.anfodis.command.annotation.properties.Command;
import net.mcparkour.anfodis.command.annotation.properties.Description;
import net.mcparkour.anfodis.command.annotation.properties.DescriptionTranslation;
import net.mcparkour.anfodis.command.annotation.properties.Permission;
import net.mcparkour.anfodis.mapper.ElementsMapper;
import net.mcparkour.anfodis.mapper.ElementsMapperBuilder;
import net.mcparkour.anfodis.mapper.Mapper;
import net.mcparkour.anfodis.mapper.MapperBuilderApplier;
import net.mcparkour.anfodis.mapper.SingleElementMapperBuilder;
import org.jetbrains.annotations.Nullable;

public class CommandPropertiesMapper<P extends CommandProperties, D extends CommandPropertiesData>
    implements Mapper<Class<?>, P> {

    private final Function<D, P> propertiesSupplier;
    private final ElementsMapper<Class<?>, D> mapper;

    public CommandPropertiesMapper(
        final Function<D, P> propertiesSupplier,
        final Supplier<D> propertiesDataSupplier
    ) {
        this(propertiesSupplier, propertiesDataSupplier, null);
    }

    public CommandPropertiesMapper(
        final Function<D, P> propertiesSupplier,
        final Supplier<D> propertiesDataSupplier,
        final @Nullable MapperBuilderApplier<Class<?>, D> additional
    ) {
        this.propertiesSupplier = propertiesSupplier;
        this.mapper = createMapper(propertiesDataSupplier, additional);
    }

    private static <D extends CommandPropertiesData> ElementsMapper<Class<?>, D> createMapper(
        final Supplier<D> propertiesDataSupplier,
        final @Nullable MapperBuilderApplier<Class<?>, D> additional
    ) {
        MapperBuilderApplier<Class<?>, D> applier = (builder, data) -> builder
            .required(Command.class, command ->
                data.setName(command.value()))
            .additional(Description.class, description ->
                data.setDescription(description.value()))
            .additional(DescriptionTranslation.class, translation ->
                data.setDescriptionTranslationId(translation.value()))
            .additional(Aliases.class, aliases ->
                data.setAliases(aliases.value()))
            .additional(AliasesTranslation.class, translation ->
                data.setAliasesTranslationId(translation.value()))
            .additional(Permission.class, permission ->
                data.setPermission(permission.value()))
            .additional(Asynchronous.class, asynchronous ->
                data.setAsynchronous(true));
        if (additional != null) {
            applier = applier.andThen(additional);
        }
        return new ElementsMapperBuilder<Class<?>, D>()
            .data(propertiesDataSupplier)
            .element(applier)
            .build();
    }

    @Override
    public P map(final Collection<Class<?>> elements) {
        D propertiesData = this.mapper.mapToSingle(elements);
        return this.propertiesSupplier.apply(propertiesData);
    }
}

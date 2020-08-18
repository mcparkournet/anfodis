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

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.mcparkour.anfodis.command.annotation.properties.Aliases;
import net.mcparkour.anfodis.command.annotation.properties.AliasesTranslation;
import net.mcparkour.anfodis.command.annotation.properties.Command;
import net.mcparkour.anfodis.command.annotation.properties.Description;
import net.mcparkour.anfodis.command.annotation.properties.DescriptionTranslation;
import net.mcparkour.anfodis.command.annotation.properties.Permission;
import net.mcparkour.anfodis.mapper.ElementsMapperBuilder;
import net.mcparkour.anfodis.mapper.Mapper;
import net.mcparkour.anfodis.mapper.SingleElementMapperBuilder;

public class CommandPropertiesMapper<P extends CommandProperties, D extends CommandPropertiesData> implements Mapper<Class<?>, P> {

    private Function<D, P> propertiesSupplier;
    private Supplier<D> propertiesDataSupplier;
    private BiConsumer<D, SingleElementMapperBuilder<Class<?>>> additional;

    public CommandPropertiesMapper(Function<D, P> propertiesSupplier, Supplier<D> propertiesDataSupplier) {
        this(propertiesSupplier, propertiesDataSupplier, (data, builder) -> {});
    }

    public CommandPropertiesMapper(Function<D, P> propertiesSupplier, Supplier<D> propertiesDataSupplier, BiConsumer<D, SingleElementMapperBuilder<Class<?>>> additional) {
        this.propertiesSupplier = propertiesSupplier;
        this.propertiesDataSupplier = propertiesDataSupplier;
        this.additional = additional;
    }

    @Override
    public P map(Iterable<Class<?>> elements) {
        return new ElementsMapperBuilder<Class<?>, D>()
            .data(this.propertiesDataSupplier)
            .singleElement(data -> {
                SingleElementMapperBuilder<Class<?>> builder = new SingleElementMapperBuilder<Class<?>>()
                    .annotation(Command.class, command -> data.setName(command.value()))
                    .annotation(Description.class, description -> data.setDescription(description.value()))
                    .annotation(DescriptionTranslation.class, descriptionTranslation -> data.setDescriptionTranslationId(descriptionTranslation.value()))
                    .annotation(Aliases.class, aliases -> data.setAliases(aliases.value()))
                    .annotation(AliasesTranslation.class, aliasesTranslation -> data.setAliasesTranslationId(aliasesTranslation.value()))
                    .annotation(Permission.class, permission -> data.setPermission(permission.value()));
                this.additional.accept(data, builder);
                return builder.build();
            })
            .build()
            .mapFirstOptional(elements)
            .map(this.propertiesSupplier)
            .orElseThrow();
    }
}

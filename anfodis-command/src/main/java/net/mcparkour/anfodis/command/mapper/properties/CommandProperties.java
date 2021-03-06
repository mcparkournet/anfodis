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

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import net.mcparkour.craftmon.permission.Permission;
import org.jetbrains.annotations.Nullable;

public class CommandProperties {

    private final String name;
    private final String description;
    private final @Nullable String descriptionTranslationId;
    private final Set<String> aliases;
    private final Set<String> lowerCaseAliases;
    private final @Nullable String aliasesTranslationId;
    private final Permission permission;
    private final boolean asynchronous;

    public CommandProperties(final CommandPropertiesData propertiesData) {
        String name = propertiesData.getName();
        this.name = Objects.requireNonNull(name, "Command name is null");
        String description = propertiesData.getDescription();
        this.description = description == null ?
            "" :
            description;
        this.descriptionTranslationId = propertiesData.getDescriptionTranslationId();
        String[] aliases = propertiesData.getAliases();
        this.aliases = aliases == null ?
            Set.of() :
            Set.of(aliases);
        this.lowerCaseAliases = this.aliases.stream()
            .map(String::toLowerCase)
            .collect(Collectors.toUnmodifiableSet());
        this.aliasesTranslationId = propertiesData.getAliasesTranslationId();
        String permissionName = propertiesData.getPermission();
        this.permission = permissionName == null ?
            Permission.empty() :
            Permission.of(
                permissionName.isEmpty() ?
                    this.name :
                    permissionName
            );
        Boolean asynchronous = propertiesData.getAsynchronous();
        this.asynchronous = asynchronous != null && asynchronous;
    }

    public Permission getPermission() {
        return this.permission;
    }

    public Set<String> getAllNames() {
        Set<String> names = new HashSet<>(1);
        names.add(this.name);
        names.addAll(this.aliases);
        return names;
    }

    public String getDefaultUsage() {
        return "/" + this.name;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public Optional<String> getDescriptionTranslationId() {
        return Optional.ofNullable(this.descriptionTranslationId);
    }

    public Set<String> getAliases() {
        return this.aliases;
    }

    public Set<String> getLowerCaseAliases() {
        return this.lowerCaseAliases;
    }

    public Optional<String> getAliasesTranslationId() {
        return Optional.ofNullable(this.aliasesTranslationId);
    }

    public boolean isAsynchronous() {
        return this.asynchronous;
    }
}

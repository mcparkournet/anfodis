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

import java.util.List;
import org.jetbrains.annotations.Nullable;

public class CommandProperties<D extends CommandPropertiesData> {

	private D propertiesData;

	public CommandProperties(D propertiesData) {
		this.propertiesData = propertiesData;
	}

	@Nullable
	public String getPermission() {
		String permission = this.propertiesData.getPermission();
		if (permission == null) {
			return null;
		}
		if (permission.isEmpty()) {
			return getName();
		}
		return permission;
	}

	public String getDefaultUsage() {
		return "/" + getName();
	}

	public String getName() {
		String name = this.propertiesData.getName();
		if (name == null) {
			throw new RuntimeException("Command name is null");
		}
		return name;
	}

	public String getDescription() {
		String description = this.propertiesData.getDescription();
		if (description == null) {
			return "";
		}
		return description;
	}

	@Nullable
	public String getDescriptionTranslationId() {
		return this.propertiesData.getDescriptionTranslationId();
	}

	public List<String> getAliases() {
		String[] aliases = this.propertiesData.getAliases();
		if (aliases == null) {
			return List.of();
		}
		return List.of(aliases);
	}

	@Nullable
	public String getAliasesTranslationId() {
		return this.propertiesData.getAliasesTranslationId();
	}

	protected D getPropertiesData() {
		return this.propertiesData;
	}
}

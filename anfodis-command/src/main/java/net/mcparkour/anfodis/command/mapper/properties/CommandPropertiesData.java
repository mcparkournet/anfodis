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

import org.jetbrains.annotations.Nullable;

public class CommandPropertiesData {

	@Nullable
	private String name;
	@Nullable
	private String description;
	@Nullable
	private String descriptionTranslationId;
	@Nullable
	private String[] aliases;
	@Nullable
	private String aliasesTranslationId;
	@Nullable
	private String permission;

	@Nullable
	public String getName() {
		return this.name;
	}

	public void setName(@Nullable String name) {
		this.name = name;
	}

	@Nullable
	public String getDescription() {
		return this.description;
	}

	public void setDescription(@Nullable String description) {
		this.description = description;
	}

	@Nullable
	public String getDescriptionTranslationId() {
		return this.descriptionTranslationId;
	}

	public void setDescriptionTranslationId(@Nullable String descriptionTranslationId) {
		this.descriptionTranslationId = descriptionTranslationId;
	}

	@Nullable
	public String[] getAliases() {
		return this.aliases;
	}

	public void setAliases(@Nullable String[] aliases) {
		this.aliases = aliases;
	}

	@Nullable
	public String getAliasesTranslationId() {
		return this.aliasesTranslationId;
	}

	public void setAliasesTranslationId(@Nullable String aliasesTranslationId) {
		this.aliasesTranslationId = aliasesTranslationId;
	}

	@Nullable
	public String getPermission() {
		return this.permission;
	}

	public void setPermission(@Nullable String permission) {
		this.permission = permission;
	}
}

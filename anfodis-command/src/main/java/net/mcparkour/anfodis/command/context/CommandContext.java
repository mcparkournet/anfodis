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

package net.mcparkour.anfodis.command.context;

import java.util.List;
import net.mcparkour.anfodis.handler.RootContext;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.craftmon.permission.PermissionBuilder;
import org.jetbrains.annotations.Nullable;

public class CommandContext<T> extends RootContext {

	private CommandSender<T> sender;
	private List<String> arguments;
	@Nullable
	private Permission permission;

	public CommandContext(CommandSender<T> sender, List<String> arguments, @Nullable Permission permission) {
		this.sender = sender;
		this.arguments = arguments;
		this.permission = permission;
	}

	public void removeFirstArgument() {
		int size = this.arguments.size();
		this.arguments = this.arguments.subList(1, size);
	}

	public void appendPermissionNode(String node) {
		if (this.permission != null) {
			this.permission = new PermissionBuilder()
				.with(this.permission)
				.node(node)
				.build();
		}
	}

	public CommandSender<T> getSender() {
		return this.sender;
	}

	public List<String> getArguments() {
		return List.copyOf(this.arguments);
	}

	@Nullable
	public Permission getPermission() {
		return this.permission;
	}
}

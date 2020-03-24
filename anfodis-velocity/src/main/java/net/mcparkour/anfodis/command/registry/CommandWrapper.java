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

package net.mcparkour.anfodis.command.registry;

import java.util.List;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import net.mcparkour.anfodis.command.handler.CommandContext;
import net.mcparkour.anfodis.command.handler.CompletionContext;
import net.mcparkour.anfodis.command.handler.CompletionContextHandler;
import net.mcparkour.anfodis.command.handler.VelocityCommandSender;
import net.mcparkour.anfodis.handler.ContextHandler;
import net.mcparkour.craftmon.permission.Permission;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Nullable;

public class CommandWrapper implements Command {

	@Nullable
	private Permission permission;
	private ContextHandler<CommandContext> handler;
	private CompletionContextHandler<CompletionContext> completionHandler;

	public CommandWrapper(@Nullable Permission permission, ContextHandler<CommandContext> handler, CompletionContextHandler<CompletionContext> completionHandler) {
		this.permission = permission;
		this.handler = handler;
		this.completionHandler = completionHandler;
	}

	@Override
	public void execute(@NonNull CommandSource source, @NonNull String[] args) {
		VelocityCommandSender paperSender = new VelocityCommandSender(source);
		List<String> arguments = List.of(args);
		CommandContext context = new CommandContext(paperSender, arguments, this.permission);
		this.handler.handle(context);
	}

	@Override
	public List<String> suggest(@NonNull CommandSource source, @NonNull String[] currentArgs) {
		VelocityCommandSender paperSender = new VelocityCommandSender(source);
		List<String> arguments = List.of(currentArgs);
		CompletionContext context = new CompletionContext(paperSender, arguments, this.permission);
		return this.completionHandler.handle(context);
	}
}

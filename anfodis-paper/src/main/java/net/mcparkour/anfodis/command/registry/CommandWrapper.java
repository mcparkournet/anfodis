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
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.codec.completion.CompletionCodec;
import net.mcparkour.anfodis.command.handler.CommandContext;
import net.mcparkour.anfodis.command.handler.CompletionContext;
import net.mcparkour.anfodis.command.handler.CompletionHandler;
import net.mcparkour.anfodis.command.handler.PaperCommandHandler;
import net.mcparkour.anfodis.command.handler.PaperCommandSender;
import net.mcparkour.anfodis.command.mapper.PaperCommand;
import net.mcparkour.anfodis.handler.Handler;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.intext.translation.Translations;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CommandWrapper extends Command {

	private PaperCommand command;
	@Nullable
	private Permission permission;
	private Translations translations;
	private CodecRegistry<InjectionCodec<?>> injectionCodecRegistry;
	private CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry;
	private CodecRegistry<CompletionCodec> completionCodecRegistry;

	protected CommandWrapper(String name, String description, String usageMessage, List<String> aliases, PaperCommand command, @Nullable Permission permission, Translations translations, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, CodecRegistry<CompletionCodec> completionCodecRegistry) {
		super(name, description, usageMessage, aliases);
		this.command = command;
		this.permission = permission;
		this.translations = translations;
		this.injectionCodecRegistry = injectionCodecRegistry;
		this.argumentCodecRegistry = argumentCodecRegistry;
		this.completionCodecRegistry = completionCodecRegistry;
	}

	@Override
	public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
		PaperCommandSender paperSender = new PaperCommandSender(sender);
		List<String> arguments = List.of(args);
		CommandContext context = new CommandContext(paperSender, arguments, this.permission);
		Handler handler = new PaperCommandHandler(this.command, context, this.translations, this.injectionCodecRegistry, this.argumentCodecRegistry);
		handler.handle();
		return true;
	}

	@Override
	@NotNull
	public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args, @Nullable Location location) {
		PaperCommandSender paperSender = new PaperCommandSender(sender);
		List<String> arguments = List.of(args);
		CompletionContext context = new CompletionContext(paperSender, arguments, this.permission);
		CompletionHandler handler = new CompletionHandler(this.command, context, this.completionCodecRegistry);
		return handler.handle();
	}
}

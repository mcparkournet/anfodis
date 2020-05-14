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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.codec.completion.CompletionCodec;
import net.mcparkour.anfodis.command.context.CommandContext;
import net.mcparkour.anfodis.command.handler.CommandContextHandler;
import net.mcparkour.anfodis.command.handler.CommandContextSupplier;
import net.mcparkour.anfodis.command.handler.CompletionContext;
import net.mcparkour.anfodis.command.handler.CompletionContextHandler;
import net.mcparkour.anfodis.command.mapper.CompletionCommand;
import net.mcparkour.anfodis.mapper.RootMapper;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.intext.message.MessageReceiverFactory;

public abstract class AbstractCompletionRegistry<T extends CompletionCommand<T, ?, ?, ?>, C extends CommandContext<S>, D extends CompletionContext<S>, S> extends AbstractCommandRegistry<T, C, S> {

	private CompletionHandlerSupplier<T, D, S> completionHandlerSupplier;
	private CommandContextSupplier<D, S> completionContextSupplier;
	private CodecRegistry<CompletionCodec> completionCodecRegistry;

	public AbstractCompletionRegistry(RootMapper<T> mapper, CommandHandlerSupplier<T, C, S> commandHandlerSupplier, CommandExecutorHandlerSupplier<T, C> commandExecutorHandlerSupplier, CommandContextSupplier<C, S> commandContextSupplier, CompletionHandlerSupplier<T, D, S> completionHandlerSupplier, CommandContextSupplier<D, S> completionContextSupplier, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, CodecRegistry<CompletionCodec> completionCodecRegistry, MessageReceiverFactory<S> messageReceiverFactory, Permission basePermission) {
		super(mapper, commandHandlerSupplier, commandExecutorHandlerSupplier, commandContextSupplier, injectionCodecRegistry, argumentCodecRegistry, messageReceiverFactory, basePermission);
		this.completionContextSupplier = completionContextSupplier;
		this.completionHandlerSupplier = completionHandlerSupplier;
		this.completionCodecRegistry = completionCodecRegistry;
	}

	@Override
	public void register(T root) {
		CommandContextHandler<C> commandHandler = createCommandHandler(root);
		CompletionContextHandler<D> completionHandler = createCompletionHandler(root);
		register(root, commandHandler, completionHandler);
	}

	private CompletionContextHandler<D> createCompletionHandler(T command) {
		List<T> subCommands = command.getSubCommands();
		int size = subCommands.size();
		Map<T, CompletionContextHandler<D>> handlers = new HashMap<>(size);
		for (T subCommand : subCommands) {
			CompletionContextHandler<D> handler = createCompletionHandler(subCommand);
			handlers.put(subCommand, handler);
		}
		return this.completionHandlerSupplier.supply(command, this.completionCodecRegistry, handlers, this.completionContextSupplier);
	}

	@Override
	public void register(T command, CommandContextHandler<C> commandHandler) {
		register(command, commandHandler, context -> List.of());
	}

	public abstract void register(T command, CommandContextHandler<C> commandHandler, CompletionContextHandler<D> completionHandler);
}

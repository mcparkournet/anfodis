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
import net.mcparkour.anfodis.command.context.CommandContext;
import net.mcparkour.anfodis.command.handler.CommandHandler;
import net.mcparkour.anfodis.command.mapper.Command;
import net.mcparkour.anfodis.command.mapper.properties.CommandProperties;
import net.mcparkour.anfodis.handler.ContextHandler;
import net.mcparkour.anfodis.mapper.RootMapper;
import net.mcparkour.anfodis.registry.AbstractRegistry;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.craftmon.permission.PermissionBuilder;
import net.mcparkour.intext.message.MessageReceiverFactory;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractCommandRegistry<T extends Command<T, ?, ?, ?>, C extends CommandContext<?>, S> extends AbstractRegistry<T, C> {

	private CommandHandlerSupplier<T, C> commandHandlerSupplier;
	private CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry;
	private MessageReceiverFactory<S> messageReceiverFactory;
	private String permissionPrefix;

	public AbstractCommandRegistry(RootMapper<T> mapper, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, MessageReceiverFactory<S> messageReceiverFactory, String permissionPrefix) {
		this(mapper, CommandHandler::new, injectionCodecRegistry, argumentCodecRegistry, messageReceiverFactory, permissionPrefix);
	}

	public AbstractCommandRegistry(RootMapper<T> mapper, CommandHandlerSupplier<T, C> commandHandlerSupplier, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, MessageReceiverFactory<S> messageReceiverFactory, String permissionPrefix) {
		super(net.mcparkour.anfodis.command.annotation.properties.Command.class, mapper, injectionCodecRegistry);
		this.commandHandlerSupplier = commandHandlerSupplier;
		this.argumentCodecRegistry = argumentCodecRegistry;
		this.messageReceiverFactory = messageReceiverFactory;
		this.permissionPrefix = permissionPrefix;
	}

	@Override
	public void register(T root) {
		ContextHandler<C> handler = createCommandHandler(root);
		register(root, handler);
	}

	protected ContextHandler<C> createCommandHandler(T command) {
		CodecRegistry<InjectionCodec<?>> injectionCodecRegistry = getInjectionCodecRegistry();
		List<T> subCommands = command.getSubCommands();
		int size = subCommands.size();
		Map<T, ContextHandler<C>> handlers = new HashMap<>(size);
		for (T subCommand : subCommands) {
			ContextHandler<C> handler = createCommandHandler(subCommand);
			handlers.put(subCommand, handler);
		}
		return this.commandHandlerSupplier.supply(command, injectionCodecRegistry, this.argumentCodecRegistry, handlers);
	}

	@Nullable
	protected Permission createPermission(CommandProperties<?> properties) {
		String commandPermission = properties.getPermission();
		if (commandPermission == null) {
			return null;
		}
		return new PermissionBuilder()
			.node(this.permissionPrefix)
			.node(commandPermission)
			.build();
	}

	protected CodecRegistry<ArgumentCodec<?>> getArgumentCodecRegistry() {
		return this.argumentCodecRegistry;
	}

	protected MessageReceiverFactory<S> getMessageReceiverFactory() {
		return this.messageReceiverFactory;
	}

	protected String getPermissionPrefix() {
		return this.permissionPrefix;
	}
}

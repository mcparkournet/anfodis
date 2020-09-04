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
import net.mcparkour.anfodis.codec.registry.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.Messenger;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.context.CommandContext;
import net.mcparkour.anfodis.command.handler.CommandContextHandler;
import net.mcparkour.anfodis.command.handler.CommandContextSupplier;
import net.mcparkour.anfodis.command.mapper.Command;
import net.mcparkour.anfodis.handler.ContextHandler;
import net.mcparkour.anfodis.mapper.RootMapper;
import net.mcparkour.anfodis.mapper.executor.Executor;
import net.mcparkour.anfodis.registry.AbstractRegistry;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.intext.message.MessageReceiverFactory;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractCommandRegistry<T extends Command<T, ?, ?, ?>, C extends CommandContext<S>, S, M extends Messenger<T, S>> extends AbstractRegistry<T, C> {

    private final CommandHandlerSupplier<T, C, S, M> commandHandlerSupplier;
    private final CommandExecutorHandlerSupplier<T, C> commandExecutorHandlerSupplier;
    private final CommandContextSupplier<C, S> contextSupplier;
    private final CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry;
    private final MessageReceiverFactory<S> messageReceiverFactory;
    private final M messenger;
    private final Permission basePermission;

    public AbstractCommandRegistry(
        final RootMapper<T> mapper,
        final CommandHandlerSupplier<T, C, S, M> commandHandlerSupplier,
        final CommandExecutorHandlerSupplier<T, C> commandExecutorHandlerSupplier,
        final CommandContextSupplier<C, S> contextSupplier,
        final CodecRegistry<InjectionCodec<?>> injectionCodecRegistry,
        final CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry,
        final MessageReceiverFactory<S> messageReceiverFactory,
        final M messenger,
        final Permission basePermission
    ) {
        super(net.mcparkour.anfodis.command.annotation.properties.Command.class, mapper, injectionCodecRegistry);
        this.commandHandlerSupplier = commandHandlerSupplier;
        this.commandExecutorHandlerSupplier = commandExecutorHandlerSupplier;
        this.contextSupplier = contextSupplier;
        this.argumentCodecRegistry = argumentCodecRegistry;
        this.messageReceiverFactory = messageReceiverFactory;
        this.messenger = messenger;
        this.basePermission = basePermission;
    }

    @Override
    public void register(final T root) {
        CommandContextHandler<C> handler = createCommandHandler(root);
        register(root, handler);
    }

    @Override
    public void register(final T root, final ContextHandler<C> handler) {
        register(root, context -> {
            Object instance = root.createInstance();
            handler.handle(context, instance);
        });
    }

    protected CommandContextHandler<C> createCommandHandler(final T command) {
        List<T> subCommands = command.getSubCommands();
        int size = subCommands.size();
        Map<T, CommandContextHandler<C>> handlers = new HashMap<>(size);
        for (final T subCommand : subCommands) {
            CommandContextHandler<C> handler = createCommandHandler(subCommand);
            handlers.put(subCommand, handler);
        }
        ContextHandler<C> executorHandler = createCommandExecutorHandler(command);
        return this.commandHandlerSupplier.supply(command, handlers, executorHandler, this.contextSupplier, this.messenger);
    }

    @Nullable
    private ContextHandler<C> createCommandExecutorHandler(final T command) {
        Executor executor = command.getExecutor();
        if (!executor.hasExecutor()) {
            return null;
        }
        CodecRegistry<InjectionCodec<?>> injectionCodecRegistry = getInjectionCodecRegistry();
        return this.commandExecutorHandlerSupplier.supply(command, injectionCodecRegistry, this.argumentCodecRegistry);
    }

    public abstract void register(T command, CommandContextHandler<C> commandHandler);

    protected MessageReceiverFactory<S> getMessageReceiverFactory() {
        return this.messageReceiverFactory;
    }

    protected Permission getBasePermission() {
        return this.basePermission;
    }
}

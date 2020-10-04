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
import net.mcparkour.anfodis.command.codec.completion.CompletionCodec;
import net.mcparkour.anfodis.command.context.CommandContext;
import net.mcparkour.anfodis.command.context.CommandContextBuilder;
import net.mcparkour.anfodis.command.handler.CommandContextBuilderHandler;
import net.mcparkour.anfodis.command.handler.CommandContextCreator;
import net.mcparkour.anfodis.command.handler.CompletionContext;
import net.mcparkour.anfodis.command.handler.CompletionContextBuilder;
import net.mcparkour.anfodis.command.handler.CompletionContextBuilderHandler;
import net.mcparkour.anfodis.command.mapper.CompletionCommand;
import net.mcparkour.anfodis.mapper.RootMapper;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.intext.message.MessageReceiverFactory;

public abstract class AbstractCompletionRegistry<T extends CompletionCommand<T, ?, ?, ?>, C1 extends CommandContext<T, S>, B1 extends CommandContextBuilder<C1, T, S>, C2 extends CompletionContext<T, S>, B2 extends CompletionContextBuilder<C2, T, S>, S, M extends Messenger<T, S>> extends AbstractCommandRegistry<T, C1, B1, S, M> {

    private final CompletionHandlerSupplier<T, C2, B2, S> completionHandlerSupplier;
    private final CommandContextCreator<T, C2, S> completionContextSupplier;
    private final CodecRegistry<CompletionCodec> completionCodecRegistry;

    public AbstractCompletionRegistry(
        final RootMapper<T> mapper,
        final CommandHandlerSupplier<T, C1, B1, S, M> commandHandlerSupplier,
        final CommandExecutorHandlerSupplier<T, C1> commandExecutorHandlerSupplier,
        final CommandContextCreator<T, C1, S> commandContextCreator,
        final CompletionHandlerSupplier<T, C2, B2, S> completionHandlerSupplier,
        final CommandContextCreator<T, C2, S> completionContextSupplier,
        final CodecRegistry<InjectionCodec<?>> injectionCodecRegistry,
        final CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry,
        final CodecRegistry<CompletionCodec> completionCodecRegistry,
        final MessageReceiverFactory<S> messageReceiverFactory,
        final M messenger,
        final Permission basePermission
    ) {
        super(
            mapper,
            commandHandlerSupplier,
            commandExecutorHandlerSupplier,
            commandContextCreator,
            injectionCodecRegistry,
            argumentCodecRegistry,
            messageReceiverFactory,
            messenger,
            basePermission
        );
        this.completionContextSupplier = completionContextSupplier;
        this.completionHandlerSupplier = completionHandlerSupplier;
        this.completionCodecRegistry = completionCodecRegistry;
    }

    @Override
    public void register(final T root) {
        CommandContextBuilderHandler<B1, C1> commandHandler = createCommandHandler(root);
        CompletionContextBuilderHandler<B2, C2> completionHandler = createCompletionHandler(root);
        register(root, commandHandler, completionHandler);
    }

    private CompletionContextBuilderHandler<B2, C2> createCompletionHandler(final T command) {
        List<T> subCommands = command.getSubCommands();
        int size = subCommands.size();
        Map<T, CompletionContextBuilderHandler<B2, C2>> handlers = new HashMap<>(size);
        for (final T subCommand : subCommands) {
            CompletionContextBuilderHandler<B2, C2> handler = createCompletionHandler(subCommand);
            handlers.put(subCommand, handler);
        }
        return this.completionHandlerSupplier.supply(command, this.completionCodecRegistry, handlers, this.completionContextSupplier);
    }

    @Override
    public void register(final T command, final CommandContextBuilderHandler<B1, C1> commandHandler) {
        register(command, commandHandler, context -> List.of());
    }

    public abstract void register(T command, CommandContextBuilderHandler<B1, C1> commandHandler, CompletionContextBuilderHandler<B2, C2> completionHandler);
}

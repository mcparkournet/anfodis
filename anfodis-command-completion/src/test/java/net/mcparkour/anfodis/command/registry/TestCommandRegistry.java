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

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import net.mcparkour.anfodis.codec.context.TransformCodec;
import net.mcparkour.anfodis.codec.registry.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.TestMessenger;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.codec.completion.CompletionCodec;
import net.mcparkour.anfodis.command.context.TestCommandContext;
import net.mcparkour.anfodis.command.context.TestCommandContextBuilder;
import net.mcparkour.anfodis.command.context.TestSender;
import net.mcparkour.anfodis.command.context.TestCompletionContext;
import net.mcparkour.anfodis.command.context.TestCompletionContextBuilder;
import net.mcparkour.anfodis.command.handler.CommandContextBuilderHandler;
import net.mcparkour.anfodis.command.handler.CompletionContextBuilderHandler;
import net.mcparkour.anfodis.command.handler.CompletionHandler;
import net.mcparkour.anfodis.command.handler.TestCommandExecutorHandler;
import net.mcparkour.anfodis.command.handler.TestCommandHandler;
import net.mcparkour.anfodis.command.mapper.TestCommand;
import net.mcparkour.anfodis.command.mapper.TestCommandMapper;
import net.mcparkour.anfodis.command.mapper.properties.TestCommandProperties;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.intext.message.MessageReceiver;
import net.mcparkour.intext.message.MessageReceiverFactory;

public class TestCommandRegistry
    extends AbstractCompletionRegistry<TestCommand, TestCommandContext, TestCommandContextBuilder, TestCompletionContext, TestCompletionContextBuilder, net.mcparkour.anfodis.TestCommandSender, TestMessenger> {

    private static final TestCommandMapper COMMAND_MAPPER = new TestCommandMapper();

    private final Map<String, CommandWrapper> commandManager;

    public TestCommandRegistry(
        final CodecRegistry<InjectionCodec<?>> injectionCodecRegistry,
        final CodecRegistry<TransformCodec<TestCommandContext, ?>> transformCodecRegistry,
        final CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry,
        final CodecRegistry<CompletionCodec> completionCodecRegistry,
        final MessageReceiverFactory<net.mcparkour.anfodis.TestCommandSender> messageReceiverFactory,
        final TestMessenger messenger,
        final Permission basePermission,
        final Map<String, CommandWrapper> commandManager
    ) {
        super(
            COMMAND_MAPPER,
            TestCommandHandler::new,
            TestCommandExecutorHandler::new,
            TestCommandContext::new,
            CompletionHandler::new,
            TestCompletionContext::new,
            injectionCodecRegistry,
            transformCodecRegistry,
            argumentCodecRegistry,
            completionCodecRegistry,
            messageReceiverFactory,
            messenger,
            basePermission
        );
        this.commandManager = commandManager;
    }

    @Override
    public void register(final TestCommand command, final CommandContextBuilderHandler<TestCommandContextBuilder, TestCommandContext> commandHandler, final CompletionContextBuilderHandler<TestCompletionContextBuilder, TestCompletionContext> completionHandler) {
        TestCommandProperties properties = command.getProperties();
        Set<String> names = properties.getAllNames();
        Permission basePermission = getBasePermission();
        Permission commandPermission = properties.getPermission();
        Permission permission = commandPermission.withFirst(basePermission);
        boolean asynchronous = properties.isAsynchronous();
        if (asynchronous) {
            throw new RuntimeException("Asynchronous command execution is not supported");
        }
        register(command, names, permission, false, commandHandler, completionHandler);
    }

    @SuppressWarnings("SameParameterValue")
    private void register(
        final TestCommand command,
        final Collection<String> aliases,
        final Permission permission,
        final boolean asynchronous,
        final CommandContextBuilderHandler<TestCommandContextBuilder, TestCommandContext> commandHandler,
        final CompletionContextBuilderHandler<TestCompletionContextBuilder, TestCompletionContext> completionHandler
    ) {
        MessageReceiverFactory<net.mcparkour.anfodis.TestCommandSender> messageReceiverFactory = getMessageReceiverFactory();
        TestCommandExecutor commandExecutor = (sender, arguments) -> {
            MessageReceiver receiver = messageReceiverFactory.createMessageReceiver(sender);
            TestSender testCommandSender = new TestSender(sender, receiver);
            TestCommandContextBuilder contextBuilder = new TestCommandContextBuilder(testCommandSender, command, arguments, permission, asynchronous);
            commandHandler.handle(contextBuilder);
        };
        TestCompletionExecutor completionExecutor = (sender, arguments) -> {
            MessageReceiver receiver = messageReceiverFactory.createMessageReceiver(sender);
            TestSender testCommandSender = new TestSender(sender, receiver);
            TestCompletionContextBuilder contextBuilder = new TestCompletionContextBuilder(testCommandSender, command, arguments, permission, asynchronous);
            return completionHandler.handle(contextBuilder);
        };
        register(aliases, commandExecutor, completionExecutor);
    }

    public void register(final Collection<String> aliases, final TestCommandExecutor commandExecutor, final TestCompletionExecutor completionExecutor) {
        register(aliases, Permission.empty(), commandExecutor, completionExecutor);
    }

    public void register(final Collection<String> aliases, final Permission permission, final TestCommandExecutor commandExecutor, final TestCompletionExecutor completionExecutor) {
        CommandWrapper command = new CommandWrapper(commandExecutor, completionExecutor);
        for (final String alias : aliases) {
            this.commandManager.put(alias, command);
        }
    }
}

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
import net.mcparkour.anfodis.codec.registry.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.codec.completion.CompletionCodec;
import net.mcparkour.anfodis.command.context.TestCommandContext;
import net.mcparkour.anfodis.command.context.TestCommandSender;
import net.mcparkour.anfodis.command.context.TestCompletionContext;
import net.mcparkour.anfodis.command.handler.CommandContextHandler;
import net.mcparkour.anfodis.command.handler.CompletionContextHandler;
import net.mcparkour.anfodis.command.handler.CompletionHandler;
import net.mcparkour.anfodis.command.handler.TestCommandExecutorHandler;
import net.mcparkour.anfodis.command.handler.TestCommandHandler;
import net.mcparkour.anfodis.command.mapper.TestCommand;
import net.mcparkour.anfodis.command.mapper.TestCommandMapper;
import net.mcparkour.anfodis.command.mapper.properties.TestCommandProperties;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.intext.message.MessageReceiver;
import net.mcparkour.intext.message.MessageReceiverFactory;

public class TestCommandRegistry extends AbstractCompletionRegistry<TestCommand, TestCommandContext, TestCompletionContext, net.mcparkour.anfodis.TestCommandSender> {

    private static final TestCommandMapper COMMAND_MAPPER = new TestCommandMapper();

    private final Map<String, CommandWrapper> commandManager;

    public TestCommandRegistry(
        final CodecRegistry<InjectionCodec<?>> injectionCodecRegistry,
        final CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry,
        final CodecRegistry<CompletionCodec> completionCodecRegistry,
        final MessageReceiverFactory<net.mcparkour.anfodis.TestCommandSender> messageReceiverFactory,
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
            argumentCodecRegistry,
            completionCodecRegistry,
            messageReceiverFactory,
            basePermission
        );
        this.commandManager = commandManager;
    }

    @Override
    public void register(final TestCommand command, final CommandContextHandler<TestCommandContext> commandHandler, final CompletionContextHandler<TestCompletionContext> completionHandler) {
        TestCommandProperties properties = command.getProperties();
        Set<String> names = properties.getAllNames();
        Permission basePermission = getBasePermission();
        Permission commandPermission = properties.getPermission();
        Permission permission = commandPermission.withFirst(basePermission);
        boolean asynchronous = properties.isAsynchronous();
        if (asynchronous) {
            throw new RuntimeException("Asynchronous command execution is not supported");
        }
        register(names, permission, false, commandHandler, completionHandler);
    }

    private void register(final Collection<String> aliases, final Permission permission, final boolean asynchronous, final CommandContextHandler<TestCommandContext> commandHandler, final CompletionContextHandler<TestCompletionContext> completionHandler) {
        MessageReceiverFactory<net.mcparkour.anfodis.TestCommandSender> messageReceiverFactory = getMessageReceiverFactory();
        TestCommandExecutor commandExecutor = (sender, arguments) -> {
            MessageReceiver receiver = messageReceiverFactory.createMessageReceiver(sender);
            TestCommandSender testCommandSender = new TestCommandSender(sender, receiver);
            TestCommandContext context = new TestCommandContext(testCommandSender, arguments, permission, asynchronous);
            commandHandler.handle(context);
        };
        TestCompletionExecutor completionExecutor = (sender, arguments) -> {
            MessageReceiver receiver = messageReceiverFactory.createMessageReceiver(sender);
            TestCommandSender testCommandSender = new TestCommandSender(sender, receiver);
            TestCompletionContext context = new TestCompletionContext(testCommandSender, arguments, permission, asynchronous);
            return completionHandler.handle(context);
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

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

package net.mcparkour.anfodis.command.handler;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.mcparkour.anfodis.codec.registry.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.context.CommandContext;
import net.mcparkour.anfodis.command.context.CommandSender;
import net.mcparkour.anfodis.command.mapper.Command;
import net.mcparkour.anfodis.command.mapper.argument.Argument;
import net.mcparkour.anfodis.command.mapper.context.Context;
import net.mcparkour.anfodis.handler.RootHandler;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.intext.message.MessageReceiver;
import org.jetbrains.annotations.Nullable;

public class CommandExecutorHandler<T extends Command<T, ?, ?, ?>, C extends CommandContext<?>> extends RootHandler<T, C> {

    private CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry;

    public CommandExecutorHandler(final T root, final CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, final CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry) {
        super(root, injectionCodecRegistry);
        this.argumentCodecRegistry = argumentCodecRegistry;
    }

    @Override
    public void handle(final C context, final Object instance) {
        setArguments(context, instance);
        setContext(context, instance);
        super.handle(context, instance);
    }

    private void setArguments(final C context, final Object commandInstance) {
        T command = getRoot();
        List<? extends Argument> commandArguments = command.getArguments();
        int commandArgumentsSize = commandArguments.size();
        List<String> arguments = context.getArguments();
        int argumentsSize = arguments.size();
        for (int index = 0; index < commandArgumentsSize; index++) {
            Argument commandArgument = commandArguments.get(index);
            if (index >= argumentsSize) {
                commandArgument.setEmptyArgumentField(commandInstance);
            } else {
                Object argumentValue = getArgumentValue(arguments, commandArgument, index);
                commandArgument.setArgumentField(commandInstance, argumentValue);
            }
        }
    }

    @Nullable
    private Object getArgumentValue(final List<String> arguments, final Argument commandArgument, final int index) {
        if (commandArgument.isList()) {
            return getListArgumentValue(arguments, commandArgument, index);
        }
        String argument = arguments.get(index);
        ArgumentCodec<?> codec = commandArgument.getCodec(this.argumentCodecRegistry);
        return codec.parse(argument);
    }

    private List<?> getListArgumentValue(final List<String> arguments, final Argument commandArgument, final int startIndex) {
        int size = arguments.size();
        ArgumentCodec<?> codec = commandArgument.getGenericTypeCodec(this.argumentCodecRegistry, 0);
        return IntStream.range(startIndex, size)
            .mapToObj(arguments::get)
            .map(codec::parse)
            .collect(Collectors.toList());
    }

    private void setContext(final C context, final Object commandInstance) {
        T command = getRoot();
        Context commandContext = command.getContext();
        List<String> arguments = context.getArguments();
        commandContext.setArgumentsField(commandInstance, arguments);
        Permission permission = context.getPermission();
        commandContext.setRequiredPermissionField(commandInstance, permission);
        CommandSender<?> sender = context.getSender();
        Object rawSender = sender.getSender();
        commandContext.setSenderField(commandInstance, rawSender);
        MessageReceiver receiver = sender.getReceiver();
        commandContext.setReceiverField(commandInstance, receiver);
    }
}

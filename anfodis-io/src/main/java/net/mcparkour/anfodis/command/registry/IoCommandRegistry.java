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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;

import net.mcparkour.anfodis.codec.context.TransformCodec;
import net.mcparkour.anfodis.codec.registry.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.IoMessenger;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.context.IoCommandContext;
import net.mcparkour.anfodis.command.context.IoCommandContextBuilder;
import net.mcparkour.anfodis.command.handler.CommandContextBuilderHandler;
import net.mcparkour.anfodis.command.handler.CommandExecutorHandler;
import net.mcparkour.anfodis.command.handler.IoCommandHandler;
import net.mcparkour.anfodis.command.mapper.IoCommand;
import net.mcparkour.anfodis.command.mapper.IoCommandMapper;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.intext.message.MessageReceiverFactory;

public class IoCommandRegistry
    extends AbstractCommandRegistry<IoCommand, IoCommandContext, IoCommandContextBuilder, OutputStreamWriter, IoMessenger> {

    private static final IoCommandMapper COMMAND_MAPPER = new IoCommandMapper();

    private final Scanner input;
    private final OutputStreamWriter output;
    private final CommandMap commandMap;

    public IoCommandRegistry(
        final InputStream input,
        final OutputStream output,
        final CodecRegistry<InjectionCodec<?>> injectionCodecRegistry,
        final CodecRegistry<TransformCodec<IoCommandContext, ?>> transformCodecRegistry,
        final CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry,
        final MessageReceiverFactory<OutputStreamWriter> messageReceiverFactory,
        final IoMessenger messenger
    ) {
        super(
            COMMAND_MAPPER,
            IoCommandHandler::new,
            CommandExecutorHandler::new,
            IoCommandContext::new,
            injectionCodecRegistry,
            transformCodecRegistry,
            argumentCodecRegistry,
            messageReceiverFactory,
            messenger,
            Permission.empty()
        );
        this.input = new Scanner(input);
        this.output = new OutputStreamWriter(output);
        this.commandMap = new CommandMap();
    }

    @Override
    public void register(
        final IoCommand command,
        final CommandContextBuilderHandler<IoCommandContextBuilder, IoCommandContext> commandHandler
    ) {
        var entry = new CommandMapEntry(command, commandHandler);
        this.commandMap.register(entry);
    }

    public IoCommandExecutor createExecutor() {
        return new IoCommandExecutor(
            this.input, this.output, this.getMessageReceiverFactory(), this.commandMap
        );
    }
}

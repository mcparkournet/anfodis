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

import net.mcparkour.anfodis.command.context.IoCommandContextBuilder;
import net.mcparkour.anfodis.command.context.IoSender;
import net.mcparkour.anfodis.command.lexer.Lexer;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.intext.message.MessageReceiver;
import net.mcparkour.intext.message.MessageReceiverFactory;

import java.io.OutputStreamWriter;
import java.util.Scanner;

public final class IoCommandExecutor {

    private static final Lexer LEXER = new Lexer();

    private final Scanner input;
    private final OutputStreamWriter output;
    private final MessageReceiverFactory<OutputStreamWriter> receiverFactory;
    private final CommandMap commands;

    IoCommandExecutor(
        final Scanner input,
        final OutputStreamWriter output,
        final MessageReceiverFactory<OutputStreamWriter> receiverFactory,
        final CommandMap commands) {
        this.input = input;
        this.output = output;
        this.receiverFactory = receiverFactory;
        this.commands = commands;
    }

    public void execute() {
        var line = this.input.nextLine();
        var lineTokens = LEXER.tokenize(line);

        if (lineTokens.isEmpty()) {
            return;
        }
        var arguments = lineTokens.subList(1, lineTokens.size());
        var commandNameToken = lineTokens.get(0);
        var commandName = commandNameToken.getString();
        var commandOptional = this.commands.getCommand(commandName);
        if (commandOptional.isEmpty()) {
            return;
        }
        var commandEntry = commandOptional.get();
        var command = commandEntry.getCommand();
        var handler = commandEntry.getHandler();

        MessageReceiver receiver = this.receiverFactory.createMessageReceiver(this.output);
        IoSender cliSender = new IoSender(this.output, receiver);
        IoCommandContextBuilder contextBuilder =
            new IoCommandContextBuilder(cliSender, command, arguments, Permission.empty(), false);
        handler.handle(contextBuilder);
    }
}

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

package net.mcparkour.anfodis;

import net.mcparkour.anfodis.annotation.executor.Executor;
import net.mcparkour.anfodis.command.annotation.argument.Argument;
import net.mcparkour.anfodis.command.annotation.argument.ArgumentPermission;
import net.mcparkour.anfodis.command.annotation.argument.Completion;
import net.mcparkour.anfodis.command.annotation.argument.CompletionCodec;
import net.mcparkour.anfodis.command.annotation.argument.Optional;
import net.mcparkour.anfodis.command.annotation.context.Receiver;
import net.mcparkour.anfodis.command.annotation.properties.Command;
import net.mcparkour.anfodis.command.annotation.properties.Permission;
import net.mcparkour.intext.message.MessageReceiver;

@Command("argumentPermission")
@Permission
public class TestArgumentPermissionCommand {

    @Receiver
    private MessageReceiver receiver;

    @Argument
    @ArgumentPermission
    @Completion
    @CompletionCodec(ArgsCompletionCodec.class)
    @Optional
    private String string1;

    @Argument
    @ArgumentPermission
    @Completion
    @CompletionCodec(ArgsCompletionCodec.class)
    @Optional
    private String string2;

    @Executor
    public void execute() {
        this.receiver.receivePlain(this.string1);
        this.receiver.receivePlain(this.string2);
    }
}

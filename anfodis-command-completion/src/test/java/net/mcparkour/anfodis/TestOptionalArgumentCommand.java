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

import net.mcparkour.anfodis.CommandCompletionTest.NullArgumentCodec;
import net.mcparkour.anfodis.annotation.executor.Executor;
import net.mcparkour.anfodis.annotation.transform.Transform;
import net.mcparkour.anfodis.command.annotation.argument.Argument;
import net.mcparkour.anfodis.command.annotation.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.annotation.argument.Optional;
import net.mcparkour.anfodis.command.annotation.properties.Command;
import net.mcparkour.anfodis.command.argument.OptionalArgument;
import net.mcparkour.intext.message.MessageReceiver;

@Command("optional")
public class TestOptionalArgumentCommand {

    @Transform
    private MessageReceiver receiver;

    @Argument
    @ArgumentCodec(NullArgumentCodec.class)
    private String arg1;
    @Argument
    private OptionalArgument<String> arg2;
    @Argument
    @Optional
    private OptionalArgument<String> arg3;

    @Executor
    public void execute() {
        this.receiver.receivePlain(this.arg1);
        this.receiver.receivePlain(String.valueOf(this.arg2.isPresent()));
        this.receiver.receivePlain(this.arg2.orElse("empty"));
        this.receiver.receivePlain(String.valueOf(this.arg3.isPresent()));
        this.receiver.receivePlain(this.arg3.orElse("empty"));
    }
}

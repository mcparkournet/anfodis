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

import java.util.List;
import java.util.stream.Collectors;
import net.mcparkour.anfodis.annotation.executor.Executor;
import net.mcparkour.anfodis.annotation.transform.Transform;
import net.mcparkour.anfodis.command.annotation.argument.Argument;
import net.mcparkour.anfodis.command.annotation.argument.Completion;
import net.mcparkour.anfodis.command.annotation.argument.CompletionCodec;
import net.mcparkour.anfodis.command.annotation.argument.Optional;
import net.mcparkour.anfodis.command.annotation.argument.Variadic;
import net.mcparkour.anfodis.command.annotation.properties.Command;
import net.mcparkour.anfodis.command.argument.OptionalArgument;
import net.mcparkour.anfodis.command.argument.VariadicArgument;
import net.mcparkour.intext.message.MessageReceiver;

@Command("optionalVariadic")
public class TestOptionalVariadicCommand {

    @Transform
    private MessageReceiver receiver;

    @Argument
    @Optional
    @Variadic
    @Completion
    @CompletionCodec(ArgsCompletionCodec.class)
    private OptionalArgument<VariadicArgument<Integer>> args;

    @Executor
    public void execute() {
        this.receiver.receivePlain(String.valueOf(this.args.isPresent()));
        List<String> list = this.args.orElse(VariadicArgument.of())
            .stream()
            .map(Object::toString)
            .collect(Collectors.toUnmodifiableList());
        this.receiver.receivePlain(list);
    }
}

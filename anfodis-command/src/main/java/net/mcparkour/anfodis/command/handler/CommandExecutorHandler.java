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
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.mcparkour.anfodis.codec.context.TransformCodec;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.codec.registry.CodecRegistry;
import net.mcparkour.anfodis.command.argument.ArgumentContext;
import net.mcparkour.anfodis.command.argument.VariadicArgument;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.codec.argument.result.ErrorResult;
import net.mcparkour.anfodis.command.codec.argument.result.OkResult;
import net.mcparkour.anfodis.command.codec.argument.result.Result;
import net.mcparkour.anfodis.command.context.CommandContext;
import net.mcparkour.anfodis.command.lexer.Token;
import net.mcparkour.anfodis.command.mapper.Command;
import net.mcparkour.anfodis.command.mapper.argument.Argument;
import net.mcparkour.anfodis.handler.RootHandler;

public class CommandExecutorHandler<T extends Command<T, ?, ?, C, S>, C extends CommandContext<T, S>, S>
    extends RootHandler<T, C> {

    private final CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry;

    public CommandExecutorHandler(
        final T root,
        final CodecRegistry<InjectionCodec<?>> injectionCodecRegistry,
        final CodecRegistry<TransformCodec<C, ?>> transformCodecRegistry,
        final CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry
    ) {
        super(root, injectionCodecRegistry, transformCodecRegistry);
        this.argumentCodecRegistry = argumentCodecRegistry;
    }

    @Override
    public void handle(final C context, final Object instance) {
        try {
            setArguments(context, instance);
            super.handle(context, instance);
        } catch (final ArgumentException exception) {
            exception.runResult();
        }
    }

    private void setArguments(final C context, final Object commandInstance) {
        T command = getRoot();
        List<? extends Argument> commandArguments = command.getArguments();
        int commandArgumentsSize = commandArguments.size();
        List<Token> arguments = context.getArguments();
        int argumentsSize = arguments.size();
        for (int index = 0; index < commandArgumentsSize; index++) {
            Argument commandArgument = commandArguments.get(index);
            if (index >= argumentsSize) {
                commandArgument.setEmptyArgumentField(commandInstance);
            } else {
                Object argumentValue = getArgumentValue(arguments, commandArgument, index, context);
                commandArgument.setArgumentField(commandInstance, argumentValue);
            }
        }
    }

    private Object getArgumentValue(
        final List<Token> arguments,
        final Argument commandArgument,
        final int index,
        final C context
    ) {
        if (commandArgument.isVariadicArgument()) {
            return getVariadicArgumentValue(arguments, commandArgument, index, context);
        }
        ArgumentContext argumentContext = commandArgument.getContext();
        Token token = arguments.get(index);
        String argumentValue = token.getString();
        ArgumentCodec<?> codec = commandArgument.getCodec(this.argumentCodecRegistry);
        Result<?> result = codec.parse(context, argumentContext, argumentValue);
        return getResult(result);
    }

    private VariadicArgument<?> getVariadicArgumentValue(
        final List<Token> arguments,
        final Argument commandArgument,
        final int startIndex,
        final C context
    ) {
        int size = arguments.size();
        ArgumentCodec<?> codec = commandArgument.getGenericTypeCodec(this.argumentCodecRegistry, 0);
        ArgumentContext argumentContext = commandArgument.getContext();
        List<?> argumentsList = IntStream.range(startIndex, size)
            .mapToObj(arguments::get)
            .map(Token::getString)
            .map(argumentValue -> codec.parse(context, argumentContext, argumentValue))
            .map(this::getResult)
            .collect(Collectors.toUnmodifiableList());
        return VariadicArgument.of(argumentsList);
    }

    private <U> U getResult(final Result<U> result) {
        Optional<ErrorResult> optionalErrorResult = result.getErrorResult();
        if (optionalErrorResult.isPresent()) {
            ErrorResult errorResult = optionalErrorResult.get();
            throw new ArgumentException(errorResult);
        }
        Optional<OkResult<U>> optionalOkResult = result.getOkResult();
        if (optionalOkResult.isEmpty()) {
            throw new RuntimeException("Result does not have OkResult");
        }
        OkResult<U> okResult = optionalOkResult.get();
        return okResult.getResult();
    }
}

package net.mcparkour.anfodis.command.codec.completion;

import java.util.Collection;
import java.util.List;
import net.mcparkour.anfodis.codec.Codec;
import net.mcparkour.anfodis.command.argument.ArgumentContext;
import net.mcparkour.anfodis.command.handler.CompletionContext;

public interface CompletionCodec extends Codec {

    List<String> getCompletions(CompletionContext<?> completionContext, ArgumentContext argumentContext);

    static CompletionCodec entries(final String... completions) {
        return (context, argumentContext) -> List.of(completions);
    }

    static CompletionCodec entries(final Collection<String> completions) {
        return (context, argumentContext) -> List.copyOf(completions);
    }
}

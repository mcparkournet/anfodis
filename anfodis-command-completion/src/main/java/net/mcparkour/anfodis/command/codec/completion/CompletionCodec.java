package net.mcparkour.anfodis.command.codec.completion;

import java.util.Collection;
import java.util.List;
import net.mcparkour.anfodis.codec.Codec;
import net.mcparkour.anfodis.command.handler.CompletionContext;

public interface CompletionCodec extends Codec {

    List<String> getCompletions(CompletionContext<?> context);

    static CompletionCodec entries(final String... completions) {
        return context -> List.of(completions);
    }

    static CompletionCodec entries(final Collection<String> completions) {
        return context -> List.copyOf(completions);
    }
}

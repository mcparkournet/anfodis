package net.mcparkour.anfodis.command.codec.completion;

import java.util.Collection;
import java.util.List;
import net.mcparkour.anfodis.command.handler.CompletionContext;

@FunctionalInterface
public interface CompletionCodec {

    List<String> getCompletions(CompletionContext<?> context);

    static CompletionCodec entries(final String... completions) {
        return context -> List.of(completions);
    }

    static CompletionCodec entries(final Collection<String> completions) {
        return context -> List.copyOf(completions);
    }
}

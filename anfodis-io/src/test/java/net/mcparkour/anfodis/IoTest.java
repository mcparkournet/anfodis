package net.mcparkour.anfodis;

import net.mcparkour.anfodis.codec.context.TransformCodec;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.codec.registry.CodecRegistry;
import net.mcparkour.anfodis.command.IoMessenger;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.context.IoCommandContext;
import net.mcparkour.anfodis.command.registry.IoCommandRegistry;
import net.mcparkour.intext.message.MessageReceiver;
import net.mcparkour.intext.translation.Translation;
import net.mcparkour.intext.translation.Translations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Map;

public class IoTest {

    @Test
    public void testIo() {
        var input = new TestInputStream();
        var output = new TestOutputStream();

        var injectionCodecRegistry = CodecRegistry.<InjectionCodec<?>>builder().build();
        var transformCodecRegistry = CodecRegistry.<TransformCodec<IoCommandContext, ?>>builder()
            .typed(MessageReceiver.class, context -> context.getSender().getReceiver())
            .build();
        var argumentCodecRegistry = CodecRegistry.<ArgumentCodec<?>>builder()
            .typed(String.class, ArgumentCodec.identity())
            .build();
        var translationMap = Map.<String, Translation>of();
        Translations translations = new Translations(Locale.US, translationMap);
        var messageReceiverFactory =
            new IoMessageReceiverFactory(translations, (receiver) -> Locale.US);
        var messenger = new IoMessenger() {
        };
        var registry = new IoCommandRegistry(
            input, output, injectionCodecRegistry, transformCodecRegistry, argumentCodecRegistry,
            messageReceiverFactory, messenger
        );
        registry.register(FooCommand.class);
        var executor = registry.createExecutor();
        input.pushLine("foo ping");
        executor.execute();
        var line = output.pop();
        Assertions.assertEquals("`ping` & pong!\n", line);
    }
}

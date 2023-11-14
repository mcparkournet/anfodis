package net.mcparkour.anfodis;

import net.kyori.text.Component;
import net.mcparkour.intext.message.AbstractMessageReceiver;
import net.mcparkour.intext.translation.Translations;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Locale;

public class IoMessageReceiver extends AbstractMessageReceiver {

    private final OutputStreamWriter writer;

    public IoMessageReceiver(final OutputStreamWriter output, final Translations translations, final Locale language) {
        super(translations, language);
        this.writer = output;
    }

    @Override
    public void receiveComponent(final Component component) {
        throw new UnsupportedOperationException("Unimplemented");
    }

    @Override
    public void receiveSerializedComponent(final String serializedComponent) {
        throw new UnsupportedOperationException("Unimplemented");

    }

    @Override
    public void receivePlainColorized(final String message) {
        try {
            this.writer.write(message);
            this.writer.flush();
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public void receivePlain(final String message) {
        try {
            this.writer.write(message);
            this.writer.flush();
        } catch (final IOException exception) {
            throw new RuntimeException(exception);
        }
    }
}

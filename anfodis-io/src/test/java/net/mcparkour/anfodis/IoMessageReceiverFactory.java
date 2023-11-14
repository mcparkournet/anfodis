package net.mcparkour.anfodis;

import net.mcparkour.intext.message.AbstractMessageReceiverFactory;
import net.mcparkour.intext.message.LanguageProvider;
import net.mcparkour.intext.message.MessageReceiver;
import net.mcparkour.intext.translation.Translations;

import java.io.OutputStreamWriter;
import java.util.Locale;

public class IoMessageReceiverFactory extends AbstractMessageReceiverFactory<OutputStreamWriter> {

    public IoMessageReceiverFactory(final Translations translations, final LanguageProvider<OutputStreamWriter> provider) {
        super(translations, provider);
    }

    @Override
    public MessageReceiver createMessageReceiver(final OutputStreamWriter receiver, final Locale language) {
        Translations translations = getTranslations();
        return new IoMessageReceiver(receiver, translations, language);
    }

}



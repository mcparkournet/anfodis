package net.mcparkour.anfodis.command.handler;

import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.mapper.JDACommand;
import net.mcparkour.intext.translation.Translations;

public class JDACommandHandler extends CommandHandler<JDACommand> {

	public JDACommandHandler(JDACommand command, JDACommandContext context, Translations translations, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry) {
		super(command, context, translations, injectionCodecRegistry, argumentCodecRegistry, new JDACommandExecutorHandler(command, context, translations, injectionCodecRegistry, argumentCodecRegistry));
	}
}

package net.mcparkour.anfodis.command.handler;

import java.util.Map;
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.mapper.JDACommand;
import net.mcparkour.anfodis.handler.ContextHandler;
import net.mcparkour.intext.translation.Translations;

public class JDACommandHandler extends CommandHandler<JDACommand, JDACommandContext> {

	public JDACommandHandler(JDACommand command, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, Translations translations, Map<JDACommand, ? extends ContextHandler<JDACommandContext>> subCommandHandlers) {
		super(command, injectionCodecRegistry, argumentCodecRegistry, translations, subCommandHandlers, new JDACommandExecutorHandler(command,injectionCodecRegistry,argumentCodecRegistry, translations));
	}
}

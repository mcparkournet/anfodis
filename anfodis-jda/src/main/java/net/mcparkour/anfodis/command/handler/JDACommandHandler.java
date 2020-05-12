package net.mcparkour.anfodis.command.handler;

import java.util.Map;
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.context.JDACommandContext;
import net.mcparkour.anfodis.command.mapper.JDACommand;
import net.mcparkour.anfodis.handler.ContextHandler;

public class JDACommandHandler extends CommandHandler<JDACommand, JDACommandContext> {

	public JDACommandHandler(JDACommand command, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, Map<JDACommand, ? extends ContextHandler<JDACommandContext>> subCommandHandlers) {
		super(command, injectionCodecRegistry, argumentCodecRegistry, subCommandHandlers);
	}
}

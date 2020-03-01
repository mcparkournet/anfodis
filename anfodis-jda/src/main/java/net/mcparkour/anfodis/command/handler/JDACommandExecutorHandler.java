package net.mcparkour.anfodis.command.handler;

import net.dv8tion.jda.api.entities.PrivateChannel;
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.mapper.JDACommand;
import net.mcparkour.anfodis.command.mapper.context.JDAContext;
import net.mcparkour.intext.translation.Translations;

public class JDACommandExecutorHandler extends CommandExecutorHandler<JDACommand, JDACommandContext> {

	public JDACommandExecutorHandler(JDACommand command, JDACommandContext context, Translations translations, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry) {
		super(command, context, translations, injectionCodecRegistry, argumentCodecRegistry);
	}

	@Override
	public void handle() {
		setChannel();
		super.handle();
	}

	private void setChannel() {
		JDACommand command = getCommand();
		JDAContext commandContext = command.getContext();
		Object commandInstance = getCommandInstance();
		JDACommandContext context = getContext();
		PrivateChannel channel = context.getChannel();
		commandContext.setChannelField(commandInstance, channel);
	}
}

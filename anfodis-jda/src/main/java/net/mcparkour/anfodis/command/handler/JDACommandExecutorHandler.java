package net.mcparkour.anfodis.command.handler;

import net.dv8tion.jda.api.entities.PrivateChannel;
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.mapper.JDACommand;
import net.mcparkour.anfodis.command.mapper.context.JDAContext;
import net.mcparkour.intext.translation.Translations;

public class JDACommandExecutorHandler extends CommandExecutorHandler<JDACommand, JDACommandContext> {

	public JDACommandExecutorHandler(JDACommand root, CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, Translations translations) {
		super(root, injectionCodecRegistry, argumentCodecRegistry, translations);
	}

	@Override
	public void handle(JDACommandContext context, Object instance) {
		setChannel(context, instance);
		super.handle(context, instance);
	}

	private void setChannel(JDACommandContext context, Object commandInstance) {
		JDACommand command = getRoot();
		JDAContext commandContext = command.getContext();
		PrivateChannel channel = context.getChannel();
		commandContext.setChannelField(commandInstance, channel);
	}
}

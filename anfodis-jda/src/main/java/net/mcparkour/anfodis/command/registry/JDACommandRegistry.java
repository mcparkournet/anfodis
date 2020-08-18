/*
 * MIT License
 *
 * Copyright (c) 2020 MCParkour
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.mcparkour.anfodis.command.registry;

import net.dv8tion.jda.api.JDA;
import net.mcparkour.anfodis.codec.CodecRegistry;
import net.mcparkour.anfodis.codec.injection.InjectionCodec;
import net.mcparkour.anfodis.command.ChannelSender;
import net.mcparkour.anfodis.command.PermissionMap;
import net.mcparkour.anfodis.command.codec.argument.ArgumentCodec;
import net.mcparkour.anfodis.command.context.JDACommandContext;
import net.mcparkour.anfodis.command.handler.CommandContextHandler;
import net.mcparkour.anfodis.command.handler.CommandExecutorHandler;
import net.mcparkour.anfodis.command.handler.CommandHandler;
import net.mcparkour.anfodis.command.mapper.JDACommand;
import net.mcparkour.anfodis.command.mapper.JDACommandMapper;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.intext.message.MessageReceiverFactory;

public class JDACommandRegistry extends AbstractCommandRegistry<JDACommand, JDACommandContext, ChannelSender> {

    private static final JDACommandMapper COMMAND_MAPPER = new JDACommandMapper();

    private JDA jda;
    private PermissionMap permissionMap;
    private CommandMap commandMap;

    public JDACommandRegistry(final CodecRegistry<InjectionCodec<?>> injectionCodecRegistry, final CodecRegistry<ArgumentCodec<?>> argumentCodecRegistry, final MessageReceiverFactory<ChannelSender> messageReceiverFactory, final Permission basePermission, final JDA jda, final PermissionMap permissionMap) {
        super(COMMAND_MAPPER, CommandHandler::new, CommandExecutorHandler::new, JDACommandContext::new, injectionCodecRegistry, argumentCodecRegistry, messageReceiverFactory, basePermission);
        this.jda = jda;
        this.permissionMap = permissionMap;
        this.commandMap = new CommandMap();
        registerCommandListener();
    }

    private void registerCommandListener() {
        Permission basePermission = getBasePermission();
        MessageReceiverFactory<ChannelSender> messageReceiverFactory = getMessageReceiverFactory();
        PrivateMessageReceivedListener listener = new PrivateMessageReceivedListener(basePermission, this.permissionMap, this.commandMap, messageReceiverFactory);
        this.jda.addEventListener(listener);
    }

    @Override
    public void register(final JDACommand command, final CommandContextHandler<JDACommandContext> commandHandler) {
        this.commandMap.register(command, commandHandler);
    }
}

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

import java.util.List;
import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.mcparkour.anfodis.command.ChannelSender;
import net.mcparkour.anfodis.command.JDAChannelSender;
import net.mcparkour.anfodis.command.PermissionMap;
import net.mcparkour.anfodis.command.context.JDACommandContext;
import net.mcparkour.anfodis.command.context.JDACommandSender;
import net.mcparkour.anfodis.command.handler.CommandContextHandler;
import net.mcparkour.anfodis.command.lexer.Lexer;
import net.mcparkour.anfodis.command.lexer.Token;
import net.mcparkour.anfodis.command.mapper.JDACommand;
import net.mcparkour.anfodis.command.mapper.properties.JDACommandProperties;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.intext.message.MessageReceiver;
import net.mcparkour.intext.message.MessageReceiverFactory;

public class PrivateMessageReceivedListener implements EventListener {

    private static final Lexer LEXER = new Lexer();

    private final Permission basePermission;
    private final PermissionMap permissionMap;
    private final CommandMap commandMap;
    private final MessageReceiverFactory<ChannelSender> messageReceiverFactory;

    public PrivateMessageReceivedListener(final Permission basePermission, final PermissionMap permissionMap, final CommandMap commandMap, final MessageReceiverFactory<ChannelSender> messageReceiverFactory) {
        this.basePermission = basePermission;
        this.permissionMap = permissionMap;
        this.commandMap = commandMap;
        this.messageReceiverFactory = messageReceiverFactory;
    }

    @Override
    public void onEvent(@Nonnull final GenericEvent event) {
        if (event instanceof PrivateMessageReceivedEvent) {
            PrivateMessageReceivedEvent privateMessageReceivedEvent = (PrivateMessageReceivedEvent) event;
            onPrivateMessageReceivedEvent(privateMessageReceivedEvent);
        }
    }

    private void onPrivateMessageReceivedEvent(final PrivateMessageReceivedEvent event) {
        Message message = event.getMessage();
        String rawMessage = message.getContentRaw();
        if (rawMessage.isEmpty() || rawMessage.charAt(0) != '/') {
            return;
        }
        List<Token> tokens = LEXER.tokenize(rawMessage);
        Token nameToken = tokens.get(0);
        String name = nameToken.getString();
        String nameWithoutSlash = name.substring(1);
        CommandMapEntry entry = this.commandMap.getCommand(nameWithoutSlash);
        if (entry == null) {
            return;
        }
        CommandContextHandler<JDACommandContext> handler = entry.getHandler();
        int size = tokens.size();
        List<Token> arguments = tokens.subList(1, size);
        JDACommand command = entry.getCommand();
        JDACommandContext context = createContext(event, arguments, command);
        handler.handle(context);
    }

    private JDACommandContext createContext(final PrivateMessageReceivedEvent event, final List<Token> arguments, final JDACommand command) {
        User sender = event.getAuthor();
        PrivateChannel channel = event.getChannel();
        ChannelSender channelSender = new JDAChannelSender(sender, channel);
        MessageReceiver receiver = this.messageReceiverFactory.createMessageReceiver(channelSender);
        JDACommandSender commandSender = new JDACommandSender(channelSender, receiver, this.permissionMap);
        JDACommandProperties properties = command.getProperties();
        Permission commandPermission = properties.getPermission();
        Permission permission = commandPermission.withFirst(this.basePermission);
        boolean asynchronous = properties.isAsynchronous();
        return new JDACommandContext(commandSender, arguments, permission, asynchronous);
    }
}

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

package net.mcparkour.anfodis.command;

import java.util.List;
import java.util.stream.Collectors;
import net.mcparkour.anfodis.command.context.Sender;
import net.mcparkour.anfodis.command.context.Permissible;
import net.mcparkour.anfodis.command.mapper.Command;
import net.mcparkour.anfodis.command.mapper.properties.CommandProperties;
import net.mcparkour.craftmon.permission.Permission;
import net.mcparkour.intext.message.MessageReceiver;

public interface Messenger<T extends Command<T, ?, ?, ?>, S> {

    default void sendNoPermissionMessage(final Sender<S> sender, final Permission permission) {
        MessageReceiver receiver = sender.getReceiver();
        String permissionName = permission.getName();
        receiver.receivePlain("You do not have permission: " + permissionName);
    }

    default void sendCommandUsageMessage(final Sender<S> sender, final T command) {
        String usage = command.getUsage();
        MessageReceiver receiver = sender.getReceiver();
        receiver.receivePlain(usage);
    }

    default void sendSubCommandsUsageMessage(final Sender<S> sender, final Permission permission, final T command) {
        String usage = getUsage(command, sender, permission);
        MessageReceiver receiver = sender.getReceiver();
        receiver.receivePlain(usage);
    }

    private String getUsage(final T command, final Permissible permissible, final Permission contextPermission) {
        String header = command.getUsageHeader();
        String subCommandsUsage = getSubCommandsUsage(command, permissible, contextPermission);
        return header + '\n' + subCommandsUsage;
    }

    private String getSubCommandsUsage(final T command, final Permissible permissible, final Permission contextPermission) {
        List<T> subCommands = command.getSubCommands();
        return subCommands.stream()
            .filter(subCommand -> {
                CommandProperties subCommandProperties = subCommand.getProperties();
                Permission subCommandPermission = subCommandProperties.getPermission();
                Permission permission = contextPermission.withLast(subCommandPermission);
                return permissible.hasPermission(permission);
            })
            .map(T::getUsage)
            .collect(Collectors.joining("\n"));
    }
}

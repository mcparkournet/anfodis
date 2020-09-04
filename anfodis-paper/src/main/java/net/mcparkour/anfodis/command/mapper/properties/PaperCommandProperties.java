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

package net.mcparkour.anfodis.command.mapper.properties;

import java.util.Set;
import net.mcparkour.anfodis.command.context.CommandSender;

public class PaperCommandProperties extends CommandProperties {

    private final Set<Class<? extends org.bukkit.command.CommandSender>> senderTypes;

    public PaperCommandProperties(final PaperCommandPropertiesData propertiesData) {
        super(propertiesData);
        Class<? extends org.bukkit.command.CommandSender>[] senderTypes = propertiesData.getSenderTypes();
        this.senderTypes = senderTypes == null ?
            Set.of() :
            Set.of(senderTypes);
    }

    public boolean isValidSender(final CommandSender<org.bukkit.command.CommandSender> commandSender) {
        if (this.senderTypes.isEmpty()) {
            return true;
        }
        org.bukkit.command.CommandSender bukkitSender = commandSender.getSender();
        Class<?> bukkitSenderClass = bukkitSender.getClass();
        return this.senderTypes.stream()
            .anyMatch(sender -> sender.isAssignableFrom(bukkitSenderClass));
    }

    public Set<Class<? extends org.bukkit.command.CommandSender>> getSenderTypes() {
        return this.senderTypes;
    }
}

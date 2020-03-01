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

package net.mcparkour.anfodis.command.handler;

import java.util.List;
import java.util.Map;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.mcparkour.craftmon.permission.Permission;

public class JDACommandSender implements CommandSender {

	private User sender;
	private PrivateChannel channel;
	private Map<Long, List<Permission>> permissions;

	public JDACommandSender(User sender, PrivateChannel channel, Map<Long, List<Permission>> permissions) {
		this.sender = sender;
		this.channel = channel;
		this.permissions = permissions;
	}

	@Override
	public void sendMessage(String message) {
		this.channel.sendMessage(message).queue();
	}

	@Override
	public boolean hasPermission(Permission permission) {
		String name = permission.getName();
		return hasPermission(name);
	}

	@Override
	public boolean hasPermission(String name) {
		long id = this.sender.getIdLong();
		List<Permission> permissions = this.permissions.get(id);
		if (permissions == null) {
			return false;
		}
		return permissions.stream()
			.map(Permission::getName)
			.anyMatch(permissionName -> permissionName.equals(name));
	}

	@Override
	public Object getRawSender() {
		return this.sender;
	}
}

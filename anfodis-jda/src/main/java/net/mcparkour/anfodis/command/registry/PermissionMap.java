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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.dv8tion.jda.api.entities.User;
import net.mcparkour.craftmon.permission.Permission;
import org.jetbrains.annotations.Nullable;

public class PermissionMap {

	private Map<Long, List<Permission>> permissionMap;

	public PermissionMap() {
		this.permissionMap = new HashMap<>(4);
	}

	public void addPermission(User user, Permission permission) {
		List<Permission> currentPermissions = getCurrentPermissions(user);
		currentPermissions.add(permission);
	}

	public void addPermissions(User user, List<Permission> permissions) {
		List<Permission> currentPermissions = getCurrentPermissions(user);
		currentPermissions.addAll(permissions);
	}

	private List<Permission> getCurrentPermissions(User user) {
		long id = user.getIdLong();
		return this.permissionMap.computeIfAbsent(id, key -> new ArrayList<>(4));
	}

	@Nullable
	public List<Permission> getPermissions(User user) {
		long id = user.getIdLong();
		return getPermissions(id);
	}

	@Nullable
	public List<Permission> getPermissions(long userId) {
		List<Permission> permissions = this.permissionMap.get(userId);
		if (permissions == null) {
			return null;
		}
		return List.copyOf(permissions);
	}
}

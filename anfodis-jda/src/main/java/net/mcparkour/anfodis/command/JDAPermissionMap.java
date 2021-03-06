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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.dv8tion.jda.api.entities.User;
import net.mcparkour.craftmon.permission.Permission;

public class JDAPermissionMap implements PermissionMap {

    private final Map<Long, List<Permission>> map;

    public JDAPermissionMap() {
        this.map = new HashMap<>(4);
    }

    @Override
    public void addPermission(final User user, final Permission permission) {
        List<Permission> currentPermissions = getCurrentPermissions(user);
        currentPermissions.add(permission);
    }

    @Override
    public void addPermissions(final User user, final List<Permission> permissions) {
        List<Permission> currentPermissions = getCurrentPermissions(user);
        currentPermissions.addAll(permissions);
    }

    private List<Permission> getCurrentPermissions(final User user) {
        long id = user.getIdLong();
        return this.map.computeIfAbsent(id, key -> new ArrayList<>(4));
    }

    @Override
    public List<Permission> getPermissions(final User user) {
        long id = user.getIdLong();
        List<Permission> permissions = this.map.get(id);
        if (permissions == null) {
            return List.of();
        }
        return List.copyOf(permissions);
    }
}

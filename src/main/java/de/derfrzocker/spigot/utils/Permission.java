package de.derfrzocker.spigot.utils;

import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Permission {

    private final static Map<Plugin, Set<Permission>> PERMISSIONS = new HashMap<>();

    private final Permission parent;

    private final String permission;

    private final Plugin plugin;

    private final boolean commandPermission;

    public Permission(final Permission parent, final String permission, final Plugin plugin, final boolean commandPermission) {
        this.parent = parent;
        this.permission = permission;
        this.plugin = plugin;
        this.commandPermission = commandPermission;

        PERMISSIONS.computeIfAbsent(plugin, plugin1 -> new HashSet<>()).add(this);
    }

    public String getPermission() {
        if (parent == null)
            return permission;

        return String.format("%s.%s", parent.getPermission(), permission);
    }

    public static boolean hasAnyCommandPermission(final Plugin plugin, final Permissible permissible) {
        return PERMISSIONS.computeIfAbsent(plugin, plugin1 -> new HashSet<>()).stream().filter(Permission::isCommandPermission).anyMatch(permission -> permissible.hasPermission(permission.getPermission()));
    }

    public boolean hasPermission(final Permissible permissible) {
        return permissible.hasPermission(getPermission());
    }

    public boolean hasPermission(final Permissible permissible, final int level) {
        int max = getPermissionLevel(permissible);

        if (max == -1)
            return true;

        return max >= level;
    }

    public int getPermissionLevel(final Permissible permissible) {
        if (permissible.isOp())
            return -1;

        int max = 0;

        final Set<PermissionAttachmentInfo> permissionAttachmentInfos = permissible.getEffectivePermissions();

        for (final PermissionAttachmentInfo permissionAttachmentInfo : permissionAttachmentInfos) {
            if (!permissionAttachmentInfo.getValue())
                continue;

            String permission = permissionAttachmentInfo.getPermission().toLowerCase();

            if (!permission.startsWith(getPermission()))
                continue;

            permission = permission.replace(getPermission(), "");

            if (permission.equalsIgnoreCase("*"))
                return -1;

            try {
                int amount = Integer.parseInt(permission);

                if (amount > max)
                    max = amount;
            } catch (NumberFormatException e) {
                plugin.getLogger().warning("Unexpected value in permission, expect a Number or '*' but got '" + permission + "'");
                plugin.getLogger().warning("Please check your permission: '" + permissionAttachmentInfo.getPermission() + "'");
            }

        }

        return max;
    }

    public boolean isCommandPermission() {
        return commandPermission;
    }
}

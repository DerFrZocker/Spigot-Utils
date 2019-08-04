package de.derfrzocker.spigot.utils;

import lombok.Data;
import lombok.NonNull;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class Permission {

    private final static Map<JavaPlugin, Set<Permission>> PERMISSIONS = new HashMap<>();

    private final Permission parent;

    @NonNull
    private final String permission;

    private final JavaPlugin javaPlugin;

    private final boolean commandPermission;

    public Permission(Permission parent, String permission, JavaPlugin javaPlugin, boolean commandPermission) {
        this.parent = parent;
        this.permission = permission;
        this.javaPlugin = javaPlugin;
        this.commandPermission = commandPermission;

        PERMISSIONS.computeIfAbsent(javaPlugin, javaPlugin1 -> new HashSet<>()).add(this);
    }

    public String getPermission() {
        if (parent == null)
            return permission;

        return String.format("%s.%s", parent.getPermission(), permission);
    }

    public boolean hasPermission(Permissible permissible) {
        return permissible.hasPermission(getPermission());
    }

    public boolean hasPermission(Permissible permissible, int level) {
        int max = getPermissionLevel(permissible);

        if (max == -1)
            return true;

        return max >= level;
    }

    public int getPermissionLevel(Permissible permissible) {
        if (permissible.isOp())
            return -1;

        int max = 0;

        Set<PermissionAttachmentInfo> permissionAttachmentInfos = permissible.getEffectivePermissions();

        for (PermissionAttachmentInfo permissionAttachmentInfo : permissionAttachmentInfos) {
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
                javaPlugin.getLogger().warning("Unexpected value in permission, expect a Number or '*' but got '" + permission + "'");
                javaPlugin.getLogger().warning("Please check your permission: '" + permissionAttachmentInfo.getPermission() + "'");
            }

        }

        return max;
    }

    public static boolean hasAnyCommandPermission(JavaPlugin javaPlugin, Permissible permissible) {
        return PERMISSIONS.computeIfAbsent(javaPlugin, javaPlugin1 -> new HashSet<>()).stream().filter(Permission::isCommandPermission).anyMatch(permission -> permissible.hasPermission(permission.getPermission()));
    }

}

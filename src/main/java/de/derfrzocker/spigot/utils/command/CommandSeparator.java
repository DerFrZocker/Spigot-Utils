package de.derfrzocker.spigot.utils.command;

import de.derfrzocker.spigot.utils.Permission;
import de.derfrzocker.spigot.utils.message.MessageKey;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A CommandSeparator handel multiple sub commands of on main command.
 */
public abstract class CommandSeparator implements TabExecutor {

    @NotNull
    private final JavaPlugin javaPlugin;

    @Nullable
    private Command2 fallBack;

    @NotNull
    private final Map<String, Command2> map = new HashMap<>();

    public CommandSeparator(@NotNull final JavaPlugin javaPlugin) {
        Validate.notNull(javaPlugin, "JavaPlugin can't be null");

        this.javaPlugin = javaPlugin;
    }

    /**
     * Registers a new TabExecutor to this CommandSeparator. <br>
     * If key is null the TabExecutor is registered as fallback command <br>
     * the fallback command is usual the help command. <br>
     * <br>
     * If a permission is specific, it automatically checks if the CommandSender have the permission <br>
     * if yes, it while pas the command to the given TabExecutor. If not, it checks if a fallback command is specified, <br>
     * if yes, it while pas the command to the fallback command. If not it returns false on the onCommand method, this <br>
     * means the default "The command was not found" messages get send to the CommandSender. <br>
     * <br>
     * If a usage and / or a description is given the command while be shout on the default {@link HelpCommand}.
     *
     * @param executor    of the command
     * @param key         the name of the command
     * @param permission  of the command
     * @param usage       of the command
     * @param description of the command
     * @throws NullPointerException if executor is null
     */
    public void registerExecutor(final @NotNull TabExecutor executor, final @Nullable String key, final @Nullable Permission permission, final @Nullable MessageKey usage, final @Nullable MessageKey description) {
        Validate.notNull(executor, "TabExecutor can't be null");

        if (key == null) {
            fallBack = new Command2(executor, permission, usage, description);
            return;
        }

        map.put(key.toLowerCase(), new Command2(executor, permission, usage, description));
    }

    @Override
    public boolean onCommand(final @NotNull CommandSender sender, final @NotNull Command command, final @NotNull String label, final @NotNull String[] args) {
        Validate.notNull(sender, "CommandSender can't be null");
        Validate.notNull(command, "Command can't be null");
        Validate.notNull(label, "Label can't be null");
        Validate.notNull(args, "Arguments can't be null");

        if (args.length == 0) {
            if (map.containsKey("")) {
                final Command2 command2 = map.get("");
                if (command2.getPermission() == null || command2.getPermission().hasPermission(sender))
                    if (command2.getTabExecutor().onCommand(sender, command, label, args))
                        return true;
            }

            if (fallBack == null)
                return false;

            if (fallBack.getPermission() == null || fallBack.getPermission().hasPermission(sender))
                return fallBack.getTabExecutor().onCommand(sender, command, label, args);

            return false;
        }

        if (map.containsKey(args[0].toLowerCase())) {
            final Command2 command2 = map.get(args[0].toLowerCase());
            if (command2.getPermission() == null || command2.getPermission().hasPermission(sender))
                if (command2.getTabExecutor().onCommand(sender, command, label, buildStrings(args)))
                    return true;
        }

        if (fallBack == null)
            return false;

        if (fallBack.getPermission() == null || fallBack.getPermission().hasPermission(sender))
            return fallBack.getTabExecutor().onCommand(sender, command, label, args);

        return false;
    }

    @Override
    @Nullable
    public List<String> onTabComplete(final @NotNull CommandSender sender, final @NotNull Command command, final @NotNull String alias, final @NotNull String[] args) {
        Validate.notNull(sender, "CommandSender can't be null");
        Validate.notNull(command, "Command can't be null");
        Validate.notNull(alias, "Alias can't be null");
        Validate.notNull(args, "Arguments can't be null");

        final List<String> list = new ArrayList<>();

        if (args.length == 1) {
            map.entrySet().stream().
                    filter(entry -> entry.getKey().startsWith(args[0].toLowerCase())).
                    filter(entry -> entry.getValue().getPermission() == null || entry.getValue().getPermission().hasPermission(sender)).forEach(entry -> list.add(entry.getKey()));

            return list;
        }

        if (args.length >= 2 && map.containsKey(args[0])) {
            final Command2 command2 = map.get(args[0].toLowerCase());

            if (command2.getPermission() == null || command2.getPermission().hasPermission(sender))
                return command2.getTabExecutor().onTabComplete(sender, command, alias, buildStrings(args));

        }

        return list;
    }

    @NotNull
    Map<String, Command2> getCommands() {
        return map;
    }

    private String[] buildStrings(final String[] args) {
        final String[] strings = new String[args.length - 1];

        if (args.length - 1 > 0)
            System.arraycopy(args, 1, strings, 0, args.length - 1);

        return strings;
    }

    final class Command2 {
        @NotNull
        private final TabExecutor tabExecutor;

        @Nullable
        private final Permission permission;

        @Nullable
        private final MessageKey usage;

        @Nullable
        private final MessageKey description;

        private Command2(@NotNull final TabExecutor tabExecutor, @Nullable final Permission permission, @Nullable final MessageKey usage, @Nullable final MessageKey description) {
            Validate.notNull(tabExecutor, "TabExecutor can't be null");

            this.tabExecutor = tabExecutor;
            this.permission = permission;
            this.usage = usage;
            this.description = description;
        }

        @NotNull
        TabExecutor getTabExecutor() {
            return tabExecutor;
        }

        @Nullable
        Permission getPermission() {
            return permission;
        }

        @Nullable
        MessageKey getUsage() {
            return usage;
        }

        @Nullable
        MessageKey getDescription() {
            return description;
        }
    }

}

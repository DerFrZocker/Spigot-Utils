package de.derfrzocker.spigot.utils;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public abstract class CommandSeparator implements TabExecutor {

    @NonNull
    private final JavaPlugin javaPlugin;

    @Setter
    private boolean help = false;

    private Command2 fallBack;

    private final Map<String, Command2> map = new HashMap<>();

    @Override
    public boolean onCommand(final @NonNull CommandSender sender, final @NonNull Command command, final @NonNull String label, final @NonNull String[] args) {

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
    public List<String> onTabComplete(final @NonNull CommandSender sender, final @NonNull Command command, final @NonNull String alias, final @NonNull String[] args) {
        final List<String> list = new ArrayList<>();

        if (args.length == 1) {
            map.entrySet().stream().
                    filter(entry -> entry.getKey().startsWith(args[0].toLowerCase())).
                    filter(entry -> !entry.getKey().equals("help")).
                    filter(entry -> entry.getValue().getPermission() == null || entry.getValue().getPermission().hasPermission(sender)).forEach(entry -> list.add(entry.getKey()));

            if (help) {
                if (map.containsKey("help") && Permission.hasAnyCommandPermission(javaPlugin, sender))
                    list.add("help");
            } else if (map.containsKey("help")) {
                final Command2 helpcommand = map.get("help");
                if (helpcommand.getPermission() == null || helpcommand.getPermission().hasPermission(sender))
                    list.add("help");
            }

            return list;
        }

        if (args.length >= 2 && map.containsKey(args[0])) {
            final Command2 command2 = map.get(args[0].toLowerCase());

            if (command2.getPermission() == null || command2.getPermission().hasPermission(sender))
                return command2.getTabExecutor().onTabComplete(sender, command, alias, buildStrings(args));

        }

        return list;
    }

    public void registerExecutor(final @NonNull TabExecutor executor, final String key, final Permission permission) {
        if (key == null) {
            fallBack = new Command2(executor, permission);
            return;
        }

        map.put(key.toLowerCase(), new Command2(executor, permission));
    }

    public boolean hasHelp() {
        return this.help;
    }

    private String[] buildStrings(final String[] args) {
        final String[] strings = new String[args.length - 1];

        if (args.length - 1 > 0)
            System.arraycopy(args, 1, strings, 0, args.length - 1);

        return strings;
    }

    @RequiredArgsConstructor
    @Getter
    private final class Command2 {
        @NonNull
        private final TabExecutor tabExecutor;
        private final Permission permission;
    }

}

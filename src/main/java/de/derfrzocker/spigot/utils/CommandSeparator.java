package de.derfrzocker.spigot.utils;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.HashMap;
import java.util.Map;

public abstract class CommandSeparator implements TabExecutor {

    private TabExecutor fallBack;

    @Getter
    private final Map<String, TabExecutor> map = new HashMap<>();

    @Override
    public boolean onCommand(final @NonNull CommandSender sender, final @NonNull Command command, final @NonNull String label, final @NonNull String[] args) {

        if (args.length == 0) {
            if (map.containsKey(""))
                if (map.get("").onCommand(sender, command, label, args))
                    return true;
            if (fallBack == null)
                return false;
            return fallBack.onCommand(sender, command, label, args);
        }

        if (map.containsKey(args[0].toLowerCase()))
            if (map.get(args[0].toLowerCase()).onCommand(sender, command, label, buildStrings(args)))
                return true;

        if (fallBack == null)
            return false;

        return fallBack.onCommand(sender, command, label, args);
    }

    public void registerExecutor(final @NonNull TabExecutor executor, final String key) {
        if (key == null) {
            fallBack = executor;
            return;
        }

        map.put(key.toLowerCase(), executor);
    }

    private String[] buildStrings(final String[] args) {
        final String[] strings = new String[args.length - 1];

        if (args.length - 1 > 0)
            System.arraycopy(args, 1, strings, 0, args.length - 1);

        return strings;
    }

}

package de.derfrzocker.spigot.utils.command;

import de.derfrzocker.spigot.utils.message.MessageValue;
import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple help command
 */
public class HelpCommand implements TabExecutor {

    @NotNull
    private final CommandSeparator commandSeparator;

    @NotNull
    private final HelpConfig helpConfig;

    public HelpCommand(@NotNull final CommandSeparator commandSeparator, @NotNull final HelpConfig helpConfig) {
        Validate.notNull(commandSeparator, "CommandSeparator can't be null");
        Validate.notNull(helpConfig, "HelpConfig can't be null");

        this.commandSeparator = commandSeparator;
        this.helpConfig = helpConfig;
    }

    @Override
    public boolean onCommand(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String label, @NotNull final String[] args) {
        if (args.length != 1) {
            sendShortHelp(sender);
            return true;
        }


        final String commandName = args[0].toLowerCase();
        final CommandSeparator.Command2 command2 = commandSeparator.getCommands().get(commandName);

        if (command2 == null) {
            sendShortHelp(sender);
            return true;
        }

        sendHelp(sender, commandName, command2);

        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull final CommandSender sender, @NotNull final Command command, @NotNull final String alias, @NotNull final String[] args) {
        final List<String> list = new ArrayList<>();

        for (final Map.Entry<String, CommandSeparator.Command2> command2Entry : commandSeparator.getCommands().entrySet()) {
            final String commandName = command2Entry.getKey();
            final CommandSeparator.Command2 command2 = command2Entry.getValue();

            if (commandName.startsWith(args[1])) {
                if (command2.getPermission() == null) {
                    list.add(commandName);
                } else if (command2.getPermission().hasPermission(sender)) {
                    list.add(commandName);
                }
            }
        }

        return list;
    }

    private void sendShortHelp(@NotNull final CommandSender sender) {
        helpConfig.getHeaderMessageFormat().sendMessage(sender);

        boolean separator = false;

        for (final Map.Entry<String, CommandSeparator.Command2> command2Entry : commandSeparator.getCommands().entrySet()) {
            final String commandName = command2Entry.getKey();
            final CommandSeparator.Command2 command2 = command2Entry.getValue();

            if (command2.getDescription() != null && command2.getPermission() != null) {
                if (separator)
                    helpConfig.getSeparatorMessageFormat().sendMessage(sender);

                helpConfig.getShortHelpMessageFormat().sendMessage(sender,
                        new MessageValue("command", commandName),
                        new MessageValue("permission", command2.getPermission().getPermission()),
                        new MessageValue("description", command2.getDescription().getRawMessage()));
                separator = true;
            }
        }

        helpConfig.getFooterMessageFormat().sendMessage(sender);
    }

    private void sendHelp(@NotNull final CommandSender sender, @NotNull final String command, @NotNull final CommandSeparator.Command2 command2) {
        if (command2.getUsage() != null)
            helpConfig.getUsageMessageFormat().sendMessage(sender, new MessageValue("command", command), new MessageValue("usage", command2.getUsage().getRawMessage()));

        if (command2.getPermission() != null)
            helpConfig.getPermissionMessageFormat().sendMessage(sender, new MessageValue("command", command), new MessageValue("permission", command2.getPermission().getPermission()));

        if (command2.getDescription() != null)
            helpConfig.getDescriptionMessageFormat().sendMessage(sender, new MessageValue("command", command), new MessageValue("description", command2.getDescription().getRawMessage()));

    }

}

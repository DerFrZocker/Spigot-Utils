package de.derfrzocker.spigot.utils.command;

import de.derfrzocker.spigot.utils.message.MessageKey;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Various utils for commands
 */
public final class CommandUtil {

    private CommandUtil() {

    }

    /**
     * Runs the given runnable asynchronously.
     * When the runnable throw a CommandException it ignores it. On every other Exception it sends a message to the given commandSender
     *
     * @param commandSender which become a message on a error
     * @param plugin        on which the async task get's registered
     * @param runnable      that gets run
     * @throws NullPointerException if any given parameter is null
     */
    public static void runAsynchronously(@NotNull final CommandSender commandSender, @NotNull final Plugin plugin, @NotNull final Runnable runnable) {
        Validate.notNull(commandSender, "CommandSender can't be null");
        Validate.notNull(plugin, "Plugin can't be null");
        Validate.notNull(runnable, "Runnable can't be null");

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                runnable.run();
            } catch (final CommandException ignored) {
            } catch (final Exception e) {
                commandSender.sendMessage("§4Error while execute command, see console for more information.");
                commandSender.sendMessage("§4Please report the error to the Developer.");
                e.printStackTrace();
            }
        });
    }

    /**
     * returns the World with the given world name, if the world with the given name
     * does not exists, it throw a WorldNotFoundException.
     * When messageKey and commandSender are not null it also send a message to the given commandSender
     * When it send the message, it gives the World name as the "world" placeholder
     *
     * @param worldName     the name of the World
     * @param messageKey    which get send if the world was not found
     * @param commandSender which get the message when the world was not found
     * @return the world that have the given world name, it will never returns null
     * @throws NullPointerException   when worldName is null
     * @throws WorldNotFoundException when the world does not exist
     */
    @NotNull
    public static World getWorld(@NotNull final String worldName, @Nullable final MessageKey messageKey, @Nullable final CommandSender commandSender) {
        Validate.notNull(worldName, "World name can't be null");

        final World world = Bukkit.getWorld(worldName);

        if (world == null) {
            if (messageKey != null && commandSender != null) {
                messageKey.sendMessage(commandSender, new MessageValue("world", worldName));
            }
            throw new WorldNotFoundException(worldName, "The world '" + worldName + "' does not exists!");
        }

        return world;
    }

}

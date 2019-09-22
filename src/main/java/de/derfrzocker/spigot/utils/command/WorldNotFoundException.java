package de.derfrzocker.spigot.utils.command;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The WorldNotFound
 */
public class WorldNotFoundException extends CommandException {

    @NotNull
    private final String world;

    public WorldNotFoundException(@NotNull final String world, @Nullable final String message) {
        super(message);

        Validate.notNull(world, "World name can't be null");
        this.world = world;
    }

    public WorldNotFoundException(@NotNull final String world, @Nullable final String message, @Nullable final Throwable cause) {
        super(message, cause);

        Validate.notNull(world, "World name can't be null");
        this.world = world;
    }

    /**
     * @return the world that was not found
     */
    @NotNull
    public String getWorld() {
        return world;
    }

}

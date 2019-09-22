package de.derfrzocker.spigot.utils.command;

import org.jetbrains.annotations.Nullable;

public class CommandException extends IllegalArgumentException {

    public CommandException(@Nullable final String message) {
        super(message);
    }

    public CommandException(@Nullable final String message, @Nullable final Throwable cause) {
        super(message, cause);
    }

}

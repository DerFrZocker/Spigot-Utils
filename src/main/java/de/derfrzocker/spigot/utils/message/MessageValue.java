package de.derfrzocker.spigot.utils.message;

import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MessageValue {

    @NotNull
    private final String key;
    @NotNull
    private final String value;

    public MessageValue(@NotNull String key, @Nullable final Object value) {
        Validate.notNull(key, "Key cannot be null.");

        this.key = key;
        this.value = value == null ? "null" : value.toString();
    }

    @NotNull
    public String getKey() {
        return key;
    }

    @NotNull
    public String getValue() {
        return value;
    }
}

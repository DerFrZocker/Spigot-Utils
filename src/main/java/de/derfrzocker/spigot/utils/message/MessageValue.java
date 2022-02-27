package de.derfrzocker.spigot.utils.message;

public class MessageValue {

    private final String key;

    private final String value;

    public MessageValue(final String key, final Object value) {
        this.key = key;
        this.value = value.toString();
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}

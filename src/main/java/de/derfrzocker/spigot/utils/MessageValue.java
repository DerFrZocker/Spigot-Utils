package de.derfrzocker.spigot.utils;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class MessageValue {

    @NonNull
    private final String key;

    @NonNull
    private final String value;

    public MessageValue(String key, Object value) {
        this.key = key;
        this.value = value.toString();
    }

}

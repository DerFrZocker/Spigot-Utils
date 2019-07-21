package de.derfrzocker.spigot.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;
import java.util.stream.Stream;

@RequiredArgsConstructor
public enum Language {

    ENGLISH("English", "lang/en.yml"), GERMAN("Deutsch", "lang/de.yml"), CUSTOM("Custom", "messages.yml");

    @Getter
    private final String name;

    @Getter
    private final String fileLocation;

    private static Supplier<Language> languageSupplier;

    public static Language getLanguage(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }

        return Stream.of(values()).filter(value -> value.getName().equalsIgnoreCase(name)).findAny().orElseThrow(IllegalArgumentException::new);
    }

    public static Language getDefaultLanguage() {
        if (languageSupplier == null)
            return CUSTOM;

        return languageSupplier.get();
    }

    public static void setDefaultLanguage(Supplier<Language> defaultLanguage) {
        languageSupplier = defaultLanguage;
    }

}

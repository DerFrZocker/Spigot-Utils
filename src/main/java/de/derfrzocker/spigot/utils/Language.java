package de.derfrzocker.spigot.utils;

import java.util.function.Supplier;
import java.util.stream.Stream;

public enum Language {

    ENGLISH(new String[]{"English"}, "lang/en.yml"), GERMAN(new String[]{"Deutsch"}, "lang/de.yml"), CHINESE(new String[]{"中国", "中國"}, "lang/zh.yml"), CUSTOM(new String[]{"Custom"}, "messages.yml");

    private final String[] names;

    private final String fileLocation;

    private static Supplier<Language> languageSupplier;

    Language(String[] strings, String s) {
        names = strings;
        fileLocation = s;
    }

    public static Language getLanguage(final String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (final IllegalArgumentException ignored) {
        }

        return Stream.of(values()).filter(value -> Stream.of(value.getNames()).anyMatch(name2 -> name2.equalsIgnoreCase(name))).findAny().orElseThrow(IllegalArgumentException::new);
    }

    public static Language getDefaultLanguage() {
        if (languageSupplier == null)
            return CUSTOM;

        return languageSupplier.get();
    }

    public static void setDefaultLanguage(final Supplier<Language> defaultLanguage) {
        languageSupplier = defaultLanguage;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public String[] getNames() {
        return names;
    }
}

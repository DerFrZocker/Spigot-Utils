package de.derfrzocker.spigot.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;
import java.util.stream.Stream;

@RequiredArgsConstructor
public enum Language {

    ENGLISH(new String[]{"English"}, "lang/en.yml"), GERMAN(new String[]{"Deutsch"}, "lang/de.yml"), CHINESE(new String[]{"中国", "中國"}, "lang/zh.yml") ,CUSTOM(new String[]{"Custom"}, "messages.yml");

    @Getter
    private final String[] names;

    @Getter
    private final String fileLocation;

    private static Supplier<Language> languageSupplier;

    public static Language getLanguage(String name) {
        try {
            return valueOf(name.toUpperCase());
        } catch (IllegalArgumentException ignored) {
        }

        return Stream.of(values()).filter(value -> Stream.of(value.getNames()).anyMatch(name2 -> name2.equalsIgnoreCase(name))).findAny().orElseThrow(IllegalArgumentException::new);
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

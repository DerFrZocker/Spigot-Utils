package de.derfrzocker.spigot.utils.language;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public interface LanguageLoader {

    /**
     * @return a map of available languages
     */
    @NotNull
    Map<String, Language> loadLanguages();
}

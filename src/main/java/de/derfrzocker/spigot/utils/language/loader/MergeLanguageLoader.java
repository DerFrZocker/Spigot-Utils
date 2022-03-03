package de.derfrzocker.spigot.utils.language.loader;

import de.derfrzocker.spigot.utils.language.Language;
import de.derfrzocker.spigot.utils.language.LanguageLoader;
import org.apache.commons.lang.Validate;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class MergeLanguageLoader implements LanguageLoader {

    @NotNull
    private final Plugin plugin;
    @NotNull
    private final LanguageLoader primary;
    @NotNull
    private final LanguageLoader secondary;

    public MergeLanguageLoader(@NotNull Plugin plugin, @NotNull LanguageLoader primary, @NotNull LanguageLoader secondary) {
        Validate.notNull(plugin, "Plugin cannot be null.");
        Validate.notNull(primary, "Primary LanguageLoader cannot be null.");
        Validate.notNull(secondary, "Secondary LanguageLoader cannot be null.");

        this.plugin = plugin;
        this.primary = primary;
        this.secondary = secondary;
    }

    @Override
    public @NotNull Map<String, Language> loadLanguages() {
        Map<String, Language> primaryLanguages = primary.loadLanguages();
        Map<String, Language> secondaryLanguages = secondary.loadLanguages();
        Map<String, Language> result = new LinkedHashMap<>();


        for (Map.Entry<String, Language> entry : secondaryLanguages.entrySet()) {
            Language primaryLanguage = primaryLanguages.remove(entry.getKey());

            if (primaryLanguage == null) {
                result.put(entry.getKey(), entry.getValue());
            } else {
                Set<String> authors = new LinkedHashSet<>(entry.getValue().getAuthors());
                authors.addAll(primaryLanguage.getAuthors());
                Language language = new Language(entry.getKey(), authors, primaryLanguage.getLogo(), entry.getValue().getSetting().withSetting(primaryLanguage.getSetting()));
                result.put(entry.getKey(), language);
            }
        }

        result.putAll(primaryLanguages);

        return result;
    }
}

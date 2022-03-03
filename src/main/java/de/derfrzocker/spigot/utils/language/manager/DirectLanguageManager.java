package de.derfrzocker.spigot.utils.language.manager;

import de.derfrzocker.spigot.utils.language.Language;
import de.derfrzocker.spigot.utils.language.LanguageLoader;
import de.derfrzocker.spigot.utils.language.LanguageManager;
import de.derfrzocker.spigot.utils.setting.DummySetting;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

public class DirectLanguageManager implements LanguageManager {

    private final static String KEY = "language";

    @NotNull
    private final Map<String, Language> languages = new LinkedHashMap<>();
    @NotNull
    private final Plugin plugin;
    @NotNull
    private final LanguageLoader languageLoader;
    @NotNull
    private final String defaultLanguageName;
    @NotNull
    private final NamespacedKey languageKey;

    @NotNull
    private Language defaultLanguage;

    public DirectLanguageManager(@NotNull Plugin plugin, @NotNull LanguageLoader languageLoader, @NotNull String defaultLanguageName) {
        Validate.notNull(plugin, "Plugin cannot be null.");
        Validate.notNull(languageLoader, "LanguageLoader cannot be null.");
        Validate.notNull(defaultLanguageName, "Default language name cannot be null.");

        this.plugin = plugin;
        this.languageLoader = languageLoader;
        this.defaultLanguageName = defaultLanguageName;
        this.languageKey = new NamespacedKey(plugin, KEY);

        this.defaultLanguage = loadAndReturnDefaultLanguage();
    }

    @Override
    public boolean hasLanguageSet(@NotNull Player player) {
        Validate.notNull(player, "Player cannot be null.");

        return player.getPersistentDataContainer().has(languageKey, PersistentDataType.STRING);
    }

    @Override
    public void setLanguage(@NotNull Player player, @NotNull Language language) {
        Validate.notNull(player, "Player cannot be null.");
        Validate.notNull(language, "Language cannot be null.");

        player.getPersistentDataContainer().set(languageKey, PersistentDataType.STRING, language.getName());
    }

    @Override
    public void removeLanguage(@NotNull Player player) {
        Validate.notNull(player, "Player cannot be null.");

        player.getPersistentDataContainer().remove(languageKey);
    }

    @Override
    public @NotNull Language getLanguage(@NotNull Player player) {
        String languageName = player.getPersistentDataContainer().get(languageKey, PersistentDataType.STRING);

        if (languageName == null) {
            return defaultLanguage;
        }

        Language language = languages.get(languageName);

        if (language == null) {
            plugin.getLogger().info(String.format("Unknown language '%s' for player '%s', removing it and returning default language.", languageName, player.getName()));
            return defaultLanguage;
        }

        return language;
    }

    @Override
    public void reload() {
        languages.clear();
        defaultLanguage = loadAndReturnDefaultLanguage();
    }

    @NotNull
    private Language loadAndReturnDefaultLanguage() {
        Language defaultLanguage;

        languages.putAll(languageLoader.loadLanguages());

        defaultLanguage = languages.get(defaultLanguageName);

        if (defaultLanguage == null) {
            plugin.getLogger().warning(String.format("The default language '%s' could not be found, using empty language, this is a bug!", defaultLanguageName));
            defaultLanguage = new Language(defaultLanguageName, new LinkedHashSet<>(), new ItemStack(Material.STONE), new DummySetting());
        }

        return defaultLanguage;
    }
}

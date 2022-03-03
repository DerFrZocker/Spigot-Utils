package de.derfrzocker.spigot.utils.language.loader;

import de.derfrzocker.spigot.utils.language.Language;
import de.derfrzocker.spigot.utils.language.LanguageLoader;
import de.derfrzocker.spigot.utils.setting.ConfigSetting;
import de.derfrzocker.spigot.utils.setting.Setting;
import org.apache.commons.lang.Validate;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class FileLanguageLoader implements LanguageLoader {

    @NotNull
    private final Plugin plugin;
    @NotNull
    private final File directory;

    public FileLanguageLoader(@NotNull Plugin plugin, @NotNull File directory) {
        Validate.notNull(plugin, "Plugin cannot be null.");
        Validate.notNull(directory, "Directory cannot be null.");

        this.plugin = plugin;
        this.directory = directory;
    }

    @Override
    public @NotNull Map<String, Language> loadLanguages() {
        Map<String, Language> languageMap = new LinkedHashMap<>();
        if (!directory.exists() || !directory.isDirectory()) {
            return languageMap;
        }

        File[] languages = directory.listFiles();

        if (languages == null) {
            return languageMap;
        }

        for (File language : languages) {
            if (!language.isDirectory()) {
                continue;
            }

            String name = language.getName();

            File infoFile = new File(language, "info.yml");

            if (infoFile.exists()) {
                plugin.getLogger().info(String.format("No 'info.yml' found for the language '%s' in directory '%s', ignoring it.", name, language));
                continue;
            }

            if (infoFile.isDirectory()) {
                plugin.getLogger().info(String.format("'info.yml' for the language '%s' in directory '%s' is a directory, ignoring it.", name, language));
                continue;
            }

            YamlConfiguration infoConfig = YamlConfiguration.loadConfiguration(infoFile);

            ItemStack icon = infoConfig.getItemStack("icon");

            if (icon == null) {
                plugin.getLogger().info(String.format("No icon found in 'info.yml' for the language '%s' in directory '%s' using default icon.", name, language));
                icon = new ItemStack(Material.STONE);
            }

            Set<String> authors;
            if (infoConfig.isList("authors")) {
                authors = new LinkedHashSet<>(infoConfig.getStringList("authors"));
            } else {
                String author = infoConfig.getString("authors");
                authors = new LinkedHashSet<>();
                if (author != null) {
                    authors.add(author);
                }
            }

            Setting setting = new ConfigSetting(() -> YamlConfiguration.loadConfiguration(infoFile));
            Set<File> toAdd = new LinkedHashSet<>();
            toAdd.add(language);
            Iterator<File> iterator = toAdd.iterator();

            while (iterator.hasNext()) {
                File file = iterator.next();
                iterator.remove();

                if (file.isDirectory()) {
                    File[] files = file.listFiles();

                    if (files != null) {
                        toAdd.addAll(Arrays.asList(files));
                    }

                    toAdd.remove(infoFile);
                    iterator = toAdd.iterator();

                    continue;
                }

                if (!file.isFile()) {
                    continue;
                }

                setting = setting.withSetting(new ConfigSetting(() -> YamlConfiguration.loadConfiguration(file)));
            }

            languageMap.put(name, new Language(name, authors, icon, setting));
        }

        return languageMap;
    }
}

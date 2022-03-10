package de.derfrzocker.spigot.utils.language.loader;

import com.google.common.base.Charsets;
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
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class PluginLanguageLoader implements LanguageLoader {

    @NotNull
    private final Plugin plugin;
    @Nullable
    private final File saveLocation;

    public PluginLanguageLoader(@NotNull Plugin plugin) {
        this(plugin, null);
    }

    public PluginLanguageLoader(@NotNull Plugin plugin, @Nullable File saveLocation) {
        Validate.notNull(plugin, "Plugin cannot be null.");

        this.plugin = plugin;
        this.saveLocation = saveLocation;
    }

    @Override
    public @NotNull Map<String, Language> loadLanguages() {
        Map<String, Language> languageMap = new LinkedHashMap<>();
        Map<String, Map<String, JarEntry>> languages = new LinkedHashMap<>();

        try (JarFile jarFile = new JarFile(new File(plugin.getClass().getProtectionDomain().getCodeSource().getLocation().toURI()))) {
            ZipEntry langDirectory = jarFile.getEntry("lang");
            if (langDirectory == null || !langDirectory.isDirectory()) {
                return languageMap;
            }

            // Search the plugin for the language files
            // They should be in the directory 'lang'
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                if (!name.startsWith("lang/") || name.equals("lang/")) {
                    continue;
                }

                String[] result = name.split("/");

                if (result.length <= 2) {
                    // We got the language directory, ignore it
                    continue;
                }

                languages.computeIfAbsent(result[1], key -> new LinkedHashMap<>()).put(name.replaceFirst("lang/" + result[1] + "/", ""), entry);
            }

            // Load Language
            for (Map.Entry<String, Map<String, JarEntry>> entry : languages.entrySet()) {
                Map<String, JarEntry> files = entry.getValue();

                JarEntry infoFile = files.remove("info.yml"); // Use remove, so that it is not present later when creating the setting objects
                if (infoFile == null) {
                    plugin.getLogger().warning(String.format("No 'info.yml' found for the language '%s' in plugin '%s', ignoring it, this is a bug!", entry.getKey(), plugin.getName()));
                    continue;
                }

                if (infoFile.isDirectory()) {
                    plugin.getLogger().warning(String.format("'info.yml' for the language '%s' in plugin '%s' is a directory, ignoring it, this is a bug!", entry.getKey(), plugin.getName()));
                    continue;
                }

                YamlConfiguration infoConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(jarFile.getInputStream(infoFile), Charsets.UTF_8));

                ItemStack icon = infoConfig.getItemStack("icon");

                if (icon == null) {
                    plugin.getLogger().warning(String.format("No icon found in 'info.yml' for the language '%s' in plugin '%s' using default icon.", entry.getKey(), plugin.getName()));
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

                Setting setting = new ConfigSetting(() -> infoConfig);

                if (saveLocation != null) {
                    if (!saveLocation.exists()) {
                        saveLocation.mkdirs();
                    }

                    File file = new File(saveLocation, "info.yml");
                    if (!file.exists()) {
                        infoConfig.save(file);
                    }
                }

                for (Map.Entry<String, JarEntry> jarEntry : files.entrySet()) {
                    if (jarEntry.getValue().isDirectory()) {
                        continue;
                    }

                    YamlConfiguration configuration = YamlConfiguration.loadConfiguration(new InputStreamReader(jarFile.getInputStream(jarEntry.getValue()), Charsets.UTF_8));

                    if (saveLocation != null) {
                        File file = new File(saveLocation, jarEntry.getKey());
                        if (!file.exists()) {
                            configuration.save(file);
                        }
                    }

                    setting = setting.withSetting(new ConfigSetting(() -> configuration));
                }

                languageMap.put(entry.getKey(), new Language(entry.getKey(), authors, icon, setting));
            }
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(String.format("Unexpected error while reading languages from plugin '%s'", plugin.getName()), e);
        }

        return languageMap;
    }
}

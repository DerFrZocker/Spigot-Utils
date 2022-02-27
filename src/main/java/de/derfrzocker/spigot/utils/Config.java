package de.derfrzocker.spigot.utils;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import static java.lang.String.format;

/**
 * This is the Config class<br>
 * <br>
 * <p>
 * [notice me if you find Bugs or spelling mistake] <br>
 * [or if you have idea for more functions]
 *
 * @author ? <br>
 * optimized by DerFrZocker
 */
public class Config extends YamlConfiguration {

    @SuppressWarnings({"UnstableApiUsage", "ResultOfMethodCallIgnored"})
    public Config(final File file) {

        if (!file.exists()) {
            try {
                Files.createParentDirs(file);
                file.createNewFile();
            } catch (final IOException e) {
                throw new RuntimeException("Error while create a new File: " + file, e);
            }
        }

        // try to load the Config file
        try {
            this.load(file);
        } catch (final Exception e) {
            throw new RuntimeException("Error while load data from file: " + file, e);
        }

    }

    public Config(final InputStream input) {

        // try to load the Config file
        try {
            this.load(input);
        } catch (final Exception e) {
            throw new RuntimeException("Error while load data from InputStream: " + input, e);
        }

    }

    public Config(final String input) {
        try {
            this.loadFromString(input);
        } catch (final InvalidConfigurationException e) {
            throw new RuntimeException("Error while load data from String: " + input, e);
        }
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void save(final File file) throws IOException {
        Files.createParentDirs(file);

        try (final OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file),
                Charsets.UTF_8)) {

            writer.write(this.saveToString());
        }
    }

    @Override
    public void load(final File file) throws IOException, InvalidConfigurationException {

        // load the config file
        this.load(new InputStreamReader(new FileInputStream(file), Charsets.UTF_8));
    }

    @SuppressWarnings("WeakerAccess")
    public void load(final InputStream input) throws IOException, InvalidConfigurationException {

        // load the config file
        this.load(new InputStreamReader(input, Charsets.UTF_8));
    }

    public static Config getConfig(@NotNull Plugin plugin, @NotNull String name) {
        return getConfig(plugin, name, true);
    }

    public static Config getConfig(@NotNull Plugin plugin, @NotNull String name, boolean saveDefaults) {
        Validate.notNull(plugin, "Plugin cannot be null");
        Validate.notNull(name, "Name cannot be null");

        if (!name.endsWith(".yml"))
            name = format("%s.yml", name);

        final File file = new File(plugin.getDataFolder().getPath(), name);

        final Config defaults = new Config(plugin.getResource(name));

        if (!file.exists())
            plugin.saveResource(name, true);

        final Config config = new Config(file);

        config.setDefaults(defaults);

        if (saveDefaults) {
            config.options().copyDefaults(true);
            try {
                config.save(file);
            } catch (final IOException e) {
                throw new RuntimeException("Error while save data to file: " + file, e);
            }
        }

        return config;
    }

}

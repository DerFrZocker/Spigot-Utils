package de.derfrzocker.spigot.utils.setting;

import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.function.Supplier;

public class ConfigSetting extends AbstractSetting<ConfigSetting> {

    @Nullable
    private final Supplier<ConfigurationSection> sectionSupplier;
    public ConfigurationSection configuration;

    public ConfigSetting() {
        this(null);
    }

    public ConfigSetting(@Nullable Supplier<ConfigurationSection> sectionSupplier) {
        this.sectionSupplier = sectionSupplier;
        reload();
    }

    @Override
    public void reload() {
        if (sectionSupplier != null) {
            configuration = sectionSupplier.get();
        }
    }

    @Override
    protected Object get0(String key) {
        return configuration == null ? null : configuration.get(key);
    }

    @Override
    protected Set<String> getKeys0(String key) {
        if (configuration == null) {
            return null;
        }

        ConfigurationSection section = configuration.getConfigurationSection(key);

        if (section == null) {
            return null;
        }

        return section.getKeys(false);
    }

    @Override
    protected ConfigSetting createEmptySetting() {
        return new ConfigSetting();
    }
}

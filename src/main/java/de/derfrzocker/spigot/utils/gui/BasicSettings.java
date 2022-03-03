package de.derfrzocker.spigot.utils.gui;

import de.derfrzocker.spigot.utils.Config;
import de.derfrzocker.spigot.utils.Pair;
import de.derfrzocker.spigot.utils.ReloadAble;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.NumberConversions;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class BasicSettings implements ReloadAble {

    @NotNull
    private final Set<Supplier<ConfigurationSection>> others = new LinkedHashSet<>();
    @NotNull
    private final Supplier<ConfigurationSection> configurationSectionSupplier;
    @NotNull
    private final Plugin plugin;
    @NotNull
    private ConfigurationSection section;

    public BasicSettings(@NotNull final Plugin plugin, @NotNull final String file) {
        this(plugin, file, true);
    }

    public BasicSettings(@NotNull final Plugin plugin, @NotNull final String file, final boolean copy) {
        this(plugin, () -> copy ? Config.getConfig(plugin, file, false) : new Config(plugin.getResource(file)));
    }

    public BasicSettings(@NotNull final Plugin plugin, @NotNull final Supplier<ConfigurationSection> configurationSectionSupplier) {
        Validate.notNull(plugin, "Plugin can not be null");
        Validate.notNull(configurationSectionSupplier, "Supplier can not be null");

        this.configurationSectionSupplier = configurationSectionSupplier;
        this.plugin = plugin;
        this.section = configurationSectionSupplier.get();
        reload();
    }

    /**
     * Add values from a different file to this settings
     *
     * @param file the name to add
     */
    public void addValues(@NotNull final String file) {
        addValues(file, true);
    }

    /**
     * Add values from a different file to this settings
     *
     * @param file the name to add
     * @param copy should it copy to the plugins directory
     */
    public void addValues(@NotNull final String file, final boolean copy) {
        addValues(() -> copy ? Config.getConfig(plugin, file, false) : new Config(plugin.getResource(file)));
    }

    /**
     * Add values from a different ConfigurationSection to this settings
     *
     * @param configurationSectionSupplier to add
     */
    public void addValues(@NotNull final Supplier<ConfigurationSection> configurationSectionSupplier) {
        Validate.notNull(configurationSectionSupplier, "Supplier can not be null");

        others.add(configurationSectionSupplier);

        final ConfigurationSection configurationSection = configurationSectionSupplier.get();

        for (final String key : configurationSection.getKeys(true)) {
            final Object value = configurationSection.get(key);

            if (value instanceof ConfigurationSection) {
                continue;
            }

            section.addDefault(key, value);
        }
    }

    /**
     * @return the name of the inventory
     */
    public String getInventoryName() {
        return getSection().getString("name");
    }

    /**
     * @return the amount of rows the inventory should have
     */
    public int getRows() {
        return getSection().getInt("rows");
    }

    /**
     * @return the ConfigurationSection if this settings
     */
    @NotNull
    public ConfigurationSection getSection() {
        return this.section;
    }

    /**
     * @return a set with all decorations
     */
    @NotNull
    public Set<Pair<Set<Integer>, ItemStack>> getDecorations() {
        final Set<Pair<Set<Integer>, ItemStack>> set = new LinkedHashSet<>();

        final ConfigurationSection decorationSection = getSection().getConfigurationSection("decorations");

        if (decorationSection == null)
            return set;

        final Set<String> keys = decorationSection.getKeys(false);

        keys.forEach(key -> {
            final Object object = decorationSection.get(key + ".slot");
            final ItemStack itemStack = decorationSection.getItemStack(key + ".item-stack").clone();
            final Set<Integer> integers = new LinkedHashSet<>();

            if (object instanceof Number) {
                integers.add(((Number) object).intValue());
            } else {
                final List<?> list = (List<?>) object;
                list.forEach(integer -> integers.add(NumberConversions.toInt(integer)));
            }

            set.add(new Pair<>(integers, itemStack));

        });

        return set;
    }

    @Override
    public void reload() {
        section = configurationSectionSupplier.get();

        others.forEach(configurationSectionSupplier -> {
            final ConfigurationSection configurationSection = configurationSectionSupplier.get();

            for (final String key : configurationSection.getKeys(true)) {
                final Object value = configurationSection.get(key);

                if (value instanceof ConfigurationSection) {
                    continue;
                }

                section.addDefault(key, value);
            }
        });
    }

}

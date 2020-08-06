package de.derfrzocker.spigot.utils.gui;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class PageSettings extends BasicSettings {


    public PageSettings(@NotNull final Plugin plugin, @NotNull final String file) {
        super(plugin, file);
    }

    public PageSettings(@NotNull final Plugin plugin, @NotNull final String file, final boolean copy) {
        super(plugin, file, copy);
    }

    public PageSettings(@NotNull final Plugin plugin, @NotNull final Supplier<ConfigurationSection> configurationSectionSupplier) {
        super(plugin, configurationSectionSupplier);
    }

    public int getNextPageSlot() {
        return getSection().getInt("next-page.slot");
    }

    @NotNull
    public ItemStack getNextPageItemStack() {
        return getSection().getItemStack("next-page.item-stack").clone();
    }

    public int getPreviousPageSlot() {
        return getSection().getInt("previous-page.slot");
    }

    @NotNull
    public ItemStack getPreviousPageItemStack() {
        return getSection().getItemStack("previous-page.item-stack").clone();
    }

    public int getGap() {
        return getSection().getInt("gap");
    }

    public int getEmptyRowsUp() {
        return getSection().getInt("empty-rows.up");
    }

    public int getEmptyRowsBelow() {
        return getSection().getInt("empty-rows.below");
    }

}

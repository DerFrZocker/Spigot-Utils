package de.derfrzocker.spigot.utils.gui;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

public abstract class InventoryGui {

    @NotNull
    private final Plugin plugin;

    public InventoryGui(@NotNull final Plugin plugin) {
        Validate.notNull(plugin, "Plugin can not be null");

        this.plugin = plugin;
    }

    /**
     * @return the Bukkit Inventory of this Inventory Gui
     */
    @NotNull
    abstract Inventory getInventory();

    /**
     * Execute a none specific action, when the inventory gets clicked
     *
     * @param event to use
     * @throws IllegalArgumentException if event is null
     */
    public abstract void onClick(@NotNull final InventoryClickEvent event);

    /**
     * Opens the Inventory gui for the given entity,
     * If this method is called from the primary Thread, it gets
     * executed directly.
     * If this method is called not from the primary Thread, it post the execution to the
     * primary Thread and waits until it gets executed.
     *
     * @param entity which get the Inventory gui open
     */
    public void openSync(@NotNull final HumanEntity entity) {
        Validate.notNull(entity, "Entity can not be null");

        if (Bukkit.isPrimaryThread()) {
            InventoryGuiManager.getInventoryGuiManager(getPlugin()).registerInventoryGui(this);
            entity.openInventory(getInventory());
            return;
        }

        try {
            Bukkit.getScheduler().callSyncMethod(getPlugin(), () -> {
                InventoryGuiManager.getInventoryGuiManager(getPlugin()).registerInventoryGui(this);
                return entity.openInventory(getInventory());
            }).get();
        } catch (final InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error while open inventory Sync!", e);
        }
    }

    /**
     * Closed the Inventory of the given entity,
     * If this method is called from the primary Thread, it gets
     * executed directly.
     * If this method is called not from the primary Thread, it post the execution to the
     * primary Thread and waits until it gets executed.
     *
     * @param entity which get the Inventory closed
     * @throws IllegalArgumentException if entity is null
     */
    public void closeSync(@NotNull final HumanEntity entity) {
        Validate.notNull(entity, "Entity can not be null");

        if (Bukkit.isPrimaryThread()) {
            entity.closeInventory();
            return;
        }

        try {
            Bukkit.getScheduler().callSyncMethod(getPlugin(), () -> {
                entity.closeInventory();
                return true;
            }).get();
        } catch (final InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error while close inventory Sync!", e);
        }
    }

    /**
     * @return the plugin, which create and use this InventoryGui
     */
    @NotNull
    public Plugin getPlugin() {
        return this.plugin;
    }

}

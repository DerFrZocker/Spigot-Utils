package de.derfrzocker.spigot.utils.gui;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@RequiredArgsConstructor
public abstract class InventoryGui {

    private static final Set<HumanEntity> HUMAN_ENTITY_SET = Collections.synchronizedSet(new HashSet<>());

    @Getter
    @NonNull
    private final JavaPlugin plugin;


    abstract Inventory getInventory();

    public abstract void onClick(final @NonNull InventoryClickEvent event);

    public void openSync(final @NonNull HumanEntity entity) {
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

    public void closeSync(final @NonNull HumanEntity entity) {
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

}

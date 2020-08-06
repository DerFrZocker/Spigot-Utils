package de.derfrzocker.spigot.utils.gui;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class InventoryGuiManager implements Listener {

    private static final Set<HumanEntity> HUMAN_ENTITY_SET = Collections.synchronizedSet(new HashSet<>());
    private final static Map<Plugin, InventoryGuiManager> INVENTORY_GUI_MANAGER_MAP = new HashMap<>();

    private final Map<Inventory, InventoryGui> inventoryGuiMap = new HashMap<>();
    @NotNull
    private final Plugin plugin;

    private InventoryGuiManager(@NotNull final Plugin plugin) {
        Validate.notNull(plugin, "Plugin can not be null");

        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static InventoryGuiManager getInventoryGuiManager(@NotNull final Plugin plugin) {
        Validate.notNull(plugin, "Plugin can not be null");

        return INVENTORY_GUI_MANAGER_MAP.computeIfAbsent(plugin, InventoryGuiManager::new);
    }

    public void registerInventoryGui(@NotNull final InventoryGui inventoryGui) {
        Validate.notNull(inventoryGui, "InventoryGui can not be null");

        if (!inventoryGui.getPlugin().equals(plugin))
            throw new IllegalArgumentException("The InventoryGui is not from the same plugin(" + inventoryGui.getPlugin() + ") as the InventoryGuiManager(" + plugin + ")");

        inventoryGuiMap.put(inventoryGui.getInventory(), inventoryGui);
    }

    @EventHandler
    public void onInventoryClick(@NotNull final InventoryClickEvent event) {
        final InventoryGui inventoryGui = inventoryGuiMap.get(event.getView().getTopInventory());

        if (inventoryGui == null)
            return;

        event.setCancelled(true);

        if (HUMAN_ENTITY_SET.contains(event.getWhoClicked()))
            return;

        HUMAN_ENTITY_SET.add(event.getWhoClicked());

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                inventoryGui.onClick(event);
            } catch (final Exception e) {
                event.getWhoClicked().sendMessage("§4Error while execute gui action, see console for more information.");
                event.getWhoClicked().sendMessage("§4Please report the error to the Developer.");
                e.printStackTrace();
            } finally {
                HUMAN_ENTITY_SET.remove(event.getWhoClicked());
            }
        });
    }

    @EventHandler
    public void onInventoryClose(@NotNull final InventoryCloseEvent event) {
        final InventoryGui inventoryGui = inventoryGuiMap.get(event.getView().getTopInventory());

        if (inventoryGui == null)
            return;

        if (inventoryGui.getInventory().getViewers().size() > 1)
            return;

        inventoryGuiMap.remove(inventoryGui.getInventory());
    }

}

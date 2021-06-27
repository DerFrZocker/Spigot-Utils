package de.derfrzocker.spigot.utils.guin;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

public interface InventoryGui {

    void onClick(InventoryClickEvent event);

    InventoryGui getInventoryGui(Object identifier);

    void setPreviousGui(HumanEntity humanEntity, InventoryGui inventoryGui);

    InventoryGui getPreviousGui(HumanEntity humanEntity);

    void createGui(Plugin plugin, HumanEntity humanEntity);

    void updatedSoft(HumanEntity humanEntity);

    void updatedSoft();

    void openGui(Plugin plugin, HumanEntity humanEntity, boolean updated);

    void addInventoryGui(Object identifier, InventoryGui inventoryGui);

}

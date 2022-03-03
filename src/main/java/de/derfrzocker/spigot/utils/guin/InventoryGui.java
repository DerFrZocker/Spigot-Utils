package de.derfrzocker.spigot.utils.guin;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

public interface InventoryGui {

    void onClick(InventoryClickEvent event);

    void createGui(Plugin plugin, HumanEntity humanEntity);

    void updatedSoft(HumanEntity humanEntity);

    void updatedSoft();

    void openGui(Plugin plugin, HumanEntity humanEntity, boolean updated);

}

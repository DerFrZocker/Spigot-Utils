package de.derfrzocker.spigot.utils.gui;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

public interface InventoryGui {

    String getIdentifier();

    void onClick(InventoryClickEvent event);

    void createGui(Plugin plugin, HumanEntity humanEntity);

    void updatedSoft(HumanEntity humanEntity);

    void updatedSoft();

    void openGui(Plugin plugin, HumanEntity humanEntity, boolean updated);

    void onBack(Player player);
}

package de.derfrzocker.spigot.utils.guin;

import org.bukkit.event.inventory.InventoryClickEvent;

public class ClickAction {

    private final InventoryGui inventoryGui;
    private final InventoryClickEvent clickEvent;

    public ClickAction(InventoryGui inventoryGui, InventoryClickEvent clickEvent) {
        this.inventoryGui = inventoryGui;
        this.clickEvent = clickEvent;
    }

    public InventoryClickEvent getClickEvent() {
        return clickEvent;
    }

    public InventoryGui getInventoryGui() {
        return inventoryGui;
    }
}

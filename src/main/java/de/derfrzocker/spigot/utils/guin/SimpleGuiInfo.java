package de.derfrzocker.spigot.utils.guin;

import org.bukkit.entity.HumanEntity;

public class SimpleGuiInfo implements GuiInfo {

    private final InventoryGui inventoryGui;
    private final HumanEntity humanEntity;

    public SimpleGuiInfo(InventoryGui inventoryGui, HumanEntity humanEntity) {
        this.inventoryGui = inventoryGui;
        this.humanEntity = humanEntity;
    }

    @Override
    public HumanEntity getEntity() {
        return humanEntity;
    }

    @Override
    public InventoryGui getInventoryGui() {
        return inventoryGui;
    }
}

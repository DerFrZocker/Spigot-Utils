package de.derfrzocker.spigot.utils.gui;

import org.bukkit.entity.HumanEntity;

public class PagedGuiInfo implements GuiInfo {

    private final InventoryGui inventoryGui;
    private final HumanEntity humanEntity;
    private final int maxPages;
    private final int currentPage;

    public PagedGuiInfo(InventoryGui inventoryGui, HumanEntity humanEntity, int maxPages, int currentPage) {
        this.inventoryGui = inventoryGui;
        this.humanEntity = humanEntity;
        this.maxPages = maxPages;
        this.currentPage = currentPage;
    }

    @Override
    public HumanEntity getEntity() {
        return humanEntity;
    }

    @Override
    public InventoryGui getInventoryGui() {
        return inventoryGui;
    }

    public int getMaxPages() {
        return maxPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }
}

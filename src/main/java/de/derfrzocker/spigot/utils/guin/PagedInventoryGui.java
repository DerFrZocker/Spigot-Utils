package de.derfrzocker.spigot.utils.guin;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.derfrzocker.spigot.utils.guin.buttons.Button;
import de.derfrzocker.spigot.utils.guin.buttons.ButtonContext;
import de.derfrzocker.spigot.utils.guin.buttons.ListButton;
import de.derfrzocker.spigot.utils.guin.buttons.PageContent;
import de.derfrzocker.spigot.utils.setting.Setting;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.NumberConversions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class PagedInventoryGui<D> implements InventoryGui, Listener {

    private final Map<HumanEntity, InventoryGui> previous = new LinkedHashMap<>();

    private final String identifier;
    private final Setting setting;
    private final BiFunction<Setting, GuiInfo, Integer> rows;
    private final BiFunction<Setting, GuiInfo, String> name;
    private final BiFunction<Setting, GuiInfo, Integer> upperGap;
    private final BiFunction<Setting, GuiInfo, Integer> lowerGap;
    private final BiFunction<Setting, GuiInfo, Integer> sideGap;
    private final BiFunction<Setting, GuiInfo, Boolean> allowBottomPickUp;
    private final BiFunction<Setting, GuiInfo, Boolean> decorations;
    private final PageContent<D> pageContent;
    private final List<ButtonContext> buttonContexts = new LinkedList<>();
    private final List<ListButton> listButtons = new LinkedList<>();

    private final Map<Inventory, HumanEntity> inventoryHuman = new LinkedHashMap<>();
    private final Map<HumanEntity, PagedInventoryGuiData> guiDatas = new LinkedHashMap<>();

    private boolean registered = false;

    public PagedInventoryGui(String identifier, Setting setting, BiFunction<Setting, GuiInfo, Integer> rows, BiFunction<Setting, GuiInfo, String> name, BiFunction<Setting, GuiInfo, Integer> upperGap, BiFunction<Setting, GuiInfo, Integer> lowerGap, BiFunction<Setting, GuiInfo, Integer> sideGap, BiFunction<Setting, GuiInfo, Boolean> allowBottomPickUp, PageContent<D> pageContent, BiFunction<Setting, GuiInfo, Boolean> decorations) {
        this.identifier = identifier;
        this.setting = setting;
        this.rows = rows;
        this.name = name;
        this.upperGap = upperGap;
        this.lowerGap = lowerGap;
        this.sideGap = sideGap;
        this.allowBottomPickUp = allowBottomPickUp;
        this.pageContent = pageContent;
        this.decorations = decorations;
    }

    public void addButtonContext(ButtonContext buttonContext) {
        buttonContexts.add(buttonContext);
    }

    public void addListButton(ListButton listButton) {
        listButtons.add(listButton);
    }

    public void openNextInventory(Inventory inventory, HumanEntity humanEntity) {
        openInventory(inventory, humanEntity, 1);
    }

    public void openPreviousInventory(Inventory inventory, HumanEntity humanEntity) {
        openInventory(inventory, humanEntity, -1);
    }

    private void openInventory(Inventory inventory, HumanEntity humanEntity, int offset) {
        HumanEntity key = inventoryHuman.get(inventory);
        if (key == null) {
            return;
        }

        PagedInventoryGuiData guiData = guiDatas.get(key);
        if (guiData == null) {
            return;
        }

        Integer page = guiData.inventorys.get(inventory);
        if (page == null) {
            return;
        }

        Inventory toOpen = guiData.inventorys.inverse().get(page + offset);
        if (toOpen == null) {
            return;
        }

        humanEntity.openInventory(toOpen);
    }

    @EventHandler
    @Override
    public void onClick(InventoryClickEvent event) {
        HumanEntity owner = inventoryHuman.get(event.getView().getTopInventory());
        if (owner == null) {
            return;
        }

        PagedInventoryGuiData guiData = guiDatas.get(owner);

        if (event.getView().getBottomInventory().equals(event.getClickedInventory())) {
            if (!guiData.allowBottomPickUp) {
                event.setCancelled(true);
            }
            return;
        }

        int page = guiData.inventorys.get(event.getView().getTopInventory());
        Map<Integer, Integer> slotToPosition = guiData.inventoryDatas.get(page);

        if (slotToPosition != null) {
            Integer position = slotToPosition.get(event.getRawSlot());
            if (position != null) {
                pageContent.onClick(new ClickAction(this, event), guiData.sortedData.get(position));
                return;
            }
        }

        Map<Integer, Button> buttons = guiData.inventoryButtons.get(page);

        if (buttons != null) {
            Button button = buttons.get(event.getRawSlot());
            if (button != null) {
                ClickAction clickAction = new ClickAction(this, event);
                button.onClick(clickAction);
            } else {
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }

    @Override
    public void createGui(Plugin plugin, HumanEntity humanEntity) {
        // TODO better logic
        GuiInfo guiInfo = new SimpleGuiInfo(this, humanEntity);
        List<D> datas = pageContent.getData(guiInfo);
        TreeMap<Integer, D> sortedData = new TreeMap<>();
        int current = 0;
        for (D data : datas) {
            OptionalInt optionalInt = pageContent.getSlot(guiInfo, data);
            int slot;
            if (optionalInt.isPresent()) {
                slot = optionalInt.getAsInt();
            } else {
                do {
                    slot = current++;
                } while (sortedData.containsKey(slot));
            }
            sortedData.put(slot, data);
        }

        int rows = this.rows.apply(setting, guiInfo);
        int upperGap = this.upperGap.apply(setting, guiInfo);
        int lowerGap = this.lowerGap.apply(setting, guiInfo);
        int sideGap = this.sideGap.apply(setting, guiInfo);

        int slotsPerPage = InventoryUtil.calculateSlots(rows - upperGap - lowerGap, sideGap);

        int size;
        if (sortedData.size() == 0) {
            size = 1;
        } else {
            size = sortedData.lastKey() + 1;
        }

        int pages = InventoryUtil.calculatePages(slotsPerPage, size);
        if (pages == 0) {
            pages = 1;
        }

        BiMap<Inventory, Integer> inventoryPages = HashBiMap.create();
        Map<Integer, Map<Integer, Button>> inventoryButtons = new LinkedHashMap<>();
        Map<Integer, Map<Integer, Integer>> inventoryDatas = new LinkedHashMap<>();
        for (int page = 0; page < pages; page++) {
            guiInfo = new PagedGuiInfo(this, humanEntity, pages, page);
            Inventory subInventory = Bukkit.createInventory(null, rows * 9, this.name.apply(setting, guiInfo));

            if (decorations.apply(setting, guiInfo)) {
                Set<String> keys = setting.getKeys(identifier, "decorations");

                if (keys == null) {
                    return;
                }

                for (String key : keys) {
                    ItemStack itemStack = setting.get(identifier, key == null ? "decorations.item-stack" : "decorations." + key + ".item-stack", new ItemStack(Material.STONE));
                    List<Integer> slots = setting.get(identifier, key == null ? "decorations.slots" : "decorations." + key + ".slots", (List<Object>) new LinkedList<>()).stream().map(NumberConversions::toInt).collect(Collectors.toList());
                    for (int slot : slots) {
                        if (slot < subInventory.getSize()) {
                            subInventory.setItem(slot, itemStack);
                        }
                    }
                }
            }

            Map<Integer, Button> newButtons = new LinkedHashMap<>();
            for (ListButton listButton : listButtons) {
                for (ButtonContext buttonContext : listButton.getButtons()) {
                    Button button = buttonContext.getButton();
                    if (buttonContext.shouldPlace(guiInfo) && button.shouldPlace(guiInfo)) {
                        int slot = buttonContext.getSlot(guiInfo);
                        ItemStack itemStack = button.getItemStack(guiInfo);

                        subInventory.setItem(slot, itemStack);
                        newButtons.put(slot, button);
                    }
                }
            }

            for (ButtonContext buttonContext : buttonContexts) {
                Button button = buttonContext.getButton();
                if (buttonContext.shouldPlace(guiInfo) && button.shouldPlace(guiInfo)) {
                    int slot = buttonContext.getSlot(guiInfo);
                    ItemStack itemStack = button.getItemStack(guiInfo);

                    subInventory.setItem(slot, itemStack);
                    newButtons.put(slot, button);
                }
            }

            Map<Integer, Integer> slotToPosition = new LinkedHashMap<>();

            for (int slot = 0; slot < slotsPerPage; slot++) {
                int position = slot + (slotsPerPage * page);
                D data = sortedData.get(position);
                if (data != null) {
                    int slotToPlace = InventoryUtil.calculateSlot(slot, sideGap) + (upperGap * 9);
                    subInventory.setItem(slotToPlace, pageContent.getItemStack(guiInfo, data));
                    slotToPosition.put(slotToPlace, position);
                }
            }

            inventoryPages.put(subInventory, page);
            inventoryButtons.put(page, newButtons);
            inventoryDatas.put(page, slotToPosition);
        }

        Inventory first = inventoryPages.inverse().get(0);

        PagedInventoryGuiData guiData = guiDatas.computeIfAbsent(humanEntity, human -> new PagedInventoryGuiData());
        guiData.inventorys.keySet().forEach(inventory -> {
            new ArrayList<>(inventory.getViewers()).forEach(viewer -> viewer.openInventory(first));
            inventoryHuman.remove(inventory);
        });

        inventoryPages.keySet().forEach(inventory -> inventoryHuman.put(inventory, humanEntity));
        guiData.allowBottomPickUp = allowBottomPickUp.apply(setting, guiInfo);
        guiData.inventorys.clear();
        guiData.inventorys.putAll(inventoryPages);
        guiData.inventoryButtons.clear();
        guiData.inventoryButtons.putAll(inventoryButtons);
        guiData.inventoryDatas.clear();
        guiData.inventoryDatas.putAll(inventoryDatas);
        guiData.sortedData.clear();
        guiData.sortedData.putAll(sortedData);

    }

    @Override
    public void updatedSoft(HumanEntity humanEntity) {
        HumanEntity owner = inventoryHuman.get(humanEntity.getOpenInventory().getTopInventory());
        if (owner == null) {
            return;
        }

        createGui(null, owner);
    }

    @Override
    public void updatedSoft() {
        for (Map.Entry<HumanEntity, PagedInventoryGuiData> entry : new HashMap<>(guiDatas).entrySet()) {
            createGui(null, entry.getKey());
        }
    }

    @Override
    public void openGui(Plugin plugin, HumanEntity humanEntity, boolean updated) {
        if (!registered) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
            registered = true;
        }

        if (!guiDatas.containsKey(humanEntity) || updated) {
            createGui(plugin, humanEntity);
        }

        humanEntity.openInventory(guiDatas.get(humanEntity).inventorys.inverse().get(0));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PagedInventoryGuiData guiData = guiDatas.remove(event.getPlayer());

        if (guiData != null) {
            guiData.inventorys.keySet().forEach(inventory -> {
                inventory.getViewers().forEach(HumanEntity::closeInventory);
                inventoryHuman.remove(inventory);
            });
        }

        previous.remove(event.getPlayer());
    }

    private final class PagedInventoryGuiData {
        private final BiMap<Inventory, Integer> inventorys = HashBiMap.create();
        private final Map<Integer, Map<Integer, Button>> inventoryButtons = new LinkedHashMap<>();
        private final Map<Integer, Map<Integer, Integer>> inventoryDatas = new LinkedHashMap<>();
        private final TreeMap<Integer, D> sortedData = new TreeMap<>();
        private boolean allowBottomPickUp;
    }

}

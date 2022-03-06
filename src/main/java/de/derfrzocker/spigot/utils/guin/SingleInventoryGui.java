package de.derfrzocker.spigot.utils.guin;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.derfrzocker.spigot.utils.guin.buttons.Button;
import de.derfrzocker.spigot.utils.guin.buttons.ButtonContext;
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
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class SingleInventoryGui implements InventoryGui, Listener {

    private final Map<HumanEntity, InventoryGui> previous = new LinkedHashMap<>();

    private final BiFunction<Setting, GuiInfo, Boolean> decorations;
    private final Setting setting;
    private final BiFunction<Setting, GuiInfo, Integer> rows;
    private final BiFunction<Setting, GuiInfo, String> name;
    private final BiFunction<Setting, GuiInfo, Boolean> allowBottomPickUp;
    private String identifier;
    private final List<ButtonContext> buttonContexts = new LinkedList<>();

    private final Map<Inventory, Map<Integer, Button>> buttons = new LinkedHashMap<>();
    private final Map<Inventory, Boolean> allowBottomPickUps = new LinkedHashMap<>();
    private final BiMap<HumanEntity, Inventory> inventorys = HashBiMap.create();

    private boolean registered = false;

    public SingleInventoryGui(String identifier, Setting setting, BiFunction<Setting, GuiInfo, Integer> rows, BiFunction<Setting, GuiInfo, String> name, BiFunction<Setting, GuiInfo, Boolean> allowBottomPickUp, BiFunction<Setting, GuiInfo, Boolean> decorations) {
        this.identifier = identifier;
        this.setting = setting;
        this.rows = rows;
        this.name = name;
        this.allowBottomPickUp = allowBottomPickUp;
        this.decorations = decorations;
    }

    public void addButtonContext(ButtonContext buttonContext) {
        buttonContexts.add(buttonContext);
    }

    @EventHandler
    @Override
    public void onClick(InventoryClickEvent event) {
        if (!inventorys.containsValue(event.getView().getTopInventory())) {
            return;
        }

        if (event.getView().getBottomInventory().equals(event.getClickedInventory())) {
            if (!allowBottomPickUps.get(event.getView().getTopInventory())) {
                event.setCancelled(true);
            }
            return;
        }

        Map<Integer, Button> buttons = this.buttons.get(event.getView().getTopInventory());

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
        GuiInfo guiInfo = new SimpleGuiInfo(this, humanEntity);
        Inventory oldInv = inventorys.get(humanEntity);
        Inventory newInv = Bukkit.createInventory(null, rows.apply(setting, guiInfo) * 9, name.apply(setting, guiInfo));

        fillInventory(newInv, humanEntity);

        if (oldInv != null) {
            new ArrayList<>(oldInv.getViewers()).forEach(viewer -> viewer.openInventory(newInv));
        }

        buttons.remove(oldInv);
        allowBottomPickUps.remove(oldInv);
    }

    @Override
    public void updatedSoft(HumanEntity humanEntity) {
        HumanEntity owner = inventorys.inverse().get(humanEntity.getOpenInventory().getTopInventory());

        if (owner == null) {
            return;
        }

        fillInventory(humanEntity.getOpenInventory().getTopInventory(), owner);
    }

    @Override
    public void updatedSoft() {
        for (Map.Entry<HumanEntity, Inventory> entry : new HashMap<>(inventorys).entrySet()) {
            fillInventory(entry.getValue(), entry.getKey());
        }
    }

    public void fillInventory(Inventory inventory, HumanEntity humanEntity) {
        GuiInfo guiInfo = new SimpleGuiInfo(this, humanEntity);
        Map<Integer, Button> newButtons = new LinkedHashMap<>();

        inventory.clear();

        if (decorations.apply(setting, guiInfo)) {
            Set<String> keys = setting.getKeys(identifier, "decorations");

            if (keys == null) {
                return;
            }

            for (String key : keys) {
                ItemStack itemStack = setting.get(identifier, key == null ? "decorations.item-stack" : "decorations." + key + ".item-stack", new ItemStack(Material.STONE));
                List<Integer> slots = setting.get(identifier, key == null ? "decorations.slots" : "decorations." + key + ".slots", (List<Object>) new LinkedList<>()).stream().map(NumberConversions::toInt).collect(Collectors.toList());
                for (int slot : slots) {
                    if (slot < inventory.getSize()) {
                        inventory.setItem(slot, itemStack);
                    }
                }
            }
        }

        for (ButtonContext buttonContext : buttonContexts) {
            Button button = buttonContext.getButton();
            if (buttonContext.shouldPlace(guiInfo) && button.shouldPlace(guiInfo)) {
                int slot = buttonContext.getSlot(guiInfo);
                ItemStack itemStack = button.getItemStack(guiInfo);

                inventory.setItem(slot, itemStack);
                newButtons.put(slot, button);
            }
        }

        inventorys.put(humanEntity, inventory);
        buttons.put(inventory, newButtons);
        allowBottomPickUps.put(inventory, allowBottomPickUp.apply(setting, guiInfo));
    }

    @Override
    public void openGui(Plugin plugin, HumanEntity humanEntity, boolean updated) {
        if (!registered) {
            plugin.getServer().getPluginManager().registerEvents(this, plugin);
            registered = true;
        }

        if (!inventorys.containsKey(humanEntity) || updated) {
            createGui(plugin, humanEntity);
        }

        humanEntity.openInventory(inventorys.get(humanEntity));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Inventory inventory = inventorys.remove(event.getPlayer());

        if (inventory != null) {
            inventory.getViewers().forEach(HumanEntity::closeInventory);
            buttons.remove(inventory);
            allowBottomPickUps.remove(inventory);
        }

        previous.remove(event.getPlayer());
    }

}

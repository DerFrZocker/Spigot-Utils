package de.derfrzocker.spigot.utils.guin;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.derfrzocker.spigot.utils.guin.buttons.Button;
import de.derfrzocker.spigot.utils.guin.buttons.ButtonContext;
import de.derfrzocker.spigot.utils.guin.settings.Setting;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class SingleInventoryGui<S extends Setting<S>> implements InventoryGui, Listener {

    private final Map<HumanEntity, InventoryGui> previous = new LinkedHashMap<>();

    private final S setting;
    private final BiFunction<S, GuiInfo, Integer> rows;
    private final BiFunction<S, GuiInfo, String> name;
    private final BiFunction<S, GuiInfo, Boolean> allowBottomPickUp;
    private final List<ButtonContext> buttonContexts = new LinkedList<>();
    private final List<Decoration> decorations = new LinkedList<>();
    private final Map<Object, InventoryGui> inventoryGuiMap = new LinkedHashMap<>();

    private final Map<Inventory, Map<Integer, Button>> buttons = new LinkedHashMap<>();
    private final Map<Inventory, Boolean> allowBottomPickUps = new LinkedHashMap<>();
    private final BiMap<HumanEntity, Inventory> inventorys = HashBiMap.create();

    private boolean registered = false;

    public SingleInventoryGui(S setting, BiFunction<S, GuiInfo, Integer> rows, BiFunction<S, GuiInfo, String> name, BiFunction<S, GuiInfo, Boolean> allowBottomPickUp) {
        this.setting = setting;
        this.rows = rows;
        this.name = name;
        this.allowBottomPickUp = allowBottomPickUp;
    }

    @Override
    public void addInventoryGui(Object identifier, InventoryGui inventoryGui) {
        inventoryGuiMap.put(identifier, inventoryGui);
    }

    public void addButtonContext(ButtonContext buttonContext) {
        buttonContexts.add(buttonContext);
    }

    public void addDecoration(Decoration decoration) {
        decorations.add(decoration);
    }

    @Override
    public InventoryGui getInventoryGui(Object identifier) {
        return inventoryGuiMap.get(identifier);
    }

    @Override
    public void setPreviousGui(HumanEntity humanEntity, InventoryGui inventoryGui) {
        previous.put(humanEntity, inventoryGui);
    }

    @Override
    public InventoryGui getPreviousGui(HumanEntity humanEntity) {
        return previous.get(humanEntity);
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

        for (Decoration decoration : decorations) {
            ItemStack itemStack = decoration.getItemStack(guiInfo);
            for (int slot : decoration.getSlots(guiInfo)) {
                if (slot < inventory.getSize()) {
                    inventory.setItem(slot, itemStack);
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

package de.derfrzocker.spigot.utils.gui;

import de.derfrzocker.spigot.utils.MessageUtil;
import de.derfrzocker.spigot.utils.MessageValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public abstract class BasicGui extends InventoryGui {

    @Getter(AccessLevel.PACKAGE)
    private final Inventory inventory;

    private final Map<Integer, Consumer<InventoryClickEvent>> button = new HashMap<>();

    private final BasicSettings basicSettings;

    public BasicGui(final @NonNull JavaPlugin plugin, final @NonNull BasicSettings basicSettings) {
        super(plugin);
        this.basicSettings = basicSettings;
        inventory = Bukkit.createInventory(null, basicSettings.getRows() * 9, MessageUtil.replacePlaceHolder(plugin, basicSettings.getInventoryName()));
    }

    public BasicGui(final @NonNull JavaPlugin plugin, final @NonNull BasicSettings basicSettings, final @NonNull MessageValue... messageValue) {
        super(plugin);
        this.basicSettings = basicSettings;
        inventory = Bukkit.createInventory(null, basicSettings.getRows() * 9, MessageUtil.replacePlaceHolder(plugin, basicSettings.getInventoryName(), messageValue));
    }

    public void addItem(final int slot, final @NonNull ItemStack itemStack) {
        getInventory().setItem(slot, itemStack);
    }

    public void addItem(final int slot, final @NonNull ItemStack itemStack, final @NonNull Consumer<InventoryClickEvent> consumer) {
        button.put(slot, consumer);
        getInventory().setItem(slot, itemStack);
    }

    @Override
    public void onClick(final @NonNull InventoryClickEvent event) {
        final Consumer<InventoryClickEvent> consumer = button.get(event.getRawSlot());

        if (consumer != null)
            consumer.accept(event);
    }
}

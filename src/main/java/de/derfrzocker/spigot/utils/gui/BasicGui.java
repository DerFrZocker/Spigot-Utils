package de.derfrzocker.spigot.utils.gui;

import de.derfrzocker.spigot.utils.message.MessageUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class BasicGui extends InventoryGui {

    @NotNull
    private final Inventory inventory;

    @NotNull
    private final Map<Integer, Consumer<InventoryClickEvent>> button = new HashMap<>();

    @NotNull
    private final BasicSettings basicSettings;

    public BasicGui(@NotNull final JavaPlugin plugin, @NotNull final BasicSettings basicSettings) {
        super(plugin);

        Validate.notNull(basicSettings, "BasicSettings can not be null");

        this.basicSettings = basicSettings;
        inventory = Bukkit.createInventory(null, basicSettings.getRows() * 9, MessageUtil.replacePlaceHolder(plugin, basicSettings.getInventoryName()));
    }

    public BasicGui(@NotNull final JavaPlugin plugin, @NotNull final BasicSettings basicSettings, @NotNull final MessageValue... messageValue) {
        super(plugin);

        Validate.notNull(basicSettings, "BasicSettings can not be null");

        this.basicSettings = basicSettings;
        inventory = Bukkit.createInventory(null, basicSettings.getRows() * 9, MessageUtil.replacePlaceHolder(plugin, basicSettings.getInventoryName(), messageValue));
    }

    public void addItem(final int slot, @NotNull final ItemStack itemStack) {
        Validate.notNull(itemStack, "ItemStack can not be null");

        getInventory().setItem(slot, itemStack);
    }

    public void addItem(final int slot, @NotNull final ItemStack itemStack, @NotNull final Consumer<InventoryClickEvent> consumer) {
        Validate.notNull(itemStack, "ItemStack can not be null");
        Validate.notNull(consumer, "Consumer can not be null");

        button.put(slot, consumer);
        getInventory().setItem(slot, itemStack);
    }

    /**
     * Adds the decoration item stack from the basic settings to the inventory
     *
     * @param messageValues to use on the item stacks
     */
    public void addDecorations(@NotNull final MessageValue... messageValues) {
        basicSettings.getDecorations().forEach(pair -> {
            final ItemStack itemStack = MessageUtil.replaceItemStack(getPlugin(), pair.getSecond(), messageValues);
            pair.getFirst().forEach(integer -> addItem(integer, itemStack));
        });
    }

    @NotNull
    @Override
    Inventory getInventory() {
        return inventory;
    }

    @Override
    public void onClick(@NotNull final InventoryClickEvent event) {
        final Consumer<InventoryClickEvent> consumer = button.get(event.getRawSlot());

        if (consumer != null)
            consumer.accept(event);
    }

}

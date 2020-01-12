package de.derfrzocker.spigot.utils.gui;

import de.derfrzocker.spigot.utils.message.MessageUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;

public abstract class PageGui<T> extends InventoryGui {

    private final Map<Integer, SubPageGui> guis = new HashMap<>();
    private final Map<Integer, Consumer<InventoryClickEvent>> button = new HashMap<>();
    private final PageSettings pageSettings;
    private BiConsumer<T, InventoryClickEvent> eventBiConsumer;
    private Function<T, ItemStack> itemStackFunction;
    private int pages;
    private int nextPage;
    private int previousPage;
    private boolean init = false;

    public PageGui(@NotNull final JavaPlugin plugin, @NotNull final PageSettings pageSettings) {
        super(plugin);

        Validate.notNull(pageSettings, "PageSettings can not be null");

        this.pageSettings = pageSettings;
    }

    public void init(@NotNull final T[] values, @NotNull final IntFunction<T[]> function, @NotNull final Function<T, ItemStack> itemStackFunction, @NotNull final BiConsumer<T, InventoryClickEvent> eventBiConsumer) {
        if (this.init)
            return;

        this.init = true;

        Validate.notNull(values, "Values can not be null");
        Validate.notNull(function, "IntFunction can not be null");
        Validate.notNull(itemStackFunction, "Function for ItemStack can not be null");
        Validate.notNull(eventBiConsumer, "BiConsumer can not be null");

        this.eventBiConsumer = eventBiConsumer;
        this.itemStackFunction = itemStackFunction;
        this.nextPage = pageSettings.getNextPageSlot();
        this.previousPage = pageSettings.getPreviousPageSlot();

        final int slots = InventoryUtil.calculateSlots(pageSettings.getRows() - pageSettings.getEmptyRowsBelow() - pageSettings.getEmptyRowsUp(), pageSettings.getGap());

        this.pages = InventoryUtil.calculatePages(slots, values.length);
        for (int i = 0; i < pages; i++) {
            final T[] subValues;

            if (i == pages - 1)
                subValues = function.apply(values.length - i * slots);
            else
                subValues = function.apply(slots);

            System.arraycopy(values, i * slots, subValues, 0, subValues.length);

            guis.put(i, new SubPageGui(subValues, i));
        }
    }

    public void addItem(final int slot, @NotNull final ItemStack itemStack, @NotNull final Consumer<InventoryClickEvent> consumer) {
        button.put(slot, consumer);
        guis.forEach((key, value) -> value.getInventory().setItem(slot, itemStack));
    }

    public void addItem(final int slot, @NotNull final ItemStack itemStack) {
        guis.forEach((key, value) -> value.getInventory().setItem(slot, itemStack));
    }

    /**
     * @param value     to update
     * @param itemStack to set
     * @return true if success, false if not
     */
    public boolean updateItemStack(@NotNull final T value, @NotNull final ItemStack itemStack) {
        boolean update = false;

        for (final SubPageGui subPageGui : guis.values()) {
            if (subPageGui.updateItemStack(value, itemStack)) {
                update = true;
            }
        }

        return update;
    }

    /**
     * Adds the decoration item stack from the basic settings to the inventory
     *
     * @param messageValues to use on the item stacks
     */
    public void addDecorations(@NotNull final MessageValue... messageValues) {
        pageSettings.getDecorations().forEach(pair -> {
            final ItemStack itemStack = MessageUtil.replaceItemStack(getPlugin(), pair.getSecond(), messageValues);
            pair.getFirst().forEach(integer -> addItem(integer, itemStack));
        });
    }

    @Override
    public void onClick(@NotNull final InventoryClickEvent event) {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    Inventory getInventory() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void openSync(@NotNull final HumanEntity entity) {
        Validate.notNull(entity, "Entity can not be null");

        final SubPageGui subPageGui = guis.get(0);
        if (Bukkit.isPrimaryThread()) {
            InventoryGuiManager.getInventoryGuiManager(getPlugin()).registerInventoryGui(subPageGui);
            entity.openInventory(subPageGui.getInventory());
            return;
        }

        try {
            Bukkit.getScheduler().callSyncMethod(getPlugin(), () -> {
                InventoryGuiManager.getInventoryGuiManager(getPlugin()).registerInventoryGui(subPageGui);
                return entity.openInventory(subPageGui.getInventory());
            }).get();
        } catch (final InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error while open inventory Sync!", e);
        }
    }

    private final class SubPageGui extends InventoryGui {

        @NotNull
        private final Inventory inventory;
        private final int page;
        private final Map<Integer, T> values = new HashMap<>();

        private SubPageGui(@NotNull final T[] values, final int page) {
            super(PageGui.super.getPlugin());
            this.page = page;

            final MessageValue[] messageValues = new MessageValue[]{new MessageValue("page", String.valueOf(page)), new MessageValue("pages", String.valueOf(pages))};

            this.inventory = Bukkit.createInventory(null, pageSettings.getRows() * 9,
                    MessageUtil.replacePlaceHolder(getPlugin(), pageSettings.getInventoryName(), messageValues));

            if (page + 1 != pages)
                inventory.setItem(nextPage, MessageUtil.replaceItemStack(getPlugin(), pageSettings.getNextPageItemStack(), messageValues));

            if (page != 0)
                inventory.setItem(previousPage, MessageUtil.replaceItemStack(getPlugin(), pageSettings.getPreviousPageItemStack(), messageValues));

            for (int i = 0; i < values.length; i++) {
                final T value = values[i];

                final int slot = InventoryUtil.calculateSlot(i, pageSettings.getGap()) + (pageSettings.getEmptyRowsUp() * 9);

                inventory.setItem(slot, itemStackFunction.apply(value));

                this.values.put(slot, value);
            }

        }

        public boolean updateItemStack(@NotNull final T value, @NotNull final ItemStack itemStack) {
            boolean update = false;

            for (final Map.Entry<Integer, T> entry : values.entrySet()) {
                if (entry.getValue().equals(value)) {
                    inventory.setItem(entry.getKey(), itemStack);
                    update = true;
                }
            }

            return update;
        }

        @NotNull
        @Override
        Inventory getInventory() {
            return inventory;
        }

        @Override
        public void onClick(@NotNull final InventoryClickEvent event) {
            if (event.getRawSlot() == previousPage && page != 0) {
                guis.get(page - 1).openSync(event.getWhoClicked());
                return;
            }

            if (event.getRawSlot() == nextPage && page + 1 != pages) {
                guis.get(page + 1).openSync(event.getWhoClicked());
                return;
            }

            final Consumer<InventoryClickEvent> consumer = button.get(event.getRawSlot());

            if (consumer != null) {
                consumer.accept(event);
                return;
            }

            final T value = values.get(event.getRawSlot());

            if (value == null)
                return;

            eventBiConsumer.accept(value, event);
        }

    }

}

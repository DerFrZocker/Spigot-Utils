package de.derfrzocker.spigot.utils.gui;

import de.derfrzocker.spigot.utils.MessageUtil;
import de.derfrzocker.spigot.utils.MessageValue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

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

    private BiConsumer<T, InventoryClickEvent> eventBiConsumer;

    private Function<T, ItemStack> itemStackFunction;

    private PageSettings pageSettings;

    private int pages;

    private int nextPage;

    private int previousPage;

    private boolean init = false;

    public PageGui(JavaPlugin plugin) {
        super(plugin);
    }

    public void init(final T[] values, final IntFunction<T[]> function, final PageSettings pageSettings, final Function<T, ItemStack> itemStackFunction, BiConsumer<T, InventoryClickEvent> eventBiConsumer) {
        if (this.init)
            return;

        this.init = true;

        this.eventBiConsumer = eventBiConsumer;
        this.itemStackFunction = itemStackFunction;
        this.pageSettings = pageSettings;
        this.nextPage = pageSettings.getNextPageSlot();
        this.previousPage = pageSettings.getPreviousPageSlot();

        final int slots = InventoryUtil.calculateSlots(pageSettings.getRows() - pageSettings.getEmptyRows(), pageSettings.getGap());

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

    public void addItem(final int slot, final @NonNull ItemStack itemStack, final @NonNull Consumer<InventoryClickEvent> consumer) {
        button.put(slot, consumer);
        guis.forEach((key, value) -> value.getInventory().setItem(slot, itemStack));
    }

    public void addItem(final int slot, final @NonNull ItemStack itemStack) {
        guis.forEach((key, value) -> value.getInventory().setItem(slot, itemStack));
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        throw new UnsupportedOperationException();
    }

    @Override
    Inventory getInventory() {
        guis.values().toArray(new Object[0]);
        throw new UnsupportedOperationException();
    }

    @Override
    public void openSync(HumanEntity entity) {
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
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error while open inventory Sync!", e);
        }
    }

    private final class SubPageGui extends InventoryGui {

        @Getter(AccessLevel.PACKAGE)
        private final Inventory inventory;

        private final int page;

        private final Map<Integer, T> values = new HashMap<>();

        private SubPageGui(final T[] values, final int page) {
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

                final int slot = InventoryUtil.calculateSlot(i, pageSettings.getGap());

                inventory.setItem(slot, itemStackFunction.apply(value));

                this.values.put(slot, value);
            }

        }

        @Override
        public void onClick(final InventoryClickEvent event) {
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

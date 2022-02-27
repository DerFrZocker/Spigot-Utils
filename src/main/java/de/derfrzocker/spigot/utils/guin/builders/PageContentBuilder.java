package de.derfrzocker.spigot.utils.guin.builders;

import de.derfrzocker.spigot.utils.TripleFunction;
import de.derfrzocker.spigot.utils.TriplePredicate;
import de.derfrzocker.spigot.utils.guin.ClickAction;
import de.derfrzocker.spigot.utils.guin.GuiInfo;
import de.derfrzocker.spigot.utils.guin.buttons.PageContent;
import de.derfrzocker.spigot.utils.guin.buttons.SimplePageContent;
import de.derfrzocker.spigot.utils.setting.Setting;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

public final class PageContentBuilder<D> {

    private final List<BiConsumer<GuiBuilder, PageContentData>> records = new LinkedList<>();

    private PageContentBuilder() {
    }

    public static <D> PageContentBuilder<D> builder(Class<D> dClass) {
        return new PageContentBuilder<>();
    }

    public PageContentBuilder<D> withSetting(Setting setting) {
        records.add((parent, data) -> data.setting = data.setting.withSetting(setting));
        return this;
    }

    public PageContentBuilder<D> data(List<D> data) {
        data((setting, humanEntity) -> data);
        return this;
    }

    public PageContentBuilder<D> data(BiFunction<Setting, GuiInfo, List<D>> dataFunction) {
        records.add((parent, data) -> data.dataFunction = dataFunction);
        return this;
    }

    public PageContentBuilder<D> itemStack(Function<D, ItemStack> itemStackFunction) {
        itemStack((setting, humanEntity, data) -> itemStackFunction.apply(data));
        return this;
    }

    public PageContentBuilder<D> itemStack(TripleFunction<Setting, GuiInfo, D, ItemStack> itemStackFunction) {
        records.add((parent, data) -> data.itemStackFunction = itemStackFunction);
        return this;
    }

    public PageContentBuilder<D> slot(Function<D, OptionalInt> slotFunction) {
        slot((setting, humanEntity, data) -> slotFunction.apply(data));
        return this;
    }

    public PageContentBuilder<D> slot(TripleFunction<Setting, GuiInfo, D, OptionalInt> slotFunction) {
        records.add((parent, data) -> data.slotFunction = slotFunction);
        return this;
    }

    public PageContentBuilder<D> withAction(BiConsumer<ClickAction, D> consumer) {
        records.add((gui, data) -> data.actions.add(consumer));
        return this;
    }

    public PageContentBuilder<D> withPermission(Function<D, String> permission) {
        withPermission((setting, data) -> permission.apply(data));
        return this;
    }

    public PageContentBuilder<D> withPermission(BiFunction<Setting, D, String> permissionFunction) {
        withCondition((setting, guiInfo, data) -> guiInfo.getEntity().hasPermission(permissionFunction.apply(setting, data)));
        return this;
    }

    public PageContentBuilder<D> withCondition(BiPredicate<GuiInfo, D> predicate) {
        withCondition((setting, guiInfo, data) -> predicate.test(guiInfo, data));
        return this;
    }

    public PageContentBuilder<D> withCondition(TriplePredicate<Setting, GuiInfo, D> predicate) {
        records.add((gui, data) -> data.conditions.add(predicate));
        return this;
    }

    public PageContentBuilder<D> withClickType(ClickType clickType) {
        withClickType(setting -> clickType);
        return this;
    }


    public PageContentBuilder<D> withClickType(Function<Setting, ClickType> clickType) {
        records.add((gui, data) -> data.clickTypes.add(clickType));
        return this;
    }

    PageContent<D> build(GuiBuilder parent) {
        PageContentData data = new PageContentData();

        parent.copy(data);

        for (BiConsumer<GuiBuilder, PageContentData> consumer : records) {
            consumer.accept(parent, data);
        }

        return data.build();
    }

    private final class PageContentData extends GuiBuilder {
        protected final List<BiConsumer<ClickAction, D>> actions = new LinkedList<>();
        protected final List<TriplePredicate<Setting, GuiInfo, D>> conditions = new LinkedList<>();
        private final List<Function<Setting, ClickType>> clickTypes = new LinkedList<>();
        protected BiFunction<Setting, GuiInfo, List<D>> dataFunction;
        protected TripleFunction<Setting, GuiInfo, D, ItemStack> itemStackFunction;
        protected TripleFunction<Setting, GuiInfo, D, OptionalInt> slotFunction;

        PageContent<D> build() {
            TripleFunction<Setting, GuiInfo, D, ItemStack> itemStackFunction = this.itemStackFunction;
            TripleFunction<Setting, GuiInfo, D, OptionalInt> slotFunction = this.slotFunction;
            BiFunction<Setting, GuiInfo, List<D>> dataFunction = this.dataFunction;

            if (loadMissingFromConfig) {
                if (itemStackFunction == null) {
                    itemStackFunction = (setting, guiInfo, data) -> setting.get(data, "item-stack", new ItemStack(Material.STONE));
                }

                if (slotFunction == null) {
                    slotFunction = (setting, guiInfo, data) -> {
                        Integer slot = setting.get(data, "slot", null);
                        if (slot == null) {
                            return OptionalInt.empty();
                        } else {
                            return OptionalInt.of(slot);
                        }
                    };
                }

                if (dataFunction == null) {
                    dataFunction = (setting, guiInfo) -> new ArrayList<>();
                }

                if (clickTypes.isEmpty()) {
                    clickTypes.add(setting -> ClickType.LEFT);
                }
            }

            return new SimplePageContent<>(setting, dataFunction, itemStackFunction, slotFunction, actions, conditions, clickTypes);
        }
    }

}

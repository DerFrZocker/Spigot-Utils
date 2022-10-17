package de.derfrzocker.spigot.utils.gui.builders;

import de.derfrzocker.spigot.utils.function.TripleFunction;
import de.derfrzocker.spigot.utils.function.TriplePredicate;
import de.derfrzocker.spigot.utils.gui.ClickAction;
import de.derfrzocker.spigot.utils.gui.GuiInfo;
import de.derfrzocker.spigot.utils.gui.buttons.PageContent;
import de.derfrzocker.spigot.utils.gui.buttons.SimplePageContent;
import de.derfrzocker.spigot.utils.language.LanguageManager;
import de.derfrzocker.spigot.utils.message.MessageValue;
import de.derfrzocker.spigot.utils.setting.Setting;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

public final class PageContentBuilder<D> extends GuiBuilder {

    private final List<BiConsumer<ClickAction, D>> actions = new LinkedList<>();
    private final List<TriplePredicate<Setting, GuiInfo, D>> conditions = new LinkedList<>();
    private BiFunction<Setting, GuiInfo, List<D>> dataFunction;
    private TripleFunction<Setting, GuiInfo, D, ItemStack> itemStackFunction;
    private TripleFunction<Setting, GuiInfo, D, OptionalInt> slotFunction;
    private final List<TripleFunction<Setting, GuiInfo, D, MessageValue>> messageValues = new LinkedList<>();

    private PageContentBuilder() {
    }

    public static <D> PageContentBuilder<D> builder(Class<D> dClass) {
        return new PageContentBuilder<>();
    }

    public PageContentBuilder<D> withSetting(Setting setting) {
        this.setting = this.setting.withSetting(setting);
        return this;
    }

    public PageContentBuilder<D> data(List<D> data) {
        data((setting, humanEntity) -> data);
        return this;
    }

    public PageContentBuilder<D> data(BiFunction<Setting, GuiInfo, List<D>> dataFunction) {
        this.dataFunction = dataFunction;
        return this;
    }

    public PageContentBuilder<D> itemStack(Function<D, ItemStack> itemStackFunction) {
        itemStack((setting, humanEntity, data) -> itemStackFunction.apply(data));
        return this;
    }

    public PageContentBuilder<D> itemStack(TripleFunction<Setting, GuiInfo, D, ItemStack> itemStackFunction) {
        this.itemStackFunction = itemStackFunction;
        return this;
    }

    public PageContentBuilder<D> slot(Function<D, OptionalInt> slotFunction) {
        slot((setting, humanEntity, data) -> slotFunction.apply(data));
        return this;
    }

    public PageContentBuilder<D> slot(TripleFunction<Setting, GuiInfo, D, OptionalInt> slotFunction) {
        this.slotFunction = slotFunction;
        return this;
    }

    public PageContentBuilder<D> withAction(BiConsumer<ClickAction, D> consumer) {
        actions.add(consumer);
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
        conditions.add(predicate);
        return this;
    }

    public PageContentBuilder<D> withMessageValue(TripleFunction<Setting, GuiInfo, D, MessageValue> messageValue) {
        messageValues.add(messageValue);
        return this;
    }

    PageContent<D> build(Setting parent, LanguageManager languageManager) {
        BiFunction<Setting, GuiInfo, List<D>> dataFunction = this.dataFunction;
        TripleFunction<Setting, GuiInfo, D, ItemStack> itemStackFunction = this.itemStackFunction;
        TripleFunction<Setting, GuiInfo, D, OptionalInt> slotFunction = this.slotFunction;
        parent = parent.withSetting(setting);

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

        return new SimplePageContent<>(parent, languageManager, dataFunction, itemStackFunction, slotFunction, actions, conditions, messageValues);
    }
}

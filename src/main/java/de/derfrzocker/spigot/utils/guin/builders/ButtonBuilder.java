package de.derfrzocker.spigot.utils.guin.builders;

import de.derfrzocker.spigot.utils.guin.ClickAction;
import de.derfrzocker.spigot.utils.guin.GuiInfo;
import de.derfrzocker.spigot.utils.guin.buttons.Button;
import de.derfrzocker.spigot.utils.guin.buttons.SimpleButton;
import de.derfrzocker.spigot.utils.guin.settings.Setting;
import org.bukkit.Material;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class ButtonBuilder {

    private final List<BiConsumer<GuiBuilder, ButtonBuilderData>> records = new LinkedList<>();
    protected Object identifier;

    private ButtonBuilder() {
    }

    public static ButtonBuilder builder() {
        return new ButtonBuilder();
    }

    public ButtonBuilder withSetting(Setting setting) {
        records.add((parent, data) -> data.setting = data.setting.withSetting(setting));
        return this;
    }

    public ButtonBuilder itemStack(ItemStack itemStack) {
        itemStack((setting, guiInfo) -> itemStack);
        return this;
    }

    public ButtonBuilder itemStack(BiFunction<Setting, GuiInfo, ItemStack> itemStackFunction) {
        records.add((gui, data) -> data.itemStackFunction = itemStackFunction);
        return this;
    }

    public ButtonBuilder identifier(Object identifier) {
        this.identifier = identifier;
        return this;
    }

    public ButtonBuilder withAction(Consumer<ClickAction> consumer) {
        records.add((gui, data) -> data.actions.add(consumer));
        return this;
    }

    public ButtonBuilder withPermission(String permission) {
        withPermission(s -> permission);
        return this;
    }

    public ButtonBuilder withPermission(Function<Setting, String> permissionFunction) {
        withCondition((setting, guiInfo) -> guiInfo.getEntity().hasPermission(permissionFunction.apply(setting)));
        return this;
    }

    public ButtonBuilder withCondition(Predicate<GuiInfo> predicate) {
        withCondition((setting, guiInfo) -> predicate.test(guiInfo));
        return this;
    }

    public ButtonBuilder withCondition(BiPredicate<Setting, GuiInfo> predicate) {
        records.add((gui, data) -> data.conditions.add(predicate));
        return this;
    }

    public ButtonBuilder withClickType(ClickType clickType) {
        withClickType(setting -> clickType);
        return this;
    }


    public ButtonBuilder withClickType(Function<Setting, ClickType> clickType) {
        records.add((gui, data) -> data.clickTypes.add(clickType));
        return this;
    }

    Button build(GuiBuilder parent) {
        ButtonBuilderData data = new ButtonBuilderData();

        parent.copy(data);

        for (BiConsumer<GuiBuilder, ButtonBuilderData> consumer : records) {
            consumer.accept(parent, data);
        }

        return data.build();
    }

    private final class ButtonBuilderData extends GuiBuilder {
        private final List<Consumer<ClickAction>> actions = new LinkedList<>();
        private final List<BiPredicate<Setting, GuiInfo>> conditions = new LinkedList<>();
        private final List<Function<Setting, ClickType>> clickTypes = new LinkedList<>();
        private BiFunction<Setting, GuiInfo, ItemStack> itemStackFunction;

        Button build() {
            Object identifier = ButtonBuilder.this.identifier;
            BiFunction<Setting, GuiInfo, ItemStack> itemStackFunction = this.itemStackFunction;

            if (loadMissingFromConfig) {
                if (itemStackFunction == null) {
                    itemStackFunction = (setting, guiInfo) -> setting.get(identifier, "item-stack", new ItemStack(Material.STONE));
                }

                if (clickTypes.isEmpty()) {
                    clickTypes.add(setting -> ClickType.LEFT);
                }
            }

            return new SimpleButton(setting, itemStackFunction, actions, conditions, clickTypes);
        }
    }

}

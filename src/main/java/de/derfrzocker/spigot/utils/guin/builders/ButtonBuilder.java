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

public final class ButtonBuilder<S extends Setting<S>> {

    private final List<BiConsumer<GuiBuilder<?>, ButtonBuilderData>> records = new LinkedList<>();
    private final BiFunction<Setting<?>, S, S> settingSFunction;
    protected Object identifier;

    private ButtonBuilder(BiFunction<Setting<?>, S, S> settingSFunction) {
        this.settingSFunction = settingSFunction;
    }

    public static <S extends Setting<S>> ButtonBuilder<S> builder(BiFunction<Setting<?>, S, S> settingSFunction) {
        return new ButtonBuilder<>(settingSFunction);
    }

    public ButtonBuilder<S> withSetting(Setting<?> setting) {
        records.add((parent, data) -> data.setting = settingSFunction.apply(setting, data.setting));
        return this;
    }

    public ButtonBuilder<S> itemStack(ItemStack itemStack) {
        itemStack((setting, guiInfo) -> itemStack);
        return this;
    }

    public ButtonBuilder<S> itemStack(BiFunction<S, GuiInfo, ItemStack> itemStackFunction) {
        records.add((gui, data) -> data.itemStackFunction = itemStackFunction);
        return this;
    }

    public ButtonBuilder<S> identifier(Object identifier) {
        this.identifier = identifier;
        return this;
    }

    public ButtonBuilder<S> withAction(Consumer<ClickAction> consumer) {
        records.add((gui, data) -> data.actions.add(consumer));
        return this;
    }

    public ButtonBuilder<S> withPermission(String permission) {
        withPermission(s -> permission);
        return this;
    }

    public ButtonBuilder<S> withPermission(Function<S, String> permissionFunction) {
        withCondition((setting, guiInfo) -> guiInfo.getEntity().hasPermission(permissionFunction.apply(setting)));
        return this;
    }

    public ButtonBuilder<S> withCondition(Predicate<GuiInfo> predicate) {
        withCondition((setting, guiInfo) -> predicate.test(guiInfo));
        return this;
    }

    public ButtonBuilder<S> withCondition(BiPredicate<S, GuiInfo> predicate) {
        records.add((gui, data) -> data.conditions.add(predicate));
        return this;
    }

    public ButtonBuilder<S> withClickType(ClickType clickType) {
        withClickType(setting -> clickType);
        return this;
    }


    public ButtonBuilder<S> withClickType(Function<S, ClickType> clickType) {
        records.add((gui, data) -> data.clickTypes.add(clickType));
        return this;
    }

    Button build(GuiBuilder<?> parent) {
        ButtonBuilderData data = new ButtonBuilderData();

        parent.copy(data, settingSFunction);

        for (BiConsumer<GuiBuilder<?>, ButtonBuilderData> consumer : records) {
            consumer.accept(parent, data);
        }

        return data.build();
    }

    private final class ButtonBuilderData extends GuiBuilder<S> {
        private final List<Consumer<ClickAction>> actions = new LinkedList<>();
        private final List<BiPredicate<S, GuiInfo>> conditions = new LinkedList<>();
        private final List<Function<S, ClickType>> clickTypes = new LinkedList<>();
        private BiFunction<S, GuiInfo, ItemStack> itemStackFunction;

        Button build() {
            Object identifier = ButtonBuilder.this.identifier;
            BiFunction<S, GuiInfo, ItemStack> itemStackFunction = this.itemStackFunction;

            if (loadMissingFromConfig) {
                if (itemStackFunction == null) {
                    itemStackFunction = (setting, guiInfo) -> setting.get(identifier, "item-stack", new ItemStack(Material.STONE));
                }

                if (clickTypes.isEmpty()) {
                    clickTypes.add(setting -> ClickType.LEFT);
                }
            }

            return new SimpleButton<>(setting, itemStackFunction, actions, conditions, clickTypes);
        }
    }

}

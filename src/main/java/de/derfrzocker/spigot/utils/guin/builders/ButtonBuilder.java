package de.derfrzocker.spigot.utils.guin.builders;

import de.derfrzocker.spigot.utils.guin.ClickAction;
import de.derfrzocker.spigot.utils.guin.GuiInfo;
import de.derfrzocker.spigot.utils.guin.buttons.Button;
import de.derfrzocker.spigot.utils.guin.buttons.SimpleButton;
import de.derfrzocker.spigot.utils.setting.Setting;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class ButtonBuilder extends GuiBuilder {

    private final List<Consumer<ClickAction>> actions = new LinkedList<>();
    private final List<BiPredicate<Setting, GuiInfo>> conditions = new LinkedList<>();
    private BiFunction<Setting, GuiInfo, ItemStack> itemStackFunction;

    private ButtonBuilder() {
    }

    public static ButtonBuilder builder() {
        return new ButtonBuilder();
    }

    public ButtonBuilder withSetting(Setting setting) {
        this.setting = this.setting.withSetting(setting);
        return this;
    }

    public ButtonBuilder itemStack(ItemStack itemStack) {
        itemStack((setting, guiInfo) -> itemStack);
        return this;
    }

    public ButtonBuilder itemStack(BiFunction<Setting, GuiInfo, ItemStack> itemStackFunction) {
        this.itemStackFunction = itemStackFunction;
        return this;
    }

    public ButtonBuilder identifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public ButtonBuilder withAction(Consumer<ClickAction> consumer) {
        actions.add(consumer);
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
        conditions.add(predicate);
        return this;
    }

    Button build(Setting parent) {
        BiFunction<Setting, GuiInfo, ItemStack> itemStackFunction = this.itemStackFunction;
        String identifier = this.identifier;
        parent = parent.withSetting(setting);

        if (itemStackFunction == null) {
            itemStackFunction = (setting, guiInfo) -> setting.get(identifier, "item-stack", new ItemStack(Material.STONE));
        }

        return new SimpleButton(parent, itemStackFunction, actions, conditions);
    }
}

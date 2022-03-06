package de.derfrzocker.spigot.utils.guin.buttons;

import de.derfrzocker.spigot.utils.guin.ClickAction;
import de.derfrzocker.spigot.utils.guin.GuiInfo;
import de.derfrzocker.spigot.utils.setting.Setting;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class SimpleButton implements Button {

    private final Setting setting;
    private final BiFunction<Setting, GuiInfo, ItemStack> itemStackFunction;
    private final List<Consumer<ClickAction>> actions = new LinkedList<>();
    private final List<BiPredicate<Setting, GuiInfo>> conditions = new LinkedList<>();

    public SimpleButton(Setting setting, BiFunction<Setting, GuiInfo, ItemStack> itemStackFunction, List<Consumer<ClickAction>> actions, List<BiPredicate<Setting, GuiInfo>> conditions) {
        this.setting = setting;
        this.itemStackFunction = itemStackFunction;
        this.actions.addAll(actions);
        this.conditions.addAll(conditions);
    }

    @Override
    public ItemStack getItemStack(GuiInfo guiInfo) {
        return itemStackFunction.apply(setting, guiInfo);
    }

    @Override
    public void onClick(ClickAction event) {
        ClickType clickType = event.getClickEvent().getClick();
        if (clickType == ClickType.LEFT) {
            actions.forEach(consumer -> consumer.accept(event));
        } else {
            event.getClickEvent().setCancelled(true);
        }
    }

    @Override
    public boolean shouldPlace(GuiInfo guiInfo) {
        return conditions.stream().map(tester -> tester.test(setting, guiInfo)).filter(aBoolean -> !aBoolean).findAny().orElse(true);
    }
}

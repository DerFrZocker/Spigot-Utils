package de.derfrzocker.spigot.utils.guin.buttons;

import de.derfrzocker.spigot.utils.TripleFunction;
import de.derfrzocker.spigot.utils.TriplePredicate;
import de.derfrzocker.spigot.utils.guin.ClickAction;
import de.derfrzocker.spigot.utils.guin.GuiInfo;
import de.derfrzocker.spigot.utils.setting.Setting;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SimplePageContent<D> implements PageContent<D> {

    private final Setting setting;
    private final BiFunction<Setting, GuiInfo, List<D>> dataFunction;
    private final TripleFunction<Setting, GuiInfo, D, ItemStack> itemStackFunction;
    private final TripleFunction<Setting, GuiInfo, D, OptionalInt> slotFunction;
    private final List<BiConsumer<ClickAction, D>> actions = new LinkedList<>();
    private final List<TriplePredicate<Setting, GuiInfo, D>> conditions = new LinkedList<>();
    private final List<Function<Setting, ClickType>> clickTypes = new LinkedList<>();

    public SimplePageContent(Setting setting, BiFunction<Setting, GuiInfo, List<D>> dataFunction, TripleFunction<Setting, GuiInfo, D, ItemStack> itemStackFunction, TripleFunction<Setting, GuiInfo, D, OptionalInt> slotFunction, List<BiConsumer<ClickAction, D>> actions, List<TriplePredicate<Setting, GuiInfo, D>> conditions, List<Function<Setting, ClickType>> clickTypes) {
        this.setting = setting;
        this.dataFunction = dataFunction;
        this.itemStackFunction = itemStackFunction;
        this.slotFunction = slotFunction;
        this.actions.addAll(actions);
        this.conditions.addAll(conditions);
        this.clickTypes.addAll(clickTypes);
    }

    @Override
    public List<D> getData(GuiInfo guiInfo) {
        return dataFunction.apply(setting, guiInfo);
    }

    @Override
    public boolean shouldPlace(GuiInfo guiInfo, D data) {
        return conditions.stream().map(tester -> tester.test(setting, guiInfo, data)).filter(aBoolean -> !aBoolean).findAny().orElse(true);
    }

    @Override
    public ItemStack getItemStack(GuiInfo guiInfo, D data) {
        return itemStackFunction.apply(setting, guiInfo, data);
    }

    @Override
    public OptionalInt getSlot(GuiInfo guiInfo, D data) {
        return slotFunction.apply(setting, guiInfo, data);
    }

    @Override
    public void onClick(ClickAction event, D data) {
        ClickType clickType = event.getClickEvent().getClick();
        if (clickTypes.stream().map(clickFunction -> clickFunction.apply(setting)).anyMatch(clickType::equals)) {
            actions.forEach(consumer -> consumer.accept(event, data));
        } else {
            event.getClickEvent().setCancelled(true);
        }
    }
}

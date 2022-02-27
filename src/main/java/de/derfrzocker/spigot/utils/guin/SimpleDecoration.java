package de.derfrzocker.spigot.utils.guin;

import de.derfrzocker.spigot.utils.guin.settings.Setting;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class SimpleDecoration implements Decoration {

    private final Setting setting;
    private final BiFunction<Setting, GuiInfo, ItemStack> itemStackFunction;
    private final List<BiFunction<Setting, GuiInfo, List<Integer>>> slots = new LinkedList<>();

    public SimpleDecoration(Setting setting, BiFunction<Setting, GuiInfo, ItemStack> itemStackFunction, List<BiFunction<Setting, GuiInfo, List<Integer>>> slots) {
        this.setting = setting;
        this.itemStackFunction = itemStackFunction;
        this.slots.addAll(slots);
    }

    @Override
    public List<Integer> getSlots(GuiInfo guiInfo) {
        return slots.stream().map(function -> function.apply(setting, guiInfo)).flatMap(Collection::stream).collect(Collectors.toList());
    }

    @Override
    public ItemStack getItemStack(GuiInfo guiInfo) {
        return itemStackFunction.apply(setting, guiInfo);
    }
}

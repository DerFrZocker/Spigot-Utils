package de.derfrzocker.spigot.utils.guin;

import de.derfrzocker.spigot.utils.guin.settings.Setting;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class SimpleDecoration<S extends Setting<S>> implements Decoration {

    private final S setting;
    private final BiFunction<S, GuiInfo, ItemStack> itemStackFunction;
    private final List<BiFunction<S, GuiInfo, List<Integer>>> slots = new LinkedList<>();

    public SimpleDecoration(S setting, BiFunction<S, GuiInfo, ItemStack> itemStackFunction, List<BiFunction<S, GuiInfo, List<Integer>>> slots) {
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

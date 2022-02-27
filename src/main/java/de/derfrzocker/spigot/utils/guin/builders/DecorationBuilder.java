package de.derfrzocker.spigot.utils.guin.builders;

import de.derfrzocker.spigot.utils.guin.Decoration;
import de.derfrzocker.spigot.utils.guin.GuiInfo;
import de.derfrzocker.spigot.utils.guin.SimpleDecoration;
import de.derfrzocker.spigot.utils.setting.Setting;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class DecorationBuilder {

    private final List<BiConsumer<GuiBuilder, DecorationData>> records = new LinkedList<>();
    protected Object identifier;
    protected Object key;

    private DecorationBuilder() {
    }

    public static DecorationBuilder builder() {
        return new DecorationBuilder();
    }

    public DecorationBuilder withSetting(Setting setting) {
        records.add((parent, data) -> data.setting = data.setting.withSetting(setting));
        return this;
    }

    public DecorationBuilder itemStack(ItemStack itemStack) {
        itemStack((setting, guiInfo) -> itemStack);
        return this;
    }

    public DecorationBuilder itemStack(BiFunction<Setting, GuiInfo, ItemStack> itemStackFunction) {
        records.add((gui, data) -> data.itemStackFunction = itemStackFunction);
        return this;
    }

    public DecorationBuilder identifier(Object identifier) {
        this.identifier = identifier;
        return this;
    }

    public DecorationBuilder key(Object key) {
        this.key = key;
        return this;
    }

    public DecorationBuilder withSlot(int slot) {
        withSlot((setting, guiInfo) -> slot);
        return this;
    }

    public DecorationBuilder withSlot(BiFunction<Setting, GuiInfo, Integer> slot) {
        withSlots((setting, guiInfo) -> Collections.singletonList(slot.apply(setting, guiInfo)));
        return this;
    }

    public DecorationBuilder withSlots(List<Integer> slots) {
        withSlots((setting, guiInfo) -> slots);
        return this;
    }

    public DecorationBuilder withSlots(BiFunction<Setting, GuiInfo, List<Integer>> slots) {
        records.add((gui, data) -> data.slots.add(slots));
        return this;
    }

    Decoration build(GuiBuilder parent) {
        DecorationData data = new DecorationData();

        parent.copy(data);

        for (BiConsumer<GuiBuilder, DecorationData> consumer : records) {
            consumer.accept(parent, data);
        }

        return data.build();
    }

    private final class DecorationData extends GuiBuilder {
        private final List<BiFunction<Setting, GuiInfo, List<Integer>>> slots = new LinkedList<>();
        private BiFunction<Setting, GuiInfo, ItemStack> itemStackFunction;

        Decoration build() {
            Object identifier = DecorationBuilder.this.identifier;
            BiFunction<Setting, GuiInfo, ItemStack> itemStackFunction = this.itemStackFunction;

            if (loadMissingFromConfig) {
                if (itemStackFunction == null) {
                    itemStackFunction = (setting, guiInfo) -> setting.get(identifier, key == null ? "decorations.item-stack" : "decorations." + key + ".item-stack", new ItemStack(Material.STONE));
                }

                if (slots.isEmpty()) {
                    slots.add((setting, guiInfo) -> setting.get(identifier, key == null ? "decorations.slots" : "decorations." + key + ".slots", (List<Object>) new LinkedList<>()).stream().map(NumberConversions::toInt).collect(Collectors.toList()));
                }
            }

            return new SimpleDecoration(setting, itemStackFunction, slots);
        }
    }
}

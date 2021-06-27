package de.derfrzocker.spigot.utils.guin.builders;

import de.derfrzocker.spigot.utils.guin.Decoration;
import de.derfrzocker.spigot.utils.guin.GuiInfo;
import de.derfrzocker.spigot.utils.guin.SimpleDecoration;
import de.derfrzocker.spigot.utils.guin.settings.Setting;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.NumberConversions;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class DecorationBuilder<S extends Setting<S>> {

    private final List<BiConsumer<GuiBuilder<?>, DecorationData>> records = new LinkedList<>();
    private final BiFunction<Setting<?>, S, S> settingSFunction;
    protected Object identifier;
    protected Object key;

    private DecorationBuilder(BiFunction<Setting<?>, S, S> settingSFunction) {
        this.settingSFunction = settingSFunction;
    }

    public static <S extends Setting<S>> DecorationBuilder<S> builder(BiFunction<Setting<?>, S, S> settingSFunction) {
        return new DecorationBuilder<>(settingSFunction);
    }

    public DecorationBuilder<S> withSetting(Setting<?> setting) {
        records.add((parent, data) -> data.setting = settingSFunction.apply(setting, data.setting));
        return this;
    }

    public DecorationBuilder<S> itemStack(ItemStack itemStack) {
        itemStack((setting, guiInfo) -> itemStack);
        return this;
    }

    public DecorationBuilder<S> itemStack(BiFunction<S, GuiInfo, ItemStack> itemStackFunction) {
        records.add((gui, data) -> data.itemStackFunction = itemStackFunction);
        return this;
    }

    public DecorationBuilder<S> identifier(Object identifier) {
        this.identifier = identifier;
        return this;
    }

    public DecorationBuilder<S> key(Object key) {
        this.key = key;
        return this;
    }

    public DecorationBuilder<S> withSlot(int slot) {
        withSlot((setting, guiInfo) -> slot);
        return this;
    }

    public DecorationBuilder<S> withSlot(BiFunction<S, GuiInfo, Integer> slot) {
        withSlots((setting, guiInfo) -> Collections.singletonList(slot.apply(setting, guiInfo)));
        return this;
    }

    public DecorationBuilder<S> withSlots(List<Integer> slots) {
        withSlots((setting, guiInfo) -> slots);
        return this;
    }

    public DecorationBuilder<S> withSlots(BiFunction<S, GuiInfo, List<Integer>> slots) {
        records.add((gui, data) -> data.slots.add(slots));
        return this;
    }

    Decoration build(GuiBuilder<?> parent) {
        DecorationData data = new DecorationData();

        parent.copy(data, settingSFunction);

        for (BiConsumer<GuiBuilder<?>, DecorationData> consumer : records) {
            consumer.accept(parent, data);
        }

        return data.build();
    }

    private final class DecorationData extends GuiBuilder<S> {
        private final List<BiFunction<S, GuiInfo, List<Integer>>> slots = new LinkedList<>();
        private BiFunction<S, GuiInfo, ItemStack> itemStackFunction;

        Decoration build() {
            Object identifier = DecorationBuilder.this.identifier;
            BiFunction<S, GuiInfo, ItemStack> itemStackFunction = this.itemStackFunction;

            if (loadMissingFromConfig) {
                if (itemStackFunction == null) {
                    itemStackFunction = (setting, guiInfo) -> setting.get(identifier, key == null ? "decorations.item-stack" : "decorations." + key + ".item-stack", new ItemStack(Material.STONE));
                }

                if (slots.isEmpty()) {
                    slots.add((setting, guiInfo) -> setting.get(identifier, key == null ? "decorations.slots" : "decorations." + key + ".slots", (List<Object>) new LinkedList<>()).stream().map(NumberConversions::toInt).collect(Collectors.toList()));
                }
            }

            return new SimpleDecoration<>(setting, itemStackFunction, slots);
        }
    }
}

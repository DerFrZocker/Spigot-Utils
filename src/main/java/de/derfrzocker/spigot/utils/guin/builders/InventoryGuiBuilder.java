package de.derfrzocker.spigot.utils.guin.builders;

import de.derfrzocker.spigot.utils.guin.Decoration;
import de.derfrzocker.spigot.utils.guin.GuiInfo;
import de.derfrzocker.spigot.utils.guin.InventoryGui;
import de.derfrzocker.spigot.utils.guin.buttons.ButtonContext;
import de.derfrzocker.spigot.utils.setting.Setting;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

public abstract class InventoryGuiBuilder {

    protected Object identifier;

    protected abstract InventoryGui build(GuiBuilder parent);

    protected abstract class InventoryGuiData extends GuiBuilder {
        protected List<ButtonContext> buttonContextsPlace = new LinkedList<>();
        protected List<Decoration> decorationsPlace = new LinkedList<>();
        protected BiFunction<Setting, GuiInfo, String> inventoryName;
        protected BiFunction<Setting, GuiInfo, Integer> rows;

        protected abstract InventoryGui build();
    }
}

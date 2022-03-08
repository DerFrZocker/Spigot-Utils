package de.derfrzocker.spigot.utils.guin.builders;

import de.derfrzocker.spigot.utils.guin.GuiInfo;
import de.derfrzocker.spigot.utils.guin.InventoryGui;
import de.derfrzocker.spigot.utils.guin.SingleInventoryGui;
import de.derfrzocker.spigot.utils.setting.Setting;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.BiFunction;

public final class SingleInventoryGuiBuilder extends GuiBuilder {

    private final Set<ButtonContextBuilder> buttonContextBuilders = new LinkedHashSet<>();
    private final Set<ListButtonBuilder> listButtonBuilders = new LinkedHashSet<>();
    private BiFunction<Setting, GuiInfo, String> name;
    private BiFunction<Setting, GuiInfo, Integer> rows;
    private BiFunction<Setting, GuiInfo, Boolean> allowBottomPickUp;
    private BiFunction<Setting, GuiInfo, Boolean> decorations;

    private SingleInventoryGuiBuilder() {
    }

    public static SingleInventoryGuiBuilder builder() {
        return new SingleInventoryGuiBuilder();
    }

    public SingleInventoryGuiBuilder withSetting(Setting setting) {
        this.setting = this.setting.withSetting(setting);
        return this;
    }

    public SingleInventoryGuiBuilder identifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public SingleInventoryGuiBuilder name(String name) {
        name((setting, humanEntity) -> name);
        return this;
    }

    public SingleInventoryGuiBuilder name(BiFunction<Setting, GuiInfo, String> name) {
        this.name = name;
        return this;
    }

    public SingleInventoryGuiBuilder rows(Integer rows) {
        rows((setting, humanEntity) -> rows);
        return this;
    }

    public SingleInventoryGuiBuilder rows(BiFunction<Setting, GuiInfo, Integer> rows) {
        this.rows = rows;
        return this;
    }

    public SingleInventoryGuiBuilder addButtonContext(ButtonContextBuilder contextBuilder) {
        buttonContextBuilders.add(contextBuilder);
        return this;
    }

    public SingleInventoryGuiBuilder addListButton(ListButtonBuilder buttonBuilder) {
        listButtonBuilders.add(buttonBuilder);
        return this;
    }

    public SingleInventoryGuiBuilder allowBottomPickUp(boolean allow) {
        allowBottomPickUp((setting, guiInfo) -> allow);
        return this;
    }

    public SingleInventoryGuiBuilder allowBottomPickUp(BiFunction<Setting, GuiInfo, Boolean> allowBottomPickUp) {
        this.allowBottomPickUp = allowBottomPickUp;
        return this;
    }

    public InventoryGui build() {
        BiFunction<Setting, GuiInfo, String> name = this.name;
        BiFunction<Setting, GuiInfo, Integer> rows = this.rows;
        BiFunction<Setting, GuiInfo, Boolean> allowBottomPickUp = this.allowBottomPickUp;
        BiFunction<Setting, GuiInfo, Boolean> decorations = this.decorations;

        if (rows == null) {
            rows = (setting, guiInfo) -> setting.get(identifier, "rows", 1);
        }

        if (name == null) {
            name = (setting, guiInfo) -> setting.get(identifier, "name", "Not present");
        }

        if (allowBottomPickUp == null) {
            allowBottomPickUp = (setting, guiInfo) -> setting.get(identifier, "allow-bottom-pickup", false);
        }

        if (decorations == null) {
            decorations = (setting, guiInfo) -> setting.get(identifier, "place-decorations", true);
        }

        SingleInventoryGui gui = new SingleInventoryGui(identifier, setting, rows, name, allowBottomPickUp, decorations);

        buttonContextBuilders.stream().map(builder -> builder.build(setting)).forEach(gui::addButtonContext);
        listButtonBuilders.stream().map(builder -> builder.build(setting)).forEach(gui::addListButton);

        return gui;
    }
}

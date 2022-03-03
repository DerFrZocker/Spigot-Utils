package de.derfrzocker.spigot.utils.guin.builders;

import de.derfrzocker.spigot.utils.guin.GuiInfo;
import de.derfrzocker.spigot.utils.guin.InventoryGui;
import de.derfrzocker.spigot.utils.guin.SingleInventoryGui;
import de.derfrzocker.spigot.utils.setting.Setting;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class SingleInventoryGuiBuilder extends InventoryGuiBuilder {

    private final List<Consumer<SingleInventoryGuiData>> records = new LinkedList<>();

    private SingleInventoryGuiBuilder() {
    }

    public static SingleInventoryGuiBuilder builder() {
        return new SingleInventoryGuiBuilder();
    }

    public SingleInventoryGuiBuilder withSetting(Setting setting) {
        records.add(data -> data.setting = data.setting.withSetting(setting));
        return this;
    }

    public SingleInventoryGuiBuilder identifier(Object identifier) {
        this.identifier = identifier;
        return this;
    }

    public SingleInventoryGuiBuilder name(String name) {
        name((setting, humanEntity) -> name);
        return this;
    }

    public SingleInventoryGuiBuilder name(BiFunction<Setting, GuiInfo, String> name) {
        records.add(data -> data.inventoryName = name);
        return this;
    }

    public SingleInventoryGuiBuilder rows(Integer rows) {
        rows((setting, humanEntity) -> rows);
        return this;
    }

    public SingleInventoryGuiBuilder rows(BiFunction<Setting, GuiInfo, Integer> rows) {
        records.add(data -> data.rows = rows);
        return this;
    }

    public SingleInventoryGuiBuilder add(ButtonBuilder buttonBuilder) {
        records.add(data -> {
            if (buttonBuilder.identifier != null) {
                data.buttons.put(buttonBuilder.identifier, buttonBuilder::build);
            }
        });
        return this;
    }

    public SingleInventoryGuiBuilder add(ButtonContextBuilder contextBuilder) {
        records.add(data -> {
            if (contextBuilder.identifier != null) {
                data.buttonContexts.put(contextBuilder.identifier, contextBuilder::build);
            }
        });
        return this;
    }

    public SingleInventoryGuiBuilder addButtonContext(ButtonContextBuilder contextBuilder) {
        records.add(data -> {
            if (contextBuilder.identifier != null) {
                data.buttonContexts.put(contextBuilder.identifier, contextBuilder::build);
            }
            data.buttonContextsPlace.add(contextBuilder.build(data));
        });
        return this;
    }

    public SingleInventoryGuiBuilder addButtonContext(Object identifier) {
        records.add(data -> data.buttonContextsPlace.add(data.buttonContexts.get(identifier).apply(data)));
        return this;
    }

    public SingleInventoryGuiBuilder allowBottomPickUp(boolean allow) {
        allowBottomPickUp((setting, guiInfo) -> allow);
        return this;
    }

    public SingleInventoryGuiBuilder allowBottomPickUp(BiFunction<Setting, GuiInfo, Boolean> allow) {
        records.add(data -> data.allowBottomPickUp = allow);
        return this;
    }

    public SingleInventoryGuiBuilder addConfigDecorations() {
        records.add(data -> {
            Set<String> keys = data.setting.getKeys(identifier, "decorations");

            if (keys == null) {
                return;
            }

            for (String key : keys) {
                data.decorationsPlace.add(DecorationBuilder.builder().identifier(identifier).key(key).build(data));
            }

        });
        return this;
    }

    @Override
    protected InventoryGui build(GuiBuilder parent) {
        SingleInventoryGuiData data = new SingleInventoryGuiData();

        parent.copy(data);

        for (Consumer<SingleInventoryGuiData> consumer : records) {
            consumer.accept(data);
        }

        return data.build();
    }

    public InventoryGui build() {
        SingleInventoryGuiData data = new SingleInventoryGuiData();

        for (Consumer<SingleInventoryGuiData> consumer : records) {
            consumer.accept(data);
        }

        return data.build();
    }

    private final class SingleInventoryGuiData extends InventoryGuiData {
        protected BiFunction<Setting, GuiInfo, Boolean> allowBottomPickUp;

        @Override
        protected InventoryGui build() {
            Object identifier = SingleInventoryGuiBuilder.this.identifier;
            BiFunction<Setting, GuiInfo, Integer> rows = this.rows;
            BiFunction<Setting, GuiInfo, String> inventoryName = this.inventoryName;
            BiFunction<Setting, GuiInfo, Boolean> allowBottomPickUp = this.allowBottomPickUp;

            if (loadMissingFromConfig) {
                if (rows == null) {
                    rows = (setting, guiInfo) -> setting.get(identifier, "rows", 1);
                }

                if (inventoryName == null) {
                    inventoryName = (setting, guiInfo) -> setting.get(identifier, "name", "Not present");
                }

                if (allowBottomPickUp == null) {
                    allowBottomPickUp = (setting, guiInfo) -> setting.get(identifier, "allow-bottom-pickup", false);
                }
            }

            SingleInventoryGui gui = new SingleInventoryGui(setting, rows, inventoryName, allowBottomPickUp);

            buttonContextsPlace.forEach(gui::addButtonContext);
            decorationsPlace.forEach(gui::addDecoration);

            return gui;
        }
    }
}

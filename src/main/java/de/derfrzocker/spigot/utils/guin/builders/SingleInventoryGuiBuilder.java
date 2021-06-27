package de.derfrzocker.spigot.utils.guin.builders;

import de.derfrzocker.spigot.utils.guin.GuiInfo;
import de.derfrzocker.spigot.utils.guin.InventoryGui;
import de.derfrzocker.spigot.utils.guin.SingleInventoryGui;
import de.derfrzocker.spigot.utils.guin.settings.Setting;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class SingleInventoryGuiBuilder<S extends Setting<S>> extends InventoryGuiBuilder<S> {

    private final List<Consumer<SingleInventoryGuiData>> records = new LinkedList<>();
    private final BiFunction<Setting<?>, S, S> settingSFunction;

    private SingleInventoryGuiBuilder(BiFunction<Setting<?>, S, S> settingSFunction) {
        this.settingSFunction = settingSFunction;
    }

    public static <S extends Setting<S>> SingleInventoryGuiBuilder<S> builder(BiFunction<Setting<?>, S, S> settingSFunction) {
        return new SingleInventoryGuiBuilder<>(settingSFunction);
    }

    public SingleInventoryGuiBuilder<S> withSetting(Setting<?> setting) {
        records.add(data -> data.setting = settingSFunction.apply(setting, data.setting));
        return this;
    }

    public SingleInventoryGuiBuilder<S> identifier(Object identifier) {
        this.identifier = identifier;
        return this;
    }

    public SingleInventoryGuiBuilder<S> name(String name) {
        name((setting, humanEntity) -> name);
        return this;
    }

    public SingleInventoryGuiBuilder<S> name(BiFunction<S, GuiInfo, String> name) {
        records.add(data -> data.inventoryName = name);
        return this;
    }

    public SingleInventoryGuiBuilder<S> rows(Integer rows) {
        rows((setting, humanEntity) -> rows);
        return this;
    }

    public SingleInventoryGuiBuilder<S> rows(BiFunction<S, GuiInfo, Integer> rows) {
        records.add(data -> data.rows = rows);
        return this;
    }

    public SingleInventoryGuiBuilder<S> add(ButtonBuilder<?> buttonBuilder) {
        records.add(data -> {
            if (buttonBuilder.identifier != null) {
                data.buttons.put(buttonBuilder.identifier, buttonBuilder::build);
            }
        });
        return this;
    }

    public SingleInventoryGuiBuilder<S> add(InventoryGuiBuilder<?> inventoryGuiBuilder) {
        records.add(data -> {
            if (inventoryGuiBuilder.identifier != null) {
                data.inventoryGuis.put(inventoryGuiBuilder.identifier, inventoryGuiBuilder.build(data));
            }
        });
        return this;
    }

    public SingleInventoryGuiBuilder<S> add(ButtonContextBuilder<?> contextBuilder) {
        records.add(data -> {
            if (contextBuilder.identifier != null) {
                data.buttonContexts.put(contextBuilder.identifier, contextBuilder::build);
            }
        });
        return this;
    }

    public SingleInventoryGuiBuilder<S> addButtonContext(ButtonContextBuilder<?> contextBuilder) {
        records.add(data -> {
            if (contextBuilder.identifier != null) {
                data.buttonContexts.put(contextBuilder.identifier, contextBuilder::build);
            }
            data.buttonContextsPlace.add(contextBuilder.build(data));
        });
        return this;
    }

    public SingleInventoryGuiBuilder<S> addButtonContext(Object identifier) {
        records.add(data -> data.buttonContextsPlace.add(data.buttonContexts.get(identifier).apply(data)));
        return this;
    }

    public SingleInventoryGuiBuilder<S> allowBottomPickUp(boolean allow) {
        allowBottomPickUp((setting, guiInfo) -> allow);
        return this;
    }

    public SingleInventoryGuiBuilder<S> allowBottomPickUp(BiFunction<S, GuiInfo, Boolean> allow) {
        records.add(data -> data.allowBottomPickUp = allow);
        return this;
    }

    public SingleInventoryGuiBuilder<S> addConfigDecorations() {
        records.add(data -> {
            Set<String> keys = data.setting.getKeys(identifier, "decorations");

            if (keys == null) {
                return;
            }

            for (String key : keys) {
                data.decorationsPlace.add(DecorationBuilder.builder(settingSFunction).identifier(identifier).key(key).build(data));
            }

        });
        return this;
    }

    @Override
    protected InventoryGui build(GuiBuilder<?> parent) {
        SingleInventoryGuiData data = new SingleInventoryGuiData();

        parent.copy(data, settingSFunction);

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
        protected BiFunction<S, GuiInfo, Boolean> allowBottomPickUp;

        @Override
        protected InventoryGui build() {
            Object identifier = SingleInventoryGuiBuilder.this.identifier;
            BiFunction<S, GuiInfo, Integer> rows = this.rows;
            BiFunction<S, GuiInfo, String> inventoryName = this.inventoryName;
            BiFunction<S, GuiInfo, Boolean> allowBottomPickUp = this.allowBottomPickUp;

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

            SingleInventoryGui<S> gui = new SingleInventoryGui<>(setting, rows, inventoryName, allowBottomPickUp);

            inventoryGuis.forEach(gui::addInventoryGui);
            buttonContextsPlace.forEach(gui::addButtonContext);
            decorationsPlace.forEach(gui::addDecoration);

            if (identifier != null) {
                inventoryGuis.forEach((id, inventoryGui) -> inventoryGui.addInventoryGui(identifier, gui));
            }

            return gui;
        }
    }
}

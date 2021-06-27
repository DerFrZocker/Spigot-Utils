package de.derfrzocker.spigot.utils.guin.builders;

import de.derfrzocker.spigot.utils.guin.GuiInfo;
import de.derfrzocker.spigot.utils.guin.buttons.Button;
import de.derfrzocker.spigot.utils.guin.buttons.ButtonContext;
import de.derfrzocker.spigot.utils.guin.buttons.SimpleButtonContext;
import de.derfrzocker.spigot.utils.guin.settings.Setting;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public final class ButtonContextBuilder<S extends Setting<S>> {

    private final List<BiConsumer<GuiBuilder<?>, ButtonContextData>> records = new LinkedList<>();
    private final BiFunction<Setting<?>, S, S> settingSFunction;
    protected Object identifier;

    private ButtonContextBuilder(BiFunction<Setting<?>, S, S> settingSFunction) {
        this.settingSFunction = settingSFunction;
    }

    public static <S extends Setting<S>> ButtonContextBuilder<S> builder(BiFunction<Setting<?>, S, S> settingSFunction) {
        return new ButtonContextBuilder<>(settingSFunction);
    }

    public ButtonContextBuilder<S> withSetting(Setting<?> setting) {
        records.add((parent, data) -> data.setting = settingSFunction.apply(setting, data.setting));
        return this;
    }

    public ButtonContextBuilder<S> identifier(Object identifier) {
        this.identifier = identifier;
        return this;
    }

    public ButtonContextBuilder<S> slot(int slot) {
        slot((setting, guiInfo) -> slot);
        return this;
    }

    public ButtonContextBuilder<S> slot(BiFunction<S, GuiInfo, Integer> slotFunction) {
        records.add((gui, data) -> data.slotFunction = slotFunction);
        return this;
    }

    public ButtonContextBuilder<S> button(ButtonBuilder<?> buttonBuilder) {
        records.add((gui, data) -> {
            if (buttonBuilder.identifier != null) {
                data.buttons.put(buttonBuilder.identifier, buttonBuilder::build);
            }
            data.button = buttonBuilder.build(data);
        });
        return this;
    }

    public ButtonContextBuilder<S> button(Object identifier) {
        records.add((gui, data) -> data.button = data.buttons.get(identifier).apply(data));
        return this;
    }

    public ButtonContextBuilder<S> withPermission(String permission) {
        withPermission(s -> permission);
        return this;
    }

    public ButtonContextBuilder<S> withPermission(Function<S, String> permissionFunction) {
        withCondition((setting, guiInfo) -> guiInfo.getEntity().hasPermission(permissionFunction.apply(setting)));
        return this;
    }

    public ButtonContextBuilder<S> withCondition(Predicate<GuiInfo> predicate) {
        withCondition((setting, guiInfo) -> predicate.test(guiInfo));
        return this;
    }

    public ButtonContextBuilder<S> withCondition(BiPredicate<S, GuiInfo> predicate) {
        records.add((gui, data) -> data.conditions.add(predicate));
        return this;
    }

    ButtonContext build(GuiBuilder<?> parent) {
        ButtonContextData data = new ButtonContextData();

        parent.copy(data, settingSFunction);

        for (BiConsumer<GuiBuilder<?>, ButtonContextData> consumer : records) {
            consumer.accept(parent, data);
        }

        return data.build();
    }

    private final class ButtonContextData extends GuiBuilder<S> {
        private final List<BiPredicate<S, GuiInfo>> conditions = new LinkedList<>();
        private BiFunction<S, GuiInfo, Integer> slotFunction;
        private Button button;

        ButtonContext build() {
            Object identifier = ButtonContextBuilder.this.identifier;
            BiFunction<S, GuiInfo, Integer> slotFunction = this.slotFunction;
            Button button = this.button;

            if (loadMissingFromConfig) {
                if (slotFunction == null) {
                    slotFunction = (setting, guiInfo) -> setting.get(identifier, "slot", 0);
                }

                if (button == null) {
                    button = ButtonBuilder.builder(settingSFunction).build(this);
                }
            }

            return new SimpleButtonContext<>(setting, slotFunction, button, conditions);
        }
    }

}

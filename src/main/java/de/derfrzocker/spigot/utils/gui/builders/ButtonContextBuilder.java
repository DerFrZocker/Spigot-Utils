package de.derfrzocker.spigot.utils.gui.builders;

import de.derfrzocker.spigot.utils.gui.GuiInfo;
import de.derfrzocker.spigot.utils.gui.buttons.ButtonContext;
import de.derfrzocker.spigot.utils.gui.buttons.SimpleButtonContext;
import de.derfrzocker.spigot.utils.language.LanguageManager;
import de.derfrzocker.spigot.utils.setting.DummySetting;
import de.derfrzocker.spigot.utils.setting.Setting;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public final class ButtonContextBuilder extends GuiBuilder {

    private final List<BiPredicate<Setting, GuiInfo>> conditions = new LinkedList<>();
    private BiFunction<Setting, GuiInfo, Integer> slotFunction;
    private ButtonBuilder button;

    private ButtonContextBuilder() {
    }

    public static ButtonContextBuilder builder() {
        return new ButtonContextBuilder();
    }

    public ButtonContextBuilder withSetting(Setting setting) {
        this.setting = this.setting.withSetting(setting);
        return this;
    }

    public ButtonContextBuilder identifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public ButtonContextBuilder slot(int slot) {
        slot((setting, guiInfo) -> slot);
        return this;
    }

    public ButtonContextBuilder slot(BiFunction<Setting, GuiInfo, Integer> slotFunction) {
        this.slotFunction = slotFunction;
        return this;
    }

    public ButtonContextBuilder button(ButtonBuilder buttonBuilder) {
        this.button = buttonBuilder;
        return this;
    }

    public ButtonContextBuilder withPermission(String permission) {
        withPermission(s -> permission);
        return this;
    }

    public ButtonContextBuilder withPermission(Function<Setting, String> permissionFunction) {
        withCondition((setting, guiInfo) -> guiInfo.getEntity().hasPermission(permissionFunction.apply(setting)));
        return this;
    }

    public ButtonContextBuilder withCondition(Predicate<GuiInfo> predicate) {
        withCondition((setting, guiInfo) -> predicate.test(guiInfo));
        return this;
    }

    public ButtonContextBuilder withCondition(BiPredicate<Setting, GuiInfo> predicate) {
        conditions.add(predicate);
        return this;
    }

    public ButtonContext build(LanguageManager languageManager) {
        return build(new DummySetting(), languageManager);
    }

    ButtonContext build(Setting parent, LanguageManager languageManager) {
        BiFunction<Setting, GuiInfo, Integer> slotFunction = this.slotFunction;
        ButtonBuilder button = this.button;
        String identifier = this.identifier;
        parent = parent.withSetting(setting);

        if (slotFunction == null) {
            slotFunction = (setting, guiInfo) -> setting.get(identifier, "slot", 0);
        }

        if (button == null) {
            button = ButtonBuilder.builder().identifier(identifier);
        }

        return new SimpleButtonContext(parent, slotFunction, button.build(parent, languageManager), conditions);
    }
}

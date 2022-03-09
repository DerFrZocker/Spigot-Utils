package de.derfrzocker.spigot.utils.gui.builders;

import de.derfrzocker.spigot.utils.TripleFunction;
import de.derfrzocker.spigot.utils.TriplePredicate;
import de.derfrzocker.spigot.utils.gui.ClickAction;
import de.derfrzocker.spigot.utils.gui.GuiInfo;
import de.derfrzocker.spigot.utils.gui.buttons.ListButton;
import de.derfrzocker.spigot.utils.gui.buttons.SimpleListButton;
import de.derfrzocker.spigot.utils.message.MessageValue;
import de.derfrzocker.spigot.utils.setting.Setting;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class ListButtonBuilder extends GuiBuilder {

    private final List<BiConsumer<ClickAction, Object>> actions = new LinkedList<>();
    private final List<TriplePredicate<Setting, GuiInfo, Object>> conditions = new LinkedList<>();
    private final List<TripleFunction<Setting, GuiInfo, Object, MessageValue>> messageValues = new LinkedList<>();

    private ListButtonBuilder() {
    }

    public static ListButtonBuilder builder() {
        return new ListButtonBuilder();
    }

    public ListButtonBuilder withSetting(Setting setting) {
        this.setting = this.setting.withSetting(setting);
        return this;
    }

    public ListButtonBuilder identifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public ListButtonBuilder withAction(BiConsumer<ClickAction, Object> consumer) {
        actions.add(consumer);
        return this;
    }

    public ListButtonBuilder withPermission(Function<Object, String> permission) {
        withPermission((setting, data) -> permission.apply(data));
        return this;
    }

    public ListButtonBuilder withPermission(BiFunction<Setting, Object, String> permissionFunction) {
        withCondition((setting, guiInfo, data) -> guiInfo.getEntity().hasPermission(permissionFunction.apply(setting, data)));
        return this;
    }

    public ListButtonBuilder withCondition(BiPredicate<GuiInfo, Object> predicate) {
        withCondition((setting, guiInfo, data) -> predicate.test(guiInfo, data));
        return this;
    }

    public ListButtonBuilder withCondition(TriplePredicate<Setting, GuiInfo, Object> predicate) {
        conditions.add(predicate);
        return this;
    }

    public ListButtonBuilder withMessageValue(TripleFunction<Setting, GuiInfo, Object, MessageValue> messageValue) {
        messageValues.add(messageValue);
        return this;
    }

    ListButton build(Setting parent) {
        List<BiConsumer<ClickAction, Object>> actions = this.actions;
        List<TriplePredicate<Setting, GuiInfo, Object>> conditions = this.conditions;
        String identifier = this.identifier;
        parent = parent.withSetting(setting);

        return new SimpleListButton(identifier, parent, actions, conditions, messageValues);
    }
}

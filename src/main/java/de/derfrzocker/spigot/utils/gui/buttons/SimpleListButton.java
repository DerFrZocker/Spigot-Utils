package de.derfrzocker.spigot.utils.gui.buttons;

import de.derfrzocker.spigot.utils.TriplePredicate;
import de.derfrzocker.spigot.utils.gui.ClickAction;
import de.derfrzocker.spigot.utils.gui.GuiInfo;
import de.derfrzocker.spigot.utils.gui.builders.ButtonBuilder;
import de.derfrzocker.spigot.utils.gui.builders.ButtonContextBuilder;
import de.derfrzocker.spigot.utils.setting.Setting;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class SimpleListButton implements ListButton {

    private final String identifier;
    private final Setting setting;
    private final List<BiConsumer<ClickAction, Object>> actions = new LinkedList<>();
    private final List<TriplePredicate<Setting, GuiInfo, Object>> conditions = new LinkedList<>();

    public SimpleListButton(String identifier, Setting setting, List<BiConsumer<ClickAction, Object>> actions, List<TriplePredicate<Setting, GuiInfo, Object>> conditions) {
        this.identifier = identifier;
        this.setting = setting;
        this.actions.addAll(actions);
        this.conditions.addAll(conditions);
    }

    @Override
    public List<ButtonContext> getButtons() {
        List<ButtonContext> buttonContexts = new LinkedList<>();
        Set<String> keys = setting.getKeys(identifier);

        for (String key : keys) {
            Object value = setting.get(identifier + "." + key, "value", null);
            ButtonContextBuilder builder = ButtonContextBuilder.builder().identifier(identifier + "." + key).withSetting(setting);
            ButtonBuilder buttonBuilder = ButtonBuilder.builder().identifier(identifier + "." + key).withSetting(setting);

            for (TriplePredicate<Setting, GuiInfo, Object> condition : conditions) {
                builder.withCondition((setting1, guiInfo) -> condition.test(setting1, guiInfo, value));
            }

            for (BiConsumer<ClickAction, Object> action : actions) {
                buttonBuilder.withAction(clickAction -> action.accept(clickAction, value));
            }

            builder.button(buttonBuilder);

            buttonContexts.add(builder.build());
        }

        return buttonContexts;
    }
}

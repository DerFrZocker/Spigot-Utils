package de.derfrzocker.spigot.utils.gui.buttons;

import de.derfrzocker.spigot.utils.function.TripleFunction;
import de.derfrzocker.spigot.utils.function.TriplePredicate;
import de.derfrzocker.spigot.utils.gui.ClickAction;
import de.derfrzocker.spigot.utils.gui.GuiInfo;
import de.derfrzocker.spigot.utils.gui.builders.ButtonBuilder;
import de.derfrzocker.spigot.utils.gui.builders.ButtonContextBuilder;
import de.derfrzocker.spigot.utils.language.LanguageManager;
import de.derfrzocker.spigot.utils.message.MessageValue;
import de.derfrzocker.spigot.utils.setting.Setting;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

public class SimpleListButton implements ListButton {

    private final String identifier;
    private final Setting setting;
    private final LanguageManager languageManager;
    private final List<BiConsumer<ClickAction, Object>> actions = new LinkedList<>();
    private final List<TriplePredicate<Setting, GuiInfo, Object>> conditions = new LinkedList<>();
    private final List<TripleFunction<Setting, GuiInfo, Object, MessageValue>> messageValues = new LinkedList<>();

    public SimpleListButton(String identifier, Setting setting, LanguageManager languageManager, List<BiConsumer<ClickAction, Object>> actions, List<TriplePredicate<Setting, GuiInfo, Object>> conditions, List<TripleFunction<Setting, GuiInfo, Object, MessageValue>> messageValues) {
        this.identifier = identifier;
        this.setting = setting;
        this.languageManager = languageManager;
        this.actions.addAll(actions);
        this.conditions.addAll(conditions);
        this.messageValues.addAll(messageValues);
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

            for (TripleFunction<Setting, GuiInfo, Object, MessageValue> messageValue : messageValues) {
                buttonBuilder.withMessageValue((setting, guiInfo) -> messageValue.apply(setting, guiInfo, value));
            }

            builder.button(buttonBuilder);

            buttonContexts.add(builder.build(languageManager));
        }

        return buttonContexts;
    }
}

package de.derfrzocker.spigot.utils.gui.buttons;

import de.derfrzocker.spigot.utils.gui.ClickAction;
import de.derfrzocker.spigot.utils.gui.GuiInfo;
import de.derfrzocker.spigot.utils.language.LanguageManager;
import de.derfrzocker.spigot.utils.message.MessageUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import de.derfrzocker.spigot.utils.setting.Setting;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

public class SimpleButton implements Button {

    private final Setting setting;
    private final LanguageManager languageManager;
    private final BiFunction<Setting, GuiInfo, ItemStack> itemStackFunction;
    private final List<Consumer<ClickAction>> actions = new LinkedList<>();
    private final List<BiPredicate<Setting, GuiInfo>> conditions = new LinkedList<>();
    private final List<BiFunction<Setting, GuiInfo, MessageValue>> messageValues = new LinkedList<>();

    public SimpleButton(Setting setting, LanguageManager languageManager, BiFunction<Setting, GuiInfo, ItemStack> itemStackFunction, List<Consumer<ClickAction>> actions, List<BiPredicate<Setting, GuiInfo>> conditions, List<BiFunction<Setting, GuiInfo, MessageValue>> messageValues) {
        this.setting = setting;
        this.languageManager = languageManager;
        this.itemStackFunction = itemStackFunction;
        this.actions.addAll(actions);
        this.conditions.addAll(conditions);
        this.messageValues.addAll(messageValues);
    }

    @Override
    public ItemStack getItemStack(GuiInfo guiInfo) {
        return MessageUtil.format(languageManager != null ? languageManager.getLanguage((Player) guiInfo.getEntity()) : null, itemStackFunction.apply(setting, guiInfo), messageValues.stream().map(function -> function.apply(setting, guiInfo)).toArray(MessageValue[]::new));
    }

    @Override
    public void onClick(ClickAction event) {
        ClickType clickType = event.getClickEvent().getClick();
        if (clickType == ClickType.LEFT) {
            actions.forEach(consumer -> consumer.accept(event));
        } else {
            event.getClickEvent().setCancelled(true);
        }
    }

    @Override
    public boolean shouldPlace(GuiInfo guiInfo) {
        return conditions.stream().map(tester -> tester.test(setting, guiInfo)).filter(aBoolean -> !aBoolean).findAny().orElse(true);
    }
}

package de.derfrzocker.spigot.utils.gui.buttons;

import de.derfrzocker.spigot.utils.gui.GuiInfo;
import de.derfrzocker.spigot.utils.setting.Setting;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class SimpleButtonContext implements ButtonContext {

    private final Setting setting;
    private final BiFunction<Setting, GuiInfo, Integer> slotFunction;
    private final Button button;
    private final List<BiPredicate<Setting, GuiInfo>> conditions = new LinkedList<>();

    public SimpleButtonContext(Setting setting, BiFunction<Setting, GuiInfo, Integer> slotFunction, Button button, List<BiPredicate<Setting, GuiInfo>> conditions) {
        this.setting = setting;
        this.slotFunction = slotFunction;
        this.button = button;
        this.conditions.addAll(conditions);
    }

    @Override
    public int getSlot(GuiInfo guiInfo) {
        return slotFunction.apply(setting, guiInfo);
    }

    @Override
    public boolean shouldPlace(GuiInfo guiInfo) {
        return conditions.stream().map(tester -> tester.test(setting, guiInfo)).filter(aBoolean -> !aBoolean).findAny().orElse(true);
    }

    @Override
    public Button getButton() {
        return button;
    }


}

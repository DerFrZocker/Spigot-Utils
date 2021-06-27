package de.derfrzocker.spigot.utils.guin.buttons;

import de.derfrzocker.spigot.utils.guin.GuiInfo;
import de.derfrzocker.spigot.utils.guin.settings.Setting;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public class SimpleButtonContext<S extends Setting<S>> implements ButtonContext {

    private final S setting;
    private final BiFunction<S, GuiInfo, Integer> slotFunction;
    private final Button button;
    private final List<BiPredicate<S, GuiInfo>> conditions = new LinkedList<>();

    public SimpleButtonContext(S setting, BiFunction<S, GuiInfo, Integer> slotFunction, Button button, List<BiPredicate<S, GuiInfo>> conditions) {
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

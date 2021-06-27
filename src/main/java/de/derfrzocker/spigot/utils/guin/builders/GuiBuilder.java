package de.derfrzocker.spigot.utils.guin.builders;

import de.derfrzocker.spigot.utils.guin.InventoryGui;
import de.derfrzocker.spigot.utils.guin.buttons.Button;
import de.derfrzocker.spigot.utils.guin.buttons.ButtonContext;
import de.derfrzocker.spigot.utils.guin.settings.Setting;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public abstract class GuiBuilder<S extends Setting<S>> {

    protected boolean loadMissingFromConfig = true;

    protected boolean copySetting = true;
    protected S setting;

    protected boolean copyButtonIdentifier = true;
    protected Map<Object, Function<GuiBuilder<?>, Button>> buttons = new LinkedHashMap<>();

    protected boolean copyButtonContextIdentifier = true;
    protected Map<Object, Function<GuiBuilder<?>, ButtonContext>> buttonContexts = new LinkedHashMap<>();

    protected boolean copyInventoryGuiIdentifier = true;
    protected Map<Object, InventoryGui> inventoryGuis = new LinkedHashMap<>();

    protected <SO extends Setting<SO>> void copy(GuiBuilder<SO> other, BiFunction<Setting<?>, SO, SO> settingSFunction) {
        other.loadMissingFromConfig = loadMissingFromConfig;

        other.copySetting = copySetting;
        if (copySetting) {
            other.setting = settingSFunction.apply(setting, null);
        }

        other.copyButtonIdentifier = copyButtonIdentifier;
        if (copyButtonIdentifier) {
            other.buttons.putAll(buttons);
        }

        other.copyButtonContextIdentifier = copyButtonContextIdentifier;
        if (copyButtonContextIdentifier) {
            other.buttonContexts.putAll(buttonContexts);
        }

        other.copyInventoryGuiIdentifier = copyInventoryGuiIdentifier;
        if (copyInventoryGuiIdentifier) {
            other.inventoryGuis.putAll(inventoryGuis);
        }
    }

}

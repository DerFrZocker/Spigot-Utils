package de.derfrzocker.spigot.utils.gui.builders;

import de.derfrzocker.spigot.utils.setting.DummySetting;
import de.derfrzocker.spigot.utils.setting.Setting;

public abstract class GuiBuilder {
    protected String identifier;
    protected Setting setting = new DummySetting();
}

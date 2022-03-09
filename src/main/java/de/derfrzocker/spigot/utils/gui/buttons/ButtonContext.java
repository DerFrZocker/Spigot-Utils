package de.derfrzocker.spigot.utils.gui.buttons;

import de.derfrzocker.spigot.utils.gui.GuiInfo;

public interface ButtonContext {

    Button getButton();

    int getSlot(GuiInfo guiInfo);

    boolean shouldPlace(GuiInfo guiInfo);

}

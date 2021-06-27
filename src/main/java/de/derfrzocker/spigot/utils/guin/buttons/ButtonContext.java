package de.derfrzocker.spigot.utils.guin.buttons;

import de.derfrzocker.spigot.utils.guin.GuiInfo;

public interface ButtonContext {

    Button getButton();

    int getSlot(GuiInfo guiInfo);

    boolean shouldPlace(GuiInfo guiInfo);

}

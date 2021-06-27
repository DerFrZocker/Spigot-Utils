package de.derfrzocker.spigot.utils.guin.buttons;

import de.derfrzocker.spigot.utils.guin.ClickAction;
import de.derfrzocker.spigot.utils.guin.GuiInfo;
import de.derfrzocker.spigot.utils.guin.ItemStackProvider;

public interface Button extends ItemStackProvider {

    void onClick(ClickAction event);

    boolean shouldPlace(GuiInfo guiInfo);

}

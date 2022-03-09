package de.derfrzocker.spigot.utils.gui.buttons;

import de.derfrzocker.spigot.utils.gui.ClickAction;
import de.derfrzocker.spigot.utils.gui.GuiInfo;
import de.derfrzocker.spigot.utils.gui.ItemStackProvider;

public interface Button extends ItemStackProvider {

    void onClick(ClickAction event);

    boolean shouldPlace(GuiInfo guiInfo);

}

package de.derfrzocker.spigot.utils.gui;

import java.util.List;

public interface Decoration extends ItemStackProvider {

    List<Integer> getSlots(GuiInfo guiInfo);

}

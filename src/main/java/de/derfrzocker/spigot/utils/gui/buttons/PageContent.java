package de.derfrzocker.spigot.utils.gui.buttons;

import de.derfrzocker.spigot.utils.gui.ClickAction;
import de.derfrzocker.spigot.utils.gui.GuiInfo;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.OptionalInt;

public interface PageContent<D> {

    List<D> getData(GuiInfo guiInfo);

    boolean shouldPlace(GuiInfo guiInfo, D data);

    ItemStack getItemStack(GuiInfo guiInfo, D data);

    OptionalInt getSlot(GuiInfo guiInfo, D data);

    void onClick(ClickAction event, D data);

}

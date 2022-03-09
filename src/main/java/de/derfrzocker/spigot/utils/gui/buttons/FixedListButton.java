package de.derfrzocker.spigot.utils.gui.buttons;

import java.util.List;

public class FixedListButton implements ListButton {

    private final List<ButtonContext> buttonContexts;

    public FixedListButton(List<ButtonContext> buttonContexts) {
        this.buttonContexts = buttonContexts;
    }

    @Override
    public List<ButtonContext> getButtons() {
        return buttonContexts;
    }
}

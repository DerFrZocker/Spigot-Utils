package de.derfrzocker.spigot.utils.gui;

public class InventoryUtil {

    public static int calculateSlot(final int current, final int start) { // No Idea if this work never tested //EDIT: It works (first try)
        final int rowSize = 9 - (2 * start);

        if (rowSize <= 0)
            throw new IllegalArgumentException("row size is <= 0 for input current: " + current + ", start: " + start);

        final int row = current / rowSize;

        final int slot = current + start;

        if (slot == 0)
            return current + start;

        return slot + (2 * start * row);
    }

    public static int calculateSlots(final int rows, final int gap) {
        return (9 - 2 * gap) * rows;
    }

    public static int calculatePages(final int slots, final int amount) {
        final int rest = amount % slots;
        int pages = amount / slots;

        if (rest != 0)
            pages++;

        return pages;
    }

}

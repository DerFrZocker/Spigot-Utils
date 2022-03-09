package de.derfrzocker.spigot.utils.gui.builders;

import org.jetbrains.annotations.NotNull;

public final class Builders {

    private Builders() {
    }

    @NotNull
    public static ButtonBuilder button() {
        return ButtonBuilder.builder();
    }

    @NotNull
    public static ButtonContextBuilder buttonContext() {
        return ButtonContextBuilder.builder();
    }

    @NotNull
    public static ListButtonBuilder listButton() {
        return ListButtonBuilder.builder();
    }

    @NotNull
    public static <D> PageContentBuilder<D> pageContent(Class<D> dClass) {
        return PageContentBuilder.builder(dClass);
    }

    @NotNull
    public static PagedInventoryGuiBuilder paged() {
        return PagedInventoryGuiBuilder.builder();
    }

    @NotNull
    public static SingleInventoryGuiBuilder single() {
        return SingleInventoryGuiBuilder.builder();
    }
}

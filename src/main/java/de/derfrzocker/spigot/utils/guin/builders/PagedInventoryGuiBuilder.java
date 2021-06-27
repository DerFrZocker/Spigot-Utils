package de.derfrzocker.spigot.utils.guin.builders;

import de.derfrzocker.spigot.utils.guin.GuiInfo;
import de.derfrzocker.spigot.utils.guin.InventoryGui;
import de.derfrzocker.spigot.utils.guin.PagedGuiInfo;
import de.derfrzocker.spigot.utils.guin.PagedInventoryGui;
import de.derfrzocker.spigot.utils.guin.buttons.PageContent;
import de.derfrzocker.spigot.utils.guin.settings.DummySetting;
import de.derfrzocker.spigot.utils.guin.settings.Setting;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class PagedInventoryGuiBuilder<S extends Setting<S>> extends InventoryGuiBuilder<S> {

    private final List<Consumer<PagedInventoryGuiData>> records = new LinkedList<>();
    private final BiFunction<Setting<?>, S, S> settingSFunction;

    private PagedInventoryGuiBuilder(BiFunction<Setting<?>, S, S> settingSFunction) {
        this.settingSFunction = settingSFunction;
    }

    public static <S extends Setting<S>> PagedInventoryGuiBuilder<S> builder(BiFunction<Setting<?>, S, S> settingSFunction) {
        return new PagedInventoryGuiBuilder<>(settingSFunction);
    }

    public PagedInventoryGuiBuilder<S> withSetting(Setting<?> setting) {
        records.add(data -> data.setting = settingSFunction.apply(setting, data.setting));
        return this;
    }

    public PagedInventoryGuiBuilder<S> identifier(Object identifier) {
        this.identifier = identifier;
        return this;
    }

    public PagedInventoryGuiBuilder<S> name(String name) {
        name((setting, humanEntity) -> name);
        return this;
    }

    public PagedInventoryGuiBuilder<S> name(BiFunction<S, GuiInfo, String> name) {
        records.add(data -> data.inventoryName = name);
        return this;
    }

    public PagedInventoryGuiBuilder<S> rows(Integer rows) {
        rows((setting, humanEntity) -> rows);
        return this;
    }

    public PagedInventoryGuiBuilder<S> rows(BiFunction<S, GuiInfo, Integer> rows) {
        records.add(data -> data.rows = rows);
        return this;
    }

    public PagedInventoryGuiBuilder<S> add(ButtonBuilder<?> buttonBuilder) {
        records.add(data -> {
            if (buttonBuilder.identifier != null) {
                data.buttons.put(buttonBuilder.identifier, buttonBuilder::build);
            }
        });
        return this;
    }

    public PagedInventoryGuiBuilder<S> add(InventoryGuiBuilder<?> inventoryGuiBuilder) {
        records.add(data -> {
            if (inventoryGuiBuilder.identifier != null) {
                data.inventoryGuis.put(inventoryGuiBuilder.identifier, inventoryGuiBuilder.build(data));
            }
        });
        return this;
    }

    public PagedInventoryGuiBuilder<S> add(ButtonContextBuilder<?> contextBuilder) {
        records.add(data -> {
            if (contextBuilder.identifier != null) {
                data.buttonContexts.put(contextBuilder.identifier, contextBuilder::build);
            }
        });
        return this;
    }

    public PagedInventoryGuiBuilder<S> addButtonContext(ButtonContextBuilder<?> contextBuilder) {
        records.add(data -> {
            if (contextBuilder.identifier != null) {
                data.buttonContexts.put(contextBuilder.identifier, contextBuilder::build);
            }
            data.buttonContextsPlace.add(contextBuilder.build(data));
        });
        return this;
    }

    public PagedInventoryGuiBuilder<S> addButtonContext(Object identifier) {
        records.add(data -> data.buttonContextsPlace.add(data.buttonContexts.get(identifier).apply(data)));
        return this;
    }

    public PagedInventoryGuiBuilder<S> pageContent(PageContentBuilder<?, ?> pageContentBuilder) {
        records.add(data -> data.pageContent = pageContentBuilder.build(data));
        return this;
    }

    public PagedInventoryGuiBuilder<S> allowBottomPickUp(boolean allow) {
        allowBottomPickUp((setting, guiInfo) -> allow);
        return this;
    }

    public PagedInventoryGuiBuilder<S> allowBottomPickUp(BiFunction<S, GuiInfo, Boolean> allow) {
        records.add(data -> data.allowBottomPickUp = allow);
        return this;
    }

    public PagedInventoryGuiBuilder<S> addDefaultNextButton() {
        addButtonContext(ButtonContextBuilder.
                builder(settingSFunction).
                identifier(Controls.NEXT).
                withCondition(guiInfo -> ((PagedGuiInfo) guiInfo).getCurrentPage() < (((PagedGuiInfo) guiInfo).getMaxPages() - 1)).
                button(ButtonBuilder.
                        builder(settingSFunction).
                        identifier(Controls.NEXT).
                        withAction(clickAction -> clickAction.getClickEvent().setCancelled(true)).
                        withAction(clickAction -> ((PagedInventoryGui<?, ?>) clickAction.getInventoryGui()).openNextInventory(clickAction.getClickEvent().getView().getTopInventory(), clickAction.getClickEvent().getWhoClicked()))));
        return this;
    }

    public PagedInventoryGuiBuilder<S> addDefaultPreviousButton() {
        addButtonContext(ButtonContextBuilder.
                builder(settingSFunction).
                identifier(Controls.PREVIOUS).
                withCondition(guiInfo -> ((PagedGuiInfo) guiInfo).getCurrentPage() > 0).
                button(ButtonBuilder.
                        builder(settingSFunction).
                        identifier(Controls.PREVIOUS).
                        withAction(clickAction -> clickAction.getClickEvent().setCancelled(true)).
                        withAction(clickAction -> ((PagedInventoryGui<?, ?>) clickAction.getInventoryGui()).openPreviousInventory(clickAction.getClickEvent().getView().getTopInventory(), clickAction.getClickEvent().getWhoClicked()))));
        return this;
    }

    public PagedInventoryGuiBuilder<S> addConfigDecorations() {
        records.add(data -> {
            Set<String> keys = data.setting.getKeys(identifier, "decorations");

            if (keys == null) {
                return;
            }

            for (String key : keys) {
                data.decorationsPlace.add(DecorationBuilder.builder(settingSFunction).identifier(identifier).key(key).build(data));
            }

        });
        return this;
    }

    @Override
    protected InventoryGui build(GuiBuilder<?> parent) {
        PagedInventoryGuiData data = new PagedInventoryGuiData();

        parent.copy(data, settingSFunction);

        for (Consumer<PagedInventoryGuiData> consumer : records) {
            consumer.accept(data);
        }

        return data.build();
    }

    public InventoryGui build() {
        PagedInventoryGuiData data = new PagedInventoryGuiData();

        for (Consumer<PagedInventoryGuiData> consumer : records) {
            consumer.accept(data);
        }

        return data.build();
    }

    private enum Controls {
        NEXT, PREVIOUS;

        @Override
        public String toString() {
            return super.toString().toLowerCase();
        }
    }

    private final class PagedInventoryGuiData extends InventoryGuiData {
        protected PageContent<?> pageContent;
        protected BiFunction<S, GuiInfo, Integer> upperGap;
        protected BiFunction<S, GuiInfo, Integer> lowerGap;
        protected BiFunction<S, GuiInfo, Integer> sideGap;
        protected BiFunction<S, GuiInfo, Boolean> allowBottomPickUp;

        @Override
        protected InventoryGui build() {
            Object identifier = PagedInventoryGuiBuilder.this.identifier;
            BiFunction<S, GuiInfo, Integer> rows = this.rows;
            BiFunction<S, GuiInfo, String> inventoryName = this.inventoryName;
            BiFunction<S, GuiInfo, Integer> upperGap = this.upperGap;
            BiFunction<S, GuiInfo, Integer> lowerGap = this.lowerGap;
            BiFunction<S, GuiInfo, Integer> sideGap = this.sideGap;
            PageContent<?> pageContent = this.pageContent;
            BiFunction<S, GuiInfo, Boolean> allowBottomPickUp = this.allowBottomPickUp;

            if (loadMissingFromConfig) {
                if (rows == null) {
                    rows = (setting, guiInfo) -> setting.get(identifier, "rows", 1);
                }

                if (inventoryName == null) {
                    inventoryName = (setting, guiInfo) -> setting.get(identifier, "name", "Not present");
                }

                if (upperGap == null) {
                    upperGap = (setting, guiInfo) -> setting.get(identifier, "gap.upper", 1);
                }

                if (lowerGap == null) {
                    lowerGap = (setting, guiInfo) -> setting.get(identifier, "gap.lower", 1);
                }

                if (sideGap == null) {
                    sideGap = (setting, guiInfo) -> setting.get(identifier, "gap.side", 1);
                }

                if (pageContent == null) {
                    pageContent = PageContentBuilder.builder(Setting.createFunction(DummySetting::new), Object.class).withSetting(setting).build(this);
                }

                if (allowBottomPickUp == null) {
                    allowBottomPickUp = (setting, guiInfo) -> setting.get(identifier, "allow-bottom-pickup", false);
                }
            }

            PagedInventoryGui<S, ?> gui = new PagedInventoryGui<>(setting, rows, inventoryName, upperGap, lowerGap, sideGap, allowBottomPickUp, pageContent);

            inventoryGuis.forEach(gui::addInventoryGui);
            buttonContextsPlace.forEach(gui::addButtonContext);
            decorationsPlace.forEach(gui::addDecoration);

            if (identifier != null) {
                inventoryGuis.forEach((id, inventoryGui) -> inventoryGui.addInventoryGui(identifier, gui));
            }

            return gui;
        }
    }

}

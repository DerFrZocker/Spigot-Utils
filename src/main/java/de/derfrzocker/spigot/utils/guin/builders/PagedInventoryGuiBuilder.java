package de.derfrzocker.spigot.utils.guin.builders;

import de.derfrzocker.spigot.utils.guin.GuiInfo;
import de.derfrzocker.spigot.utils.guin.InventoryGui;
import de.derfrzocker.spigot.utils.guin.PagedGuiInfo;
import de.derfrzocker.spigot.utils.guin.PagedInventoryGui;
import de.derfrzocker.spigot.utils.guin.buttons.PageContent;
import de.derfrzocker.spigot.utils.setting.Setting;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public final class PagedInventoryGuiBuilder extends InventoryGuiBuilder {

    private final List<Consumer<PagedInventoryGuiData>> records = new LinkedList<>();

    private PagedInventoryGuiBuilder() {
    }

    public static PagedInventoryGuiBuilder builder() {
        return new PagedInventoryGuiBuilder();
    }

    public PagedInventoryGuiBuilder withSetting(Setting setting) {
        records.add(data -> data.setting = data.setting.withSetting(setting));
        return this;
    }

    public PagedInventoryGuiBuilder identifier(Object identifier) {
        this.identifier = identifier;
        return this;
    }

    public PagedInventoryGuiBuilder name(String name) {
        name((setting, humanEntity) -> name);
        return this;
    }

    public PagedInventoryGuiBuilder name(BiFunction<Setting, GuiInfo, String> name) {
        records.add(data -> data.inventoryName = name);
        return this;
    }

    public PagedInventoryGuiBuilder rows(Integer rows) {
        rows((setting, humanEntity) -> rows);
        return this;
    }

    public PagedInventoryGuiBuilder rows(BiFunction<Setting, GuiInfo, Integer> rows) {
        records.add(data -> data.rows = rows);
        return this;
    }

    public PagedInventoryGuiBuilder add(ButtonBuilder buttonBuilder) {
        records.add(data -> {
            if (buttonBuilder.identifier != null) {
                data.buttons.put(buttonBuilder.identifier, buttonBuilder::build);
            }
        });
        return this;
    }

    public PagedInventoryGuiBuilder add(ButtonContextBuilder contextBuilder) {
        records.add(data -> {
            if (contextBuilder.identifier != null) {
                data.buttonContexts.put(contextBuilder.identifier, contextBuilder::build);
            }
        });
        return this;
    }

    public PagedInventoryGuiBuilder addButtonContext(ButtonContextBuilder contextBuilder) {
        records.add(data -> {
            if (contextBuilder.identifier != null) {
                data.buttonContexts.put(contextBuilder.identifier, contextBuilder::build);
            }
            data.buttonContextsPlace.add(contextBuilder.build(data));
        });
        return this;
    }

    public PagedInventoryGuiBuilder addButtonContext(Object identifier) {
        records.add(data -> data.buttonContextsPlace.add(data.buttonContexts.get(identifier).apply(data)));
        return this;
    }

    public PagedInventoryGuiBuilder pageContent(PageContentBuilder<?> pageContentBuilder) {
        records.add(data -> data.pageContent = pageContentBuilder.build(data));
        return this;
    }

    public PagedInventoryGuiBuilder allowBottomPickUp(boolean allow) {
        allowBottomPickUp((setting, guiInfo) -> allow);
        return this;
    }

    public PagedInventoryGuiBuilder allowBottomPickUp(BiFunction<Setting, GuiInfo, Boolean> allow) {
        records.add(data -> data.allowBottomPickUp = allow);
        return this;
    }

    public PagedInventoryGuiBuilder addDefaultNextButton() {
        addButtonContext(ButtonContextBuilder.
                builder().
                identifier(Controls.NEXT).
                withCondition(guiInfo -> ((PagedGuiInfo) guiInfo).getCurrentPage() < (((PagedGuiInfo) guiInfo).getMaxPages() - 1)).
                button(ButtonBuilder.
                        builder().
                        identifier(Controls.NEXT).
                        withAction(clickAction -> clickAction.getClickEvent().setCancelled(true)).
                        withAction(clickAction -> ((PagedInventoryGui<?>) clickAction.getInventoryGui()).openNextInventory(clickAction.getClickEvent().getView().getTopInventory(), clickAction.getClickEvent().getWhoClicked()))));
        return this;
    }

    public PagedInventoryGuiBuilder addDefaultPreviousButton() {
        addButtonContext(ButtonContextBuilder.
                builder().
                identifier(Controls.PREVIOUS).
                withCondition(guiInfo -> ((PagedGuiInfo) guiInfo).getCurrentPage() > 0).
                button(ButtonBuilder.
                        builder().
                        identifier(Controls.PREVIOUS).
                        withAction(clickAction -> clickAction.getClickEvent().setCancelled(true)).
                        withAction(clickAction -> ((PagedInventoryGui<?>) clickAction.getInventoryGui()).openPreviousInventory(clickAction.getClickEvent().getView().getTopInventory(), clickAction.getClickEvent().getWhoClicked()))));
        return this;
    }

    public PagedInventoryGuiBuilder addConfigDecorations() {
        records.add(data -> {
            Set<String> keys = data.setting.getKeys(identifier, "decorations");

            if (keys == null) {
                return;
            }

            for (String key : keys) {
                data.decorationsPlace.add(DecorationBuilder.builder().identifier(identifier).key(key).build(data));
            }

        });
        return this;
    }

    @Override
    protected InventoryGui build(GuiBuilder parent) {
        PagedInventoryGuiData data = new PagedInventoryGuiData();

        parent.copy(data);

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
        protected BiFunction<Setting, GuiInfo, Integer> upperGap;
        protected BiFunction<Setting, GuiInfo, Integer> lowerGap;
        protected BiFunction<Setting, GuiInfo, Integer> sideGap;
        protected BiFunction<Setting, GuiInfo, Boolean> allowBottomPickUp;

        @Override
        protected InventoryGui build() {
            Object identifier = PagedInventoryGuiBuilder.this.identifier;
            BiFunction<Setting, GuiInfo, Integer> rows = this.rows;
            BiFunction<Setting, GuiInfo, String> inventoryName = this.inventoryName;
            BiFunction<Setting, GuiInfo, Integer> upperGap = this.upperGap;
            BiFunction<Setting, GuiInfo, Integer> lowerGap = this.lowerGap;
            BiFunction<Setting, GuiInfo, Integer> sideGap = this.sideGap;
            PageContent<?> pageContent = this.pageContent;
            BiFunction<Setting, GuiInfo, Boolean> allowBottomPickUp = this.allowBottomPickUp;

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
                    pageContent = PageContentBuilder.builder(Object.class).withSetting(setting).build(this);
                }

                if (allowBottomPickUp == null) {
                    allowBottomPickUp = (setting, guiInfo) -> setting.get(identifier, "allow-bottom-pickup", false);
                }
            }

            PagedInventoryGui<?> gui = new PagedInventoryGui<>(setting, rows, inventoryName, upperGap, lowerGap, sideGap, allowBottomPickUp, pageContent);

            buttonContextsPlace.forEach(gui::addButtonContext);
            decorationsPlace.forEach(gui::addDecoration);

            return gui;
        }
    }

}

package de.derfrzocker.spigot.utils.gui.builders;

import de.derfrzocker.spigot.utils.gui.GuiInfo;
import de.derfrzocker.spigot.utils.gui.InventoryGui;
import de.derfrzocker.spigot.utils.gui.PagedGuiInfo;
import de.derfrzocker.spigot.utils.gui.PagedInventoryGui;
import de.derfrzocker.spigot.utils.language.LanguageManager;
import de.derfrzocker.spigot.utils.message.MessageValue;
import de.derfrzocker.spigot.utils.setting.Setting;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public final class PagedInventoryGuiBuilder extends GuiBuilder {

    private final static String NEXT = "next";
    private final static String PREVIOUS = "previous";
    private final Set<ButtonContextBuilder> buttonContextBuilders = new LinkedHashSet<>();
    private final Set<ListButtonBuilder> listButtonBuilders = new LinkedHashSet<>();
    private final List<BiFunction<Setting, GuiInfo, MessageValue>> messageValues = new LinkedList<>();
    private final List<BiConsumer<Setting, GuiInfo>> backActions = new LinkedList<>();
    private LanguageManager languageManager;
    private BiFunction<Setting, GuiInfo, String> name;
    private BiFunction<Setting, GuiInfo, Integer> rows;
    private BiFunction<Setting, GuiInfo, Boolean> allowBottomPickUp;
    private BiFunction<Setting, GuiInfo, Boolean> decorations;
    private PageContentBuilder<?> pageContentBuilder;
    private BiFunction<Setting, GuiInfo, Integer> upperGap;
    private BiFunction<Setting, GuiInfo, Integer> lowerGap;
    private BiFunction<Setting, GuiInfo, Integer> sideGap;

    private PagedInventoryGuiBuilder() {
    }

    public static PagedInventoryGuiBuilder builder() {
        return new PagedInventoryGuiBuilder();
    }

    public PagedInventoryGuiBuilder withSetting(Setting setting) {
        this.setting = this.setting.withSetting(setting);
        return this;
    }

    public PagedInventoryGuiBuilder identifier(String identifier) {
        this.identifier = identifier;
        return this;
    }

    public PagedInventoryGuiBuilder languageManager(LanguageManager languageManager) {
        this.languageManager = languageManager;
        return this;
    }

    public PagedInventoryGuiBuilder name(String name) {
        name((setting, humanEntity) -> name);
        return this;
    }

    public PagedInventoryGuiBuilder name(BiFunction<Setting, GuiInfo, String> name) {
        this.name = name;
        return this;
    }

    public PagedInventoryGuiBuilder rows(Integer rows) {
        rows((setting, humanEntity) -> rows);
        return this;
    }

    public PagedInventoryGuiBuilder rows(BiFunction<Setting, GuiInfo, Integer> rows) {
        this.rows = rows;
        return this;
    }

    public PagedInventoryGuiBuilder addButtonContext(ButtonContextBuilder contextBuilder) {
        buttonContextBuilders.add(contextBuilder);
        return this;
    }

    public PagedInventoryGuiBuilder addListButton(ListButtonBuilder buttonBuilder) {
        listButtonBuilders.add(buttonBuilder);
        return this;
    }

    public PagedInventoryGuiBuilder pageContent(PageContentBuilder<?> pageContentBuilder) {
        this.pageContentBuilder = pageContentBuilder;
        return this;
    }

    public PagedInventoryGuiBuilder allowBottomPickUp(boolean allow) {
        allowBottomPickUp((setting, guiInfo) -> allow);
        return this;
    }

    public PagedInventoryGuiBuilder allowBottomPickUp(BiFunction<Setting, GuiInfo, Boolean> allow) {
        this.allowBottomPickUp = allow;
        return this;
    }

    public PagedInventoryGuiBuilder withMessageValue(BiFunction<Setting, GuiInfo, MessageValue> messageValue) {
        messageValues.add(messageValue);
        return this;
    }

    public PagedInventoryGuiBuilder withBackAction(BiConsumer<Setting, GuiInfo> backAction) {
        backActions.add(backAction);
        return this;
    }

    public PagedInventoryGuiBuilder addDefaultNextButton() {
        addButtonContext(ButtonContextBuilder.
                builder().
                identifier(NEXT).
                withCondition(guiInfo -> ((PagedGuiInfo) guiInfo).getCurrentPage() < (((PagedGuiInfo) guiInfo).getMaxPages() - 1)).
                button(ButtonBuilder.
                        builder().
                        identifier(NEXT).
                        withAction(clickAction -> clickAction.getClickEvent().setCancelled(true)).
                        withAction(clickAction -> ((PagedInventoryGui<?>) clickAction.getInventoryGui()).openNextInventory(clickAction.getClickEvent().getView().getTopInventory(), clickAction.getClickEvent().getWhoClicked()))));
        return this;
    }

    public PagedInventoryGuiBuilder addDefaultPreviousButton() {
        addButtonContext(ButtonContextBuilder.
                builder().
                identifier(PREVIOUS).
                withCondition(guiInfo -> ((PagedGuiInfo) guiInfo).getCurrentPage() > 0).
                button(ButtonBuilder.
                        builder().
                        identifier(PREVIOUS).
                        withAction(clickAction -> clickAction.getClickEvent().setCancelled(true)).
                        withAction(clickAction -> ((PagedInventoryGui<?>) clickAction.getInventoryGui()).openPreviousInventory(clickAction.getClickEvent().getView().getTopInventory(), clickAction.getClickEvent().getWhoClicked()))));
        return this;
    }

    public InventoryGui build() {
        BiFunction<Setting, GuiInfo, String> name = this.name;
        BiFunction<Setting, GuiInfo, Integer> rows = this.rows;
        BiFunction<Setting, GuiInfo, Boolean> allowBottomPickUp = this.allowBottomPickUp;
        BiFunction<Setting, GuiInfo, Boolean> decorations = this.decorations;
        PageContentBuilder<?> pageContentBuilder = this.pageContentBuilder;
        BiFunction<Setting, GuiInfo, Integer> upperGap = this.upperGap;
        BiFunction<Setting, GuiInfo, Integer> lowerGap = this.lowerGap;
        BiFunction<Setting, GuiInfo, Integer> sideGap = this.sideGap;

        if (rows == null) {
            rows = (setting, guiInfo) -> setting.get(identifier, "rows", 1);
        }

        if (name == null) {
            name = (setting, guiInfo) -> setting.get(identifier, "name", "Not present");
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

        if (pageContentBuilder == null) {
            pageContentBuilder = PageContentBuilder.builder(Object.class);
        }

        if (allowBottomPickUp == null) {
            allowBottomPickUp = (setting, guiInfo) -> setting.get(identifier, "allow-bottom-pickup", false);
        }

        if (decorations == null) {
            decorations = (setting, guiInfo) -> setting.get(identifier, "place-decorations", true);
        }

        PagedInventoryGui<?> gui = new PagedInventoryGui<>(identifier, setting, languageManager, rows, name, upperGap, lowerGap, sideGap, allowBottomPickUp, pageContentBuilder.build(setting, languageManager), decorations, messageValues, backActions);

        buttonContextBuilders.stream().map(builder -> builder.build(setting, languageManager)).forEach(gui::addButtonContext);
        listButtonBuilders.stream().map(builder -> builder.build(setting, languageManager)).forEach(gui::addListButton);

        return gui;
    }
}

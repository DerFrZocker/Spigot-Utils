package de.derfrzocker.spigot.utils.gui;

import de.derfrzocker.spigot.utils.message.MessageUtil;
import de.derfrzocker.spigot.utils.message.MessageValue;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


public class VerifyGui extends BasicGui {

    public VerifyGui(final Plugin plugin, final Consumer<InventoryClickEvent> acceptAction, final Consumer<InventoryClickEvent> denyAction) {
        this(plugin, acceptAction, denyAction, VerifyGuiSettings.getInstance(plugin));
    }

    public VerifyGui(final Plugin plugin, final Consumer<InventoryClickEvent> acceptAction, final Consumer<InventoryClickEvent> denyAction, final MessageValue... messageValues) {
        this(plugin, acceptAction, denyAction, VerifyGuiSettings.getInstance(plugin), messageValues);
    }

    public <T extends BasicSettings & VerifyGui.VerifyGuiSettingsInterface> VerifyGui(final Plugin plugin, final Consumer<InventoryClickEvent> acceptAction, final Consumer<InventoryClickEvent> denyAction, final T setting) {
        super(plugin, setting);
        addItem(setting.getAcceptSlot(), MessageUtil.replaceItemStack(plugin, setting.getAcceptItemStack()), acceptAction);
        addItem(setting.getDenySlot(), MessageUtil.replaceItemStack(plugin, setting.getDenyItemStack()), denyAction);
    }

    public <T extends BasicSettings & VerifyGui.VerifyGuiSettingsInterface> VerifyGui(final Plugin plugin, final Consumer<InventoryClickEvent> acceptAction, final Consumer<InventoryClickEvent> denyAction, final T setting, final MessageValue... messageValues) {
        super(plugin, setting);
        addItem(setting.getAcceptSlot(), MessageUtil.replaceItemStack(plugin, setting.getAcceptItemStack(), messageValues), acceptAction);
        addItem(setting.getDenySlot(), MessageUtil.replaceItemStack(plugin, setting.getDenyItemStack(), messageValues), denyAction);
    }

    public static final class VerifyGuiSettings extends BasicSettings implements VerifyGui.VerifyGuiSettingsInterface {


        private final static Map<Plugin, VerifyGuiSettings> VERIFY_GUI_SETTINGS_MAP = new HashMap<>();

        private VerifyGuiSettings(final Plugin plugin) {
            super(plugin, "data/gui/verify-gui.yml");
        }

        public static VerifyGuiSettings getInstance(final Plugin plugin) {
            return VERIFY_GUI_SETTINGS_MAP.computeIfAbsent(plugin, VerifyGuiSettings::new);
        }

        @Override
        public int getAcceptSlot() {
            return getSection().getInt("accept.slot");
        }

        @Override
        public ItemStack getAcceptItemStack() {
            return getSection().getItemStack("accept.item-stack").clone();
        }

        @Override
        public ItemStack getDenyItemStack() {
            return getSection().getItemStack("deny.item-stack").clone();
        }

        @Override
        public int getDenySlot() {
            return getSection().getInt("deny.slot");
        }

    }

    public interface VerifyGuiSettingsInterface {

        int getAcceptSlot();

        ItemStack getAcceptItemStack();

        ItemStack getDenyItemStack();

        int getDenySlot();

    }

}

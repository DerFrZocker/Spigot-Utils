package de.derfrzocker.spigot.utils.gui;

import de.derfrzocker.spigot.utils.MessageUtil;
import de.derfrzocker.spigot.utils.MessageValue;
import lombok.NonNull;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


public class VerifyGui extends BasicGui {


    public VerifyGui(final @NonNull JavaPlugin plugin, final @NonNull Consumer<InventoryClickEvent> acceptAction, final @NonNull Consumer<InventoryClickEvent> denyAction) {
        this(plugin, acceptAction, denyAction, VerifyGuiSettings.getInstance(plugin));
    }

    public VerifyGui(final @NonNull JavaPlugin plugin, final @NonNull Consumer<InventoryClickEvent> acceptAction, final @NonNull Consumer<InventoryClickEvent> denyAction, final @NonNull MessageValue... messageValues) {
        this(plugin, acceptAction, denyAction, VerifyGuiSettings.getInstance(plugin), messageValues);
    }

    public <T extends BasicSettings & VerifyGui.VerifyGuiSettingsInterface> VerifyGui(final @NonNull JavaPlugin plugin, final @NonNull Consumer<InventoryClickEvent> acceptAction, final @NonNull Consumer<InventoryClickEvent> denyAction, final T setting) {
        super(plugin, setting);
        addItem(setting.getAcceptSlot(), MessageUtil.replaceItemStack(plugin, setting.getAcceptItemStack()), acceptAction);
        addItem(setting.getDenySlot(), MessageUtil.replaceItemStack(plugin, setting.getDenyItemStack()), denyAction);
    }

    public <T extends BasicSettings & VerifyGui.VerifyGuiSettingsInterface> VerifyGui(final @NonNull JavaPlugin plugin, final @NonNull Consumer<InventoryClickEvent> acceptAction, final @NonNull Consumer<InventoryClickEvent> denyAction, final T setting, final @NonNull MessageValue... messageValues) {
        super(plugin, setting);
        addItem(setting.getAcceptSlot(), MessageUtil.replaceItemStack(plugin, setting.getAcceptItemStack(), messageValues), acceptAction);
        addItem(setting.getDenySlot(), MessageUtil.replaceItemStack(plugin, setting.getDenyItemStack(), messageValues), denyAction);
    }

    public static final class VerifyGuiSettings extends BasicSettings implements VerifyGui.VerifyGuiSettingsInterface {


        private final static Map<JavaPlugin, VerifyGuiSettings> VERIFY_GUI_SETTINGS_MAP = new HashMap<>();

        public static VerifyGuiSettings getInstance(final @NonNull JavaPlugin plugin) {
            return VERIFY_GUI_SETTINGS_MAP.computeIfAbsent(plugin, VerifyGuiSettings::new);
        }

        private VerifyGuiSettings(final @NonNull JavaPlugin plugin) {
            super(plugin, "data/verify_gui.yml");
        }

        @Override
        public int getAcceptSlot() {
            return getYaml().getInt("accept.slot");
        }

        @Override
        public ItemStack getAcceptItemStack() {
            return getYaml().getItemStack("accept.item_stack").clone();
        }

        @Override
        public ItemStack getDenyItemStack() {
            return getYaml().getItemStack("deny.item_stack").clone();
        }

        @Override
        public int getDenySlot() {
            return getYaml().getInt("deny.slot");
        }

    }

    public interface VerifyGuiSettingsInterface {

        int getAcceptSlot();

        ItemStack getAcceptItemStack();

        ItemStack getDenyItemStack();

        int getDenySlot();

    }

}

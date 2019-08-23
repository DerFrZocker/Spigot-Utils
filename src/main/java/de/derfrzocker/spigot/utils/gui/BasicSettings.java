package de.derfrzocker.spigot.utils.gui;

import de.derfrzocker.spigot.utils.Config;
import de.derfrzocker.spigot.utils.ReloadAble;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class BasicSettings implements ReloadAble {

    @Getter
    private YamlConfiguration yaml;

    @NonNull
    private final String file;

    @NonNull
    private final JavaPlugin plugin;

    public BasicSettings(final JavaPlugin plugin, final String file) {
        this.file = file;
        this.plugin = plugin;
        yaml = Config.getConfig(plugin, file);
        RELOAD_ABLES.add(this);
    }

    public String getInventoryName() {
        return yaml.getString("inventory.name");
    }

    public int getRows() {
        return yaml.getInt("inventory.rows");
    }

    @Override
    public void reload() {
        yaml = Config.getConfig(plugin, file);
    }

}

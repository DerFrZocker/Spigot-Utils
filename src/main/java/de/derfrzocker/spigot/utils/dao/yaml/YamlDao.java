package de.derfrzocker.spigot.utils.dao.yaml;

import de.derfrzocker.spigot.utils.Config;
import de.derfrzocker.spigot.utils.ReloadAble;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class YamlDao<V> implements ReloadAble {

    @NonNull
    @Getter
    private File file;

    @NonNull
    @Getter
    private YamlConfiguration yaml;

    public YamlDao(final File file) {
        this.file = file;
        yaml = new Config(file);
    }

    public Optional<V> getFromStringKey(final @NonNull String key) {
        final Object object = getYaml().get(key);

        if (object == null)
            return Optional.empty();

        return Optional.of((V) object);
    }

    public void saveFromStringKey(final @NonNull String key, final V value) {
        getYaml().set(key, value);

        try {
            getYaml().save(getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void reload() {
        yaml = new Config(file);

        try {
            yaml.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Unexpected error while save YamlConfiguration to file: " + file, e);
        }
    }

}

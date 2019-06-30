package de.derfrzocker.spigot.utils;

import lombok.Data;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@SerializableAs("DerFrZocker#SpigotUtils#SoundEffect")
public class SoundEffect implements ConfigurationSerializable {

    private final static String SOUND_CATEGORY_KEY = "sound-category";
    private final static String SOUND_KEY = "sound";
    private final static String VOLUME_KEY = "volume";
    private final static String PITCH_KEY = "pitch";
    private final static String X_INCREMENTAL_KEY = "x-incremental";
    private final static String Y_INCREMENTAL_KEY = "y-incremental";
    private final static String Z_INCREMENTAL_KEY = "z-incremental";
    private final static String X_KEY = "x";
    private final static String Y_KEY = "y";
    private final static String Z_KEY = "z";

    @NonNull
    private final SoundCategory soundCategory;
    @NonNull
    private final Sound sound;
    private final float volume;
    private final float pitch;
    private final boolean xIncremental;
    private final boolean yIncremental;
    private final boolean zIncremental;
    private final double x;
    private final double y;
    private final double z;


    public void playSound(Player player) {
        double x = this.x;
        double y = this.y;
        double z = this.z;

        if (xIncremental)
            x += player.getLocation().getX();

        if (yIncremental)
            y += player.getLocation().getY();

        if (zIncremental)
            z += player.getLocation().getZ();

        Location location = new Location(player.getWorld(), x, y, z);

        player.playSound(location, sound, soundCategory, volume, pitch);
    }

    public void playSound() {
        Bukkit.getOnlinePlayers().forEach(this::playSound);
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new LinkedHashMap<>();

        map.put(SOUND_CATEGORY_KEY, getSoundCategory().toString());
        map.put(SOUND_KEY, getSound().toString());
        map.put(VOLUME_KEY, getVolume());
        map.put(PITCH_KEY, getPitch());
        map.put(X_INCREMENTAL_KEY, isXIncremental());
        map.put(Y_INCREMENTAL_KEY, isYIncremental());
        map.put(Z_INCREMENTAL_KEY, isZIncremental());
        map.put(X_KEY, getX());
        map.put(Y_KEY, getY());
        map.put(Z_KEY, getZ());

        return map;
    }

    public static SoundEffect deserialize(Map<String, Object> map) {
        final SoundCategory soundCategory = SoundCategory.valueOf((String) map.get(SOUND_CATEGORY_KEY));
        final Sound sound = Sound.valueOf((String) map.get(SOUND_KEY));
        final float volume = ((Number) map.get(VOLUME_KEY)).floatValue();
        final float pitch = ((Number) map.get(PITCH_KEY)).floatValue();
        final boolean xIncremental = (boolean) map.get(X_INCREMENTAL_KEY);
        final boolean yIncremental = (boolean) map.get(Y_INCREMENTAL_KEY);
        final boolean zIncremental = (boolean) map.get(Z_INCREMENTAL_KEY);
        final double x = ((Number) map.get(X_KEY)).doubleValue();
        final double y = ((Number) map.get(Y_KEY)).doubleValue();
        final double z = ((Number) map.get(Z_KEY)).doubleValue();

        return new SoundEffect(soundCategory, sound, volume, pitch, xIncremental, yIncremental, zIncremental, x, y, z);
    }

    public static SoundEffect deserialize(ConfigurationSection section) {
        final SoundCategory soundCategory = SoundCategory.valueOf(section.getString(SOUND_CATEGORY_KEY).toUpperCase());
        final Sound sound = Sound.valueOf(section.getString(SOUND_KEY).toUpperCase());
        final float volume = ((Number) section.get(VOLUME_KEY)).floatValue();
        final float pitch = ((Number) section.get(PITCH_KEY)).floatValue();
        final boolean xIncremental;
        final boolean yIncremental;
        final boolean zIncremental;
        final double x;
        final double y;
        final double z;

        if (section.isDouble(X_KEY)) {
            x = section.getDouble(X_KEY);
            if (section.contains(X_INCREMENTAL_KEY)) {
                xIncremental = section.getBoolean(X_INCREMENTAL_KEY);
            } else {
                xIncremental = false;
            }
        } else {
            String string = section.getString(X_KEY);
            xIncremental = true;

            if (string.toUpperCase().startsWith("I")) {
                string = string.substring(1);

                try {
                    x = Double.valueOf(string);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid x value: " + string + " in section: " + section.getName(), e);
                }
            } else {
                throw new IllegalArgumentException("Invalid x value: " + string + " in section: " + section.getName());
            }
        }

        if (section.isDouble(Y_KEY)) {
            y = section.getDouble(Y_KEY);
            if (section.contains(Y_INCREMENTAL_KEY)) {
                yIncremental = section.getBoolean(Y_INCREMENTAL_KEY);
            } else {
                yIncremental = false;
            }
        } else {
            String string = section.getString(Y_KEY);
            yIncremental = true;

            if (string.toUpperCase().startsWith("I")) {
                string = string.substring(1);

                try {
                    y = Double.valueOf(string);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid y value: " + string + " in section: " + section.getName(), e);
                }
            } else {
                throw new IllegalArgumentException("Invalid y value: " + string + " in section: " + section.getName());
            }
        }

        if (section.isDouble(Z_KEY)) {
            z = section.getDouble(Z_KEY);
            if (section.contains(Z_INCREMENTAL_KEY)) {
                zIncremental = section.getBoolean(Z_INCREMENTAL_KEY);
            } else {
                zIncremental = false;
            }
        } else {
            String string = section.getString(Z_KEY);
            zIncremental = true;

            if (string.toUpperCase().startsWith("I")) {
                string = string.substring(1);

                try {
                    z = Double.valueOf(string);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid z value: " + string + " in section: " + section.getName(), e);
                }
            } else {
                throw new IllegalArgumentException("Invalid z value: " + string + " in section: " + section.getName());
            }
        }

        return new SoundEffect(soundCategory, sound, volume, pitch, xIncremental, yIncremental, zIncremental, x, y, z);
    }


}

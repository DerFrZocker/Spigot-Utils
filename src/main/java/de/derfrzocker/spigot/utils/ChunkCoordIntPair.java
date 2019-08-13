package de.derfrzocker.spigot.utils;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.NumberConversions;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@RequiredArgsConstructor
@SerializableAs("DerFrZocker#SpigotUtils#ChunkCoordIntPair")
public class ChunkCoordIntPair implements ConfigurationSerializable, Comparable<ChunkCoordIntPair> {

    private final int x;

    private final int z;

    public ChunkCoordIntPair(final @NonNull Location location) {
        this.x = location.getBlockX() >> 4;
        this.z = location.getBlockZ() >> 4;
    }

    @Override
    public int compareTo(ChunkCoordIntPair other) {
        if (getX() < other.getX())
            return 1;

        if (getX() > other.getX())
            return -1;

        return Integer.compare(other.getZ(), getZ());
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new LinkedHashMap<>();

        map.put("x", x);
        map.put("z", z);

        return map;
    }

    public static ChunkCoordIntPair deserialize(Map<String, Object> map) {
        final int x = NumberConversions.toInt(map.get("x"));
        final int z = NumberConversions.toInt(map.get("z"));

        return new ChunkCoordIntPair(x, z);
    }

}

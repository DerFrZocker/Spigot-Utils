package de.derfrzocker.spigot.utils.serialize;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.NumberConversions;

import java.util.Map;

@SerializableAs("DerFrZocker#SpigotUtils#Location")
public class SerializableLocation extends Location {

    private String world;

    public SerializableLocation(World world, double x, double y, double z) {
        super(world, x, y, z);
    }

    public SerializableLocation(World world, double x, double y, double z, float yaw, float pitch) {
        super(world, x, y, z, yaw, pitch);
    }

    public SerializableLocation(Location location) {
        super(location.getWorld(), location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
    }

    private SerializableLocation(String world, double x, double y, double z, float yaw, float pitch) {
        super(null, x, y, z, yaw, pitch);
        this.world = world;

        World world1 = Bukkit.getWorld(world);

        if (world1 != null)
            super.setWorld(world1);
    }

    @Override
    public void setWorld(World world) {
        if (world == null)
            this.world = null;
        else
            this.world = world.getName();

        super.setWorld(world);
    }

    @Override
    public World getWorld() {
        if (super.getWorld() != null)
            return super.getWorld();

        if (world == null)
            return null;

        World world = Bukkit.getWorld(this.world);

        super.setWorld(world);

        return world;
    }

    @Override
    public Chunk getChunk() {
        return getWorld().getChunkAt(this);
    }

    @Override
    public Block getBlock() {
        return getWorld().getBlockAt(this);
    }

    public static SerializableLocation deserialize(Map<String, Object> map) {
        final double x = NumberConversions.toDouble(map.get("x"));
        final double y = NumberConversions.toDouble(map.get("y"));
        final double z = NumberConversions.toDouble(map.get("z"));
        final float yaw = NumberConversions.toFloat(map.get("yaw"));
        final float pitch = NumberConversions.toFloat(map.get("pitch"));
        final String world = (String) map.get("world");

        return new SerializableLocation(world, x, y, z, yaw, pitch);
    }

}

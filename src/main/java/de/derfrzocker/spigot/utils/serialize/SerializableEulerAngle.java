package de.derfrzocker.spigot.utils.serialize;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.NumberConversions;

import java.util.LinkedHashMap;
import java.util.Map;

@SerializableAs("DerFrZocker#SpigotUtils#EulerAngle")
public class SerializableEulerAngle extends EulerAngle implements ConfigurationSerializable {

    public SerializableEulerAngle(double x, double y, double z) {
        super(x, y, z);
    }

    public SerializableEulerAngle(EulerAngle eulerAngle) {
        super(eulerAngle.getX(), eulerAngle.getY(), eulerAngle.getZ());
    }

    @Override
    public SerializableEulerAngle setX(double x) {
        return new SerializableEulerAngle(x, getY(), getZ());
    }

    @Override
    public SerializableEulerAngle setY(double y) {
        return new SerializableEulerAngle(getX(), y, getZ());
    }

    @Override
    public SerializableEulerAngle setZ(double z) {
        return new SerializableEulerAngle(getX(), getY(), z);
    }

    @Override
    public SerializableEulerAngle add(double x, double y, double z) {
        return new SerializableEulerAngle(
                getX() + x,
                getY() + y,
                getZ() + z
        );
    }

    @Override
    public SerializableEulerAngle subtract(double x, double y, double z) {
        return add(-x, -y, -z);
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new LinkedHashMap<>();

        map.put("x", getX());
        map.put("y", getY());
        map.put("z", getZ());

        return map;
    }

    public static SerializableEulerAngle deserialize(Map<String, Object> map) {
        final double x = NumberConversions.toDouble(map.get("x"));
        final double y = NumberConversions.toDouble(map.get("y"));
        final double z = NumberConversions.toDouble(map.get("z"));

        return new SerializableEulerAngle(x, y, z);
    }

}

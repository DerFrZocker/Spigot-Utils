package de.derfrzocker.spigot.utils;

import lombok.Data;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.util.EulerAngle;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@SerializableAs("DerFrZocker#SpigotUtils#EulerAngle")
public class SerializableEulerAngle implements ConfigurationSerializable {

    private final static String X_KEY = "x";
    private final static String Y_KEY = "y";
    private final static String Z_KEY = "z";

    private final double x;
    private final double y;
    private final double z;

    public EulerAngle toEulerAngle(){
        return new EulerAngle(x,y,z);
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new LinkedHashMap<>();

        map.put(X_KEY, x);
        map.put(Y_KEY, y);
        map.put(Z_KEY, z);

        return map;
    }

    public static SerializableEulerAngle deserialize(Map<String, Object> map) {
        final double x = ((Number) map.get(X_KEY)).doubleValue();
        final double y = ((Number) map.get(Y_KEY)).doubleValue();
        final double z = ((Number) map.get(Z_KEY)).doubleValue();

        return new SerializableEulerAngle( x, y, z);
    }

    public static EulerAngle toEulerAngle(SerializableEulerAngle serializableEulerAngle){
        return serializableEulerAngle.toEulerAngle();
    }

    public static SerializableEulerAngle toSerializableEulerAngle(EulerAngle eulerAngle){
        return new SerializableEulerAngle(eulerAngle.getX(), eulerAngle.getY(), eulerAngle.getZ());
    }

}

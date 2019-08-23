package de.derfrzocker.spigot.utils;

import de.derfrzocker.spigot.utils.serialize.SerializableEulerAngle;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.SerializableAs;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
@RequiredArgsConstructor
@SerializableAs("DerFrZocker#SpigotUtils#ArmorStandProperties")
public class ArmorStandProperties implements ConfigurationSerializable {

    private final static String RELATIVE_X_KEY = "relative-x";
    private final static String RELATIVE_Y_KEY = "relative-y";
    private final static String RELATIVE_Z_KEY = "relative-z";

    private final static String VISIBLE_KEY = "visible";
    private final static String ARMS_KEY = "arms";
    private final static String BASE_PLATE_KEY = "base-plate";
    private final static String MARKER_KEY = "marker";
    private final static String SMALL_KEY = "small";
    private final static String CUSTOM_NAME_VISIBLE_KEY = "custom-name-visible";
    private final static String AI_KEY = "ai";
    private final static String CAN_PICKUP_ITEMS_KEY = "can-pick-up-items";
    private final static String COLLIDABLE_KEY = "collidable";
    private final static String GLOWING_KEY = "glowing";
    private final static String GRAVITY_KEY = "gravity";
    private final static String INVULNERABLE_KEY = "invulnerable";
    private final static String SILENT_KEY = "silent";

    private final static String CUSTOM_NAME_KEY = "custom-name";

    private final static String RIGHT_LEG_POSE_KEY = "right-leg-pose";
    private final static String LEFT_LEG_POSE_KEY = "left-leg-pose";
    private final static String BOOTS_KEY = "boots";
    private final static String LEGGINGS_KEY = "leggings";

    private final static String RIGHT_ARM_POSE_KEY = "right-arm-pose";
    private final static String LEFT_ARM_POSE_KEY = "left-arm-pose";
    private final static String MAIN_HAND_KEY = "main-hand";
    private final static String OFF_HAND_KEY = "off-hand";

    private final static String BODY_POSE_KEY = "body-pose";
    private final static String CHEST_PLATE_KEY = "chest-plate";

    private final static String HEAD_POSE_KEY = "head-pose";
    private final static String HELMET_KEY = "helmet";


    private final double relativeX;
    private final double relativeY;
    private final double relativeZ;

    private final boolean visible;
    private final boolean arms;
    private final boolean basePlate;
    private final boolean marker;
    private final boolean small;
    private final boolean customNameVisible;
    private final boolean ai;
    private final boolean canPickupItems;
    private final boolean collidable;
    private final boolean glowing;
    private final boolean gravity;
    private final boolean invulnerable;
    private final boolean silent;

    private final String customName;

    // Leg
    private final EulerAngle rightLegPose;
    private final EulerAngle leftLegPose;
    private final ItemStack boots;
    private final ItemStack leggings;

    // Arm
    private final EulerAngle rightArmPose;
    private final EulerAngle leftArmPose;
    private final ItemStack mainHand;
    private final ItemStack offHand;

    //Body
    private final EulerAngle bodyPose;
    private final ItemStack chestPlate;

    //Head
    private final EulerAngle headPose;
    private final ItemStack helmet;

    public ArmorStandProperties(final @NonNull ArmorStand armorStand, final double relativeX, final double relativeY, final double relativeZ) {
        this.relativeX = relativeX;
        this.relativeY = relativeY;
        this.relativeZ = relativeZ;

        this.visible = armorStand.isVisible();
        this.arms = armorStand.hasArms();
        this.basePlate = armorStand.hasBasePlate();
        this.marker = armorStand.isMarker();
        this.small = armorStand.isSmall();
        this.customNameVisible = armorStand.isCustomNameVisible();
        this.ai = armorStand.hasAI();
        this.canPickupItems = armorStand.getCanPickupItems();
        this.collidable = armorStand.isCollidable();
        this.glowing = armorStand.isGlowing();
        this.gravity = armorStand.hasGravity();
        this.invulnerable = armorStand.isInvulnerable();
        this.silent = armorStand.isSilent();

        this.customName = armorStand.getCustomName();

        this.rightLegPose = armorStand.getRightLegPose();
        this.leftLegPose = armorStand.getLeftLegPose();
        this.boots = armorStand.getBoots();
        this.leggings = armorStand.getLeggings();

        this.rightArmPose = armorStand.getRightArmPose();
        this.leftArmPose = armorStand.getLeftArmPose();
        this.mainHand = armorStand.getEquipment().getItemInMainHand();
        this.offHand = armorStand.getEquipment().getItemInOffHand();

        this.bodyPose = armorStand.getBodyPose();
        this.chestPlate = armorStand.getChestplate();

        this.headPose = armorStand.getHeadPose();
        this.helmet = armorStand.getHelmet();
    }

    public ArmorStand spawn(final @NonNull Location location) {
        return location.getWorld().spawn(location.clone().add(getRelativeX(), getRelativeY(), getRelativeZ()), ArmorStand.class, this::apply);
    }

    public void apply(final @NonNull ArmorStand armorStand) {
        armorStand.setVisible(isVisible());
        armorStand.setArms(isArms());
        armorStand.setBasePlate(isBasePlate());
        armorStand.setMarker(isMarker());
        armorStand.setSmall(isSmall());
        armorStand.setCustomNameVisible(isCustomNameVisible());
        armorStand.setAI(isAi());
        armorStand.setCanPickupItems(isCanPickupItems());
        armorStand.setCollidable(isCollidable());
        armorStand.setGlowing(isGlowing());
        armorStand.setGravity(isGravity());
        armorStand.setInvulnerable(isInvulnerable());
        armorStand.setSilent(isSilent());

        armorStand.setCustomName(getCustomName());

        armorStand.setRightLegPose(getRightLegPose());
        armorStand.setLeftLegPose(getLeftLegPose());
        armorStand.setBoots(getBoots());
        armorStand.setLeggings(getLeggings());

        armorStand.setRightArmPose(getRightArmPose());
        armorStand.setLeftArmPose(getLeftArmPose());
        armorStand.getEquipment().setItemInMainHand(getMainHand());
        armorStand.getEquipment().setItemInOffHand(getOffHand());

        armorStand.setBodyPose(getBodyPose());
        armorStand.setChestplate(getChestPlate());

        armorStand.setHeadPose(getHeadPose());
        armorStand.setHelmet(getHelmet());
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = new LinkedHashMap<>();

        map.put(RELATIVE_X_KEY, getRelativeX());
        map.put(RELATIVE_Y_KEY, getRelativeY());
        map.put(RELATIVE_Z_KEY, getRelativeZ());

        map.put(VISIBLE_KEY, isVisible());
        map.put(ARMS_KEY, isArms());
        map.put(BASE_PLATE_KEY, isBasePlate());
        map.put(MARKER_KEY, isMarker());
        map.put(SMALL_KEY, isSmall());
        map.put(CUSTOM_NAME_VISIBLE_KEY, isCustomNameVisible());
        map.put(AI_KEY, isAi());
        map.put(CAN_PICKUP_ITEMS_KEY, isCanPickupItems());
        map.put(COLLIDABLE_KEY, isCollidable());
        map.put(GLOWING_KEY, isGlowing());
        map.put(GRAVITY_KEY, isGravity());
        map.put(INVULNERABLE_KEY, isInvulnerable());
        map.put(SILENT_KEY, isSilent());

        map.put(CUSTOM_NAME_KEY, getCustomName());

        map.put(RIGHT_LEG_POSE_KEY, new SerializableEulerAngle(getRightLegPose()));
        map.put(LEFT_LEG_POSE_KEY, new SerializableEulerAngle(getLeftLegPose()));
        map.put(BOOTS_KEY, getBoots());
        map.put(LEGGINGS_KEY, getLeggings());

        map.put(RIGHT_ARM_POSE_KEY, new SerializableEulerAngle(getRightArmPose()));
        map.put(LEFT_ARM_POSE_KEY, new SerializableEulerAngle(getLeftArmPose()));
        map.put(MAIN_HAND_KEY, getMainHand());
        map.put(OFF_HAND_KEY, getOffHand());

        map.put(BODY_POSE_KEY, new SerializableEulerAngle(getBodyPose()));
        map.put(CHEST_PLATE_KEY, getChestPlate());

        map.put(HEAD_POSE_KEY, new SerializableEulerAngle(getHeadPose()));
        map.put(HELMET_KEY, getHelmet());

        return map;
    }

    public static ArmorStandProperties deserialize(final @NonNull Map<String, Object> map) {
        final double relativeX = ((Number) map.get(RELATIVE_X_KEY)).doubleValue();
        final double relativeY = ((Number) map.get(RELATIVE_Y_KEY)).doubleValue();
        final double relativeZ = ((Number) map.get(RELATIVE_Z_KEY)).doubleValue();

        final boolean visible = (boolean) map.get(VISIBLE_KEY);
        final boolean arms = (boolean) map.get(ARMS_KEY);
        final boolean basePlate = (boolean) map.get(BASE_PLATE_KEY);
        final boolean marker = (boolean) map.get(MARKER_KEY);
        final boolean small = (boolean) map.get(SMALL_KEY);
        final boolean customNameVisible = (boolean) map.get(CUSTOM_NAME_VISIBLE_KEY);
        final boolean ai = (boolean) map.get(AI_KEY);
        final boolean canPickUpItems = (boolean) map.get(CAN_PICKUP_ITEMS_KEY);
        final boolean collidable = (boolean) map.get(COLLIDABLE_KEY);
        final boolean glowing = (boolean) map.get(GLOWING_KEY);
        final boolean gravity = (boolean) map.get(GRAVITY_KEY);
        final boolean invulnerable = (boolean) map.get(INVULNERABLE_KEY);
        final boolean silent = (boolean) map.get(SILENT_KEY);

        final String customName = (String) map.get(CUSTOM_NAME_KEY);

        final EulerAngle rightLegPose = ((SerializableEulerAngle) map.get(RIGHT_LEG_POSE_KEY));
        final EulerAngle leftLegPose = ((SerializableEulerAngle) map.get(LEFT_LEG_POSE_KEY));
        final ItemStack boots = (ItemStack) map.get(BOOTS_KEY);
        final ItemStack leggings = (ItemStack) map.get(LEGGINGS_KEY);

        final EulerAngle rightArmPose = ((SerializableEulerAngle) map.get(RIGHT_ARM_POSE_KEY));
        final EulerAngle leftArmPose = ((SerializableEulerAngle) map.get(LEFT_ARM_POSE_KEY));
        final ItemStack mainHand = (ItemStack) map.get(MAIN_HAND_KEY);
        final ItemStack offHand = (ItemStack) map.get(OFF_HAND_KEY);

        final EulerAngle bodyPose = ((SerializableEulerAngle) map.get(BODY_POSE_KEY));
        final ItemStack chestPlate = (ItemStack) map.get(CHEST_PLATE_KEY);

        final EulerAngle headPose = ((SerializableEulerAngle) map.get(HEAD_POSE_KEY));
        final ItemStack helmet = (ItemStack) map.get(HELMET_KEY);

        return new ArmorStandProperties(relativeX, relativeY, relativeZ, visible, arms, basePlate, marker, small, customNameVisible, ai, canPickUpItems, collidable, glowing, gravity, invulnerable, silent, customName, rightLegPose, leftLegPose, boots, leggings, rightArmPose, leftArmPose, mainHand, offHand, bodyPose, chestPlate, headPose, helmet);
    }

}

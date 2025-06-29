package de.derfrzocker.spigot.utils.version;

public record ServerVersionRange(ServerVersion minInclusive, ServerVersion maxInclusive) {

    public static final ServerVersionRange V1_21 = create("1.21", "1.21.6");
    public static final ServerVersionRange V1_20 = create("1.20", "1.20.6");
    public static final ServerVersionRange V1_19 = create("1.19", "1.19.4");
    public static final ServerVersionRange V1_18 = create("1.18", "1.18.2");
    public static final ServerVersionRange V1_17 = create("1.17", "1.17.1");
    public static final ServerVersionRange V1_16 = create("1.16", "1.16.5");
    public static final ServerVersionRange V1_15 = create("1.15", "1.15.2");
    public static final ServerVersionRange V1_14 = create("1.14", "1.14.4");
    public static final ServerVersionRange V1_13 = create("1.13", "1.13.2");
    public static final ServerVersionRange V1_12 = create("1.12", "1.12.2");
    public static final ServerVersionRange V1_11 = create("1.11", "1.11.2");
    public static final ServerVersionRange V1_10 = create("1.10", "1.10.2");
    public static final ServerVersionRange V1_9 = create("1.9", "1.9.4");
    public static final ServerVersionRange V1_8 = create("1.8", "1.8.9");

    public boolean isInRange(ServerVersion version) {
        return version.isNewerThanOrSameAs(minInclusive) && version.isOlderThanOrSameAs(maxInclusive);
    }

    public static ServerVersionRange create(String minInclusive, String maxInclusive) {
        return new ServerVersionRange(ServerVersion.getOrCreateVersion(minInclusive), ServerVersion.getOrCreateVersion(maxInclusive));
    }

    @Override
    public String toString() {
        return minInclusive + "-" + maxInclusive;
    }
}

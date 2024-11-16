package de.derfrzocker.spigot.utils.version;

public enum InternalVersion {

    v1_21_R2(ServerVersionRange.create("1.21.2", "1.21.3")),
    v1_21_R1(ServerVersionRange.create("1.21", "1.21.1")),
    v1_20_R4(ServerVersionRange.create("1.20.5", "1.20.6")),
    v1_20_R3(ServerVersionRange.create("1.20.3", "1.20.4")),
    v1_20_R2(ServerVersionRange.create("1.20.2", "1.20.2")),
    v1_20_R1(ServerVersionRange.create("1.20", "1.20.1")),
    v1_19_R3(ServerVersionRange.create("1.19.4", "1.19.4")),
    v1_19_R2(ServerVersionRange.create("1.19.3", "1.19.3")),
    v1_19_R1(ServerVersionRange.create("1.19", "1.19.2")),
    v1_18_R2(ServerVersionRange.create("1.18.2", "1.18.2")),
    v1_18_R1(ServerVersionRange.create("1.18", "1.18.1")),
    v1_17_R1(ServerVersionRange.V1_17),
    v1_16_R3(ServerVersionRange.create("1.16.4", "1.16.5")),
    v1_16_R2(ServerVersionRange.create("1.16.2", "1.16.3")),
    v1_16_R1(ServerVersionRange.create("1.16", "1.16.1")),
    v1_15_R1(ServerVersionRange.V1_15),
    v1_14_R1(ServerVersionRange.V1_14),
    v1_13_R2(ServerVersionRange.create("1.13.1", "1.13.2")),
    v1_13_R1(ServerVersionRange.create("1.13", "1.13")),
    v1_12_R1(ServerVersionRange.V1_12),
    v1_11_R1(ServerVersionRange.V1_11),
    v1_10_R1(ServerVersionRange.V1_10),
    v1_9_R2(ServerVersionRange.create("1.9.4", "1.9.4")),
    v1_9_R1(ServerVersionRange.create("1.9", "1.9.3")),
    v1_8_R3(ServerVersionRange.create("1.8.4", "1.8.9")),
    v1_8_R2(ServerVersionRange.create("1.8.3", "1.8.3")),
    v1_8_R1(ServerVersionRange.create("1.8", "1.8.2")),
    UNKNOWN(new ServerVersionRange(ServerVersion.NONE, ServerVersion.NONE));

    private final ServerVersionRange serverVersionRange;

    InternalVersion(ServerVersionRange serverVersionRange) {
        this.serverVersionRange = serverVersionRange;
    }

    public ServerVersionRange getServerVersionRange() {
        return this.serverVersionRange;
    }
}

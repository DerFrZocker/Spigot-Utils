package de.derfrzocker.spigot.utils;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class VersionTest {

    public static Collection<VersionPair> newerThanData() {
        Set<VersionPair> set = new LinkedHashSet<>();

        set.add(new VersionPair(Version.v1_16_R2, Version.v1_8_R1, true));
        set.add(new VersionPair(Version.v1_8_R1, Version.v1_16_R2, false));
        set.add(new VersionPair(Version.v1_16_R2, Version.v1_16_R2, false));
        set.add(new VersionPair(Version.v1_8_R1, Version.v1_8_R1, false));
        set.add(new VersionPair(Version.v1_13_R2, Version.v1_14_R1, false));
        set.add(new VersionPair(Version.v1_14_R1, Version.v1_13_R2, true));
        set.add(new VersionPair(Version.v1_14_R1, Version.v1_14_R1, false));
        set.add(new VersionPair(Version.v1_13_R2, Version.v1_13_R2, false));
        set.add(new VersionPair(Version.v1_15_R1, Version.v1_10_R1, true));
        set.add(new VersionPair(Version.v1_10_R1, Version.v1_15_R1, false));
        set.add(new VersionPair(Version.v1_10_R1, Version.v1_10_R1, false));
        set.add(new VersionPair(Version.v1_15_R1, Version.v1_15_R1, false));

        return set;
    }

    public static Collection<VersionPair> newerOrSameThanData() {
        Set<VersionPair> set = new LinkedHashSet<>();

        set.add(new VersionPair(Version.v1_16_R2, Version.v1_8_R1, true));
        set.add(new VersionPair(Version.v1_8_R1, Version.v1_16_R2, false));
        set.add(new VersionPair(Version.v1_16_R2, Version.v1_16_R2, true));
        set.add(new VersionPair(Version.v1_8_R1, Version.v1_8_R1, true));
        set.add(new VersionPair(Version.v1_14_R1, Version.v1_13_R2, true));
        set.add(new VersionPair(Version.v1_13_R2, Version.v1_14_R1, false));
        set.add(new VersionPair(Version.v1_14_R1, Version.v1_14_R1, true));
        set.add(new VersionPair(Version.v1_13_R2, Version.v1_13_R2, true));
        set.add(new VersionPair(Version.v1_15_R1, Version.v1_10_R1, true));
        set.add(new VersionPair(Version.v1_10_R1, Version.v1_15_R1, false));
        set.add(new VersionPair(Version.v1_10_R1, Version.v1_10_R1, true));
        set.add(new VersionPair(Version.v1_15_R1, Version.v1_15_R1, true));

        return set;
    }

    public static Collection<VersionPair> olderThanData() {
        Set<VersionPair> set = new LinkedHashSet<>();

        set.add(new VersionPair(Version.v1_16_R2, Version.v1_8_R1, false));
        set.add(new VersionPair(Version.v1_8_R1, Version.v1_16_R2, true));
        set.add(new VersionPair(Version.v1_16_R2, Version.v1_16_R2, false));
        set.add(new VersionPair(Version.v1_8_R1, Version.v1_8_R1, false));
        set.add(new VersionPair(Version.v1_13_R2, Version.v1_14_R1, true));
        set.add(new VersionPair(Version.v1_14_R1, Version.v1_13_R2, false));
        set.add(new VersionPair(Version.v1_14_R1, Version.v1_14_R1, false));
        set.add(new VersionPair(Version.v1_13_R2, Version.v1_13_R2, false));
        set.add(new VersionPair(Version.v1_15_R1, Version.v1_10_R1, false));
        set.add(new VersionPair(Version.v1_10_R1, Version.v1_15_R1, true));
        set.add(new VersionPair(Version.v1_10_R1, Version.v1_10_R1, false));
        set.add(new VersionPair(Version.v1_15_R1, Version.v1_15_R1, false));

        return set;
    }

    public static Collection<VersionPair> olderOrSameThanData() {
        Set<VersionPair> set = new LinkedHashSet<>();

        set.add(new VersionPair(Version.v1_16_R2, Version.v1_8_R1, false));
        set.add(new VersionPair(Version.v1_8_R1, Version.v1_16_R2, true));
        set.add(new VersionPair(Version.v1_16_R2, Version.v1_16_R2, true));
        set.add(new VersionPair(Version.v1_8_R1, Version.v1_8_R1, true));
        set.add(new VersionPair(Version.v1_13_R2, Version.v1_14_R1, true));
        set.add(new VersionPair(Version.v1_14_R1, Version.v1_13_R2, false));
        set.add(new VersionPair(Version.v1_14_R1, Version.v1_14_R1, true));
        set.add(new VersionPair(Version.v1_13_R2, Version.v1_13_R2, true));
        set.add(new VersionPair(Version.v1_15_R1, Version.v1_10_R1, false));
        set.add(new VersionPair(Version.v1_10_R1, Version.v1_15_R1, true));
        set.add(new VersionPair(Version.v1_10_R1, Version.v1_10_R1, true));
        set.add(new VersionPair(Version.v1_15_R1, Version.v1_15_R1, true));

        return set;
    }

    @Test
    public void givingNullValueShouldThrowIllegalArgumentException() {
        for (Version version : Version.values()) {
            assertThrows(IllegalArgumentException.class, () -> version.isNewerThan(null));
            assertThrows(IllegalArgumentException.class, () -> version.isNewerOrSameThan(null));
            assertThrows(IllegalArgumentException.class, () -> version.isOlderThan(null));
            assertThrows(IllegalArgumentException.class, () -> version.isOlderOrSameThan(null));
        }

        assertThrows(IllegalArgumentException.class, () -> Version.getServerVersion(null));
        assertThrows(IllegalArgumentException.class, () -> Version.isPaper(null));
    }

    @ParameterizedTest
    @EnumSource(value = Version.class, names = "UNKNOWN", mode = EnumSource.Mode.EXCLUDE)
    public void usingVersionUnknownAsBaseVersionShouldThrowIllegalArgumentException(Version version) {
        assertThrows(IllegalArgumentException.class, () -> Version.UNKNOWN.isNewerThan(version));
        assertThrows(IllegalArgumentException.class, () -> Version.UNKNOWN.isNewerOrSameThan(version));
        assertThrows(IllegalArgumentException.class, () -> Version.UNKNOWN.isOlderThan(version));
        assertThrows(IllegalArgumentException.class, () -> Version.UNKNOWN.isOlderOrSameThan(version));
    }

    @ParameterizedTest
    @EnumSource(value = Version.class, names = "UNKNOWN", mode = EnumSource.Mode.EXCLUDE)
    public void usingVersionUnknownAsValueShouldThrowIllegalArgumentException(Version version) {
        assertThrows(IllegalArgumentException.class, () -> version.isNewerThan(Version.UNKNOWN));
        assertThrows(IllegalArgumentException.class, () -> version.isNewerOrSameThan(Version.UNKNOWN));
        assertThrows(IllegalArgumentException.class, () -> version.isOlderThan(Version.UNKNOWN));
        assertThrows(IllegalArgumentException.class, () -> version.isOlderOrSameThan(Version.UNKNOWN));
    }

    @ParameterizedTest
    @MethodSource("newerThanData")
    public void testingIsNewerThanShouldReturnPredefinedResult(VersionPair versionPair) {
        assertEquals(versionPair.getFirst().isNewerThan(versionPair.getSecond()), versionPair.getResult());
    }

    @ParameterizedTest
    @MethodSource("newerOrSameThanData")
    public void testingIsNewerOrSameThanShouldReturnPredefinedResult(VersionPair versionPair) {
        assertEquals(versionPair.getFirst().isNewerOrSameThan(versionPair.getSecond()), versionPair.getResult());
    }

    @ParameterizedTest
    @MethodSource("olderThanData")
    public void testingIsOlderThanShouldReturnPredefinedResult(VersionPair versionPair) {
        assertEquals(versionPair.getFirst().isOlderThan(versionPair.getSecond()), versionPair.getResult());
    }

    @ParameterizedTest
    @MethodSource("olderOrSameThanData")
    public void testingIsOlderOrSameThanShouldReturnPredefinedResult(VersionPair versionPair) {
        assertEquals(versionPair.getFirst().isOlderOrSameThan(versionPair.getSecond()), versionPair.getResult());
    }

    public static class VersionPair {

        private final Version first;
        private final Version second;
        private final boolean result;


        public VersionPair(Version first, Version second, boolean result) {
            this.first = first;
            this.second = second;
            this.result = result;
        }

        public Version getFirst() {
            return first;
        }

        public Version getSecond() {
            return second;
        }

        public boolean getResult() {
            return result;
        }

    }

}

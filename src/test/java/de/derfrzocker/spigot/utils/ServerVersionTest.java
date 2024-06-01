package de.derfrzocker.spigot.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.stream.Stream;
import org.bukkit.Server;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class ServerVersionTest {

    public static Stream<Arguments> parseData() {
        return Stream.of(
                Arguments.of(null, "none"),
                Arguments.of("", "none"),
                Arguments.of("none", "none"),
                Arguments.of("1.12", "1.12.0"),
                Arguments.of("1.13.3", "1.13.3"),
                Arguments.of("1.+20.3", "1.20.3")
        );
    }

    public static Stream<Arguments> compareData() {
        return Stream.of(
                Arguments.of("none", "none", CompareResult.SAME),
                Arguments.of("none", "1.20", CompareResult.SMALLER),
                Arguments.of("2.20.3", "1.30.4", CompareResult.BIGGER),
                Arguments.of("1.13", "1.12", CompareResult.BIGGER),
                Arguments.of("1.13.2", "1.13.3", CompareResult.SMALLER)
        );
    }

    public static Stream<Arguments> newerData() {
        return Stream.of(
                Arguments.of("1.12", "1.12", false),
                Arguments.of("1.12", "1.12.2", false),
                Arguments.of("1.12.2", "1.12", true)
        );
    }

    public static Stream<Arguments> olderData() {
        return Stream.of(
                Arguments.of("1.12", "1.12", false),
                Arguments.of("1.12", "1.12.2", true),
                Arguments.of("1.12.2", "1.12", false)
        );
    }

    public static Stream<Arguments> newerOrSameData() {
        return Stream.of(
                Arguments.of("1.12", "1.12", true),
                Arguments.of("1.12", "1.12.2", false),
                Arguments.of("1.12.2", "1.12", true)
        );
    }

    public static Stream<Arguments> olderOrSameData() {
        return Stream.of(
                Arguments.of("1.12", "1.12", true),
                Arguments.of("1.12", "1.12.2", true),
                Arguments.of("1.12.2", "1.12", false)
        );
    }

    @Test
    public void testCurrentVersion() {
        Server server = mock();
        when(server.getBukkitVersion()).thenReturn("1.20.6-R0.1-SNAPSHOT");

        ServerVersion apiVersionOne = ServerVersion.getCurrentVersion(server);
        ServerVersion apiVersionTwo = ServerVersion.getOrCreateVersion("1.20.6");

        assertEquals(apiVersionOne, apiVersionTwo);
    }

    @ParameterizedTest
    @MethodSource("parseData")
    public void testParsing(String parse, String expected) {
        ServerVersion apiVersion = ServerVersion.getOrCreateVersion(parse);

        assertEquals(expected, apiVersion.getVersionString());
    }

    @Test
    public void testSameInstance() {
        ServerVersion one = ServerVersion.getOrCreateVersion("1.23.3");
        ServerVersion second = ServerVersion.getOrCreateVersion("1.+23.3");

        assertSame(one, second);
    }

    @ParameterizedTest
    @MethodSource("compareData")
    public void testCompareTo(String first, String second, CompareResult compareResult) {
        ServerVersion firstApi = ServerVersion.getOrCreateVersion(first);
        ServerVersion secondApi = ServerVersion.getOrCreateVersion(second);


        int result = firstApi.compareTo(secondApi);

        assertSame(compareResult, CompareResult.toCompareResult(result));
    }

    @ParameterizedTest
    @MethodSource("newerData")
    public void testNewerThan(String first, String second, boolean newer) {
        ServerVersion firstApi = ServerVersion.getOrCreateVersion(first);
        ServerVersion secondApi = ServerVersion.getOrCreateVersion(second);


        boolean result = firstApi.isNewerThan(secondApi);

        assertSame(newer, result);
    }

    @ParameterizedTest
    @MethodSource("olderData")
    public void testOlderThan(String first, String second, boolean older) {
        ServerVersion firstApi = ServerVersion.getOrCreateVersion(first);
        ServerVersion secondApi = ServerVersion.getOrCreateVersion(second);


        boolean result = firstApi.isOlderThan(secondApi);

        assertSame(older, result);
    }

    @ParameterizedTest
    @MethodSource("newerOrSameData")
    public void testNewerOrSame(String first, String second, boolean newerOrSame) {
        ServerVersion firstApi = ServerVersion.getOrCreateVersion(first);
        ServerVersion secondApi = ServerVersion.getOrCreateVersion(second);


        boolean result = firstApi.isNewerThanOrSameAs(secondApi);

        assertSame(newerOrSame, result);
    }

    @ParameterizedTest
    @MethodSource("olderOrSameData")
    public void testOlderOrSame(String first, String second, boolean olderOrSame) {
        ServerVersion firstApi = ServerVersion.getOrCreateVersion(first);
        ServerVersion secondApi = ServerVersion.getOrCreateVersion(second);


        boolean result = firstApi.isOlderThanOrSameAs(secondApi);

        assertSame(olderOrSame, result);
    }

    public enum CompareResult {
        SMALLER,
        BIGGER,
        SAME;

        public static CompareResult toCompareResult(int i) {
            if (i == 0) {
                return CompareResult.SAME;
            }

            if (i < 0) {
                return CompareResult.SMALLER;
            }

            return CompareResult.BIGGER;
        }
    }
}

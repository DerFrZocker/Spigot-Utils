package de.derfrzocker.spigot.utils.version;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ServerVersion implements Comparable<ServerVersion> {

    public static final ServerVersion NONE;

    private static final Map<String, ServerVersion> versions;

    static {
        versions = new HashMap<>();
        NONE = getOrCreateVersion("none");
    }

    private final boolean none;
    private final int major;
    private final int minor;
    private final int patch;

    private ServerVersion() {
        this.none = true;
        this.major = Integer.MIN_VALUE;
        this.minor = Integer.MIN_VALUE;
        this.patch = Integer.MIN_VALUE;
    }

    private ServerVersion(int major, int minor, int patch) {
        this.none = false;
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public static boolean isSupportedVersion(@Nullable Logger logger, @NotNull ServerVersion serverVersion, @NotNull ServerVersionRange... supportedVersions) {
        for (ServerVersionRange version : supportedVersions) {
            if (version.isInRange(serverVersion)) {
                return true;
            }
        }

        if (logger == null) {
            return false;
        }

        logger.warning(String.format("The Server version which you are running is unsupported, you are running version '%s'.", serverVersion));
        logger.warning(String.format("The plugin supports following versions %s.", combineVersions(supportedVersions)));

        logger.log(Level.WARNING, "No compatible Server version found!", new IllegalStateException("No compatible Server version found!"));

        return false;
    }

    @NotNull
    private static String combineVersions(@NotNull ServerVersionRange... versions) {
        StringBuilder stringBuilder = new StringBuilder();

        boolean first = true;

        for (ServerVersionRange version : versions) {
            if (first) {
                first = false;
            } else {
                stringBuilder.append(", ");
            }

            stringBuilder.append(version);
        }

        return stringBuilder.toString();
    }

    public static ServerVersion getCurrentVersion(Server server) {
        String version = server.getBukkitVersion();
        version = version.substring(0, version.indexOf('-'));

        return getOrCreateVersion(version);
    }

    public static ServerVersion getOrCreateVersion(String versionString) {
        if (versionString == null || versionString.trim().isEmpty() || versionString.equalsIgnoreCase("none")) {
            return versions.computeIfAbsent("none", s -> new ServerVersion());
        }

        ServerVersion version = versions.get(versionString);

        if (version != null) {
            return version;
        }

        String[] versionParts = versionString.split("\\.");

        if (versionParts.length != 2 && versionParts.length != 3) {
            throw new IllegalArgumentException(String.format("API version string should be of format \"major.minor.patch\" or \"major.minor\", where \"major\", \"minor\" and \"patch\" are numbers. For example \"1.18.2\" or \"1.13\", but got '%s' instead.", versionString));
        }

        int major = parseNumber(versionParts[0]);
        int minor = parseNumber(versionParts[1]);

        int patch;
        if (versionParts.length == 3) {
            patch = parseNumber(versionParts[2]);
        } else {
            patch = 0;
        }

        versionString = toVersionString(major, minor, patch);
        return versions.computeIfAbsent(versionString, s -> new ServerVersion(major, minor, patch));
    }

    private static int parseNumber(String number) {
        return Integer.parseInt(number);
    }

    private static String toVersionString(int major, int minor, int patch) {
        return major + "." + minor + "." + patch;
    }

    @Override
    public int compareTo(@NotNull ServerVersion other) {
        int result = Integer.compare(major, other.major);

        if (result == 0) {
            result = Integer.compare(minor, other.minor);
        }

        if (result == 0) {
            result = Integer.compare(patch, other.patch);
        }

        return result;
    }

    public String getVersionString() {
        if (none) {
            return "none";
        }

        return toVersionString(major, minor, patch);
    }

    public boolean isNewerThan(ServerVersion apiVersion) {
        return compareTo(apiVersion) > 0;
    }

    public boolean isOlderThan(ServerVersion apiVersion) {
        return compareTo(apiVersion) < 0;
    }

    public boolean isNewerThanOrSameAs(ServerVersion apiVersion) {
        return compareTo(apiVersion) >= 0;
    }

    public boolean isOlderThanOrSameAs(ServerVersion apiVersion) {
        return compareTo(apiVersion) <= 0;
    }

    @Override
    public String toString() {
        return getVersionString();
    }
}

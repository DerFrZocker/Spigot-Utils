package de.derfrzocker.spigot.utils.message;

import lombok.NonNull;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class MessageUtil {

    private final static int DEFAULT_LORE_LENGTH = 40;
    private final static int MINIMUM_LORE_LENGTH = 15;

    public static String replacePlaceHolder(final @NonNull Plugin plugin, @NonNull String string, final @NonNull MessageValue... messageValues) {

        string = replaceTranslation(plugin, string, messageValues);

        for (MessageValue value : messageValues)
            string = string.replace("%" + value.getKey() + "%", value.getValue());

        string = ChatColor.translateAlternateColorCodes('&', string);

        return string;
    }

    @SuppressWarnings("WeakerAccess")
    public static List<String> replaceList(final @NonNull Plugin plugin, final @NonNull List<String> strings, final @NonNull MessageValue... messageValues) {
        List<String> list = new LinkedList<>();

        strings.stream().flatMap(line -> {
            if (line.contains("\n") || line.contains("%%new-line%")) {
                return Stream.of(line.split("(\\n|%%new-line%)"));
            }
            return Stream.of(line);
        }).forEach(value -> list.add(replacePlaceHolder(plugin, value, messageValues)));

        return list;
    }

    public static ItemStack replaceItemStack(final @NonNull Plugin plugin, @NonNull ItemStack itemStack, final @NonNull MessageValue... messageValues) {
        if (!itemStack.hasItemMeta())
            return itemStack;

        itemStack.setItemMeta(replaceItemMeta(plugin, itemStack.getItemMeta(), messageValues));

        return itemStack;
    }

    @SuppressWarnings("WeakerAccess")
    public static ItemMeta replaceItemMeta(final @NonNull Plugin plugin, final @NonNull ItemMeta itemMeta, final @NonNull MessageValue... messageValues) {
        if (itemMeta.hasDisplayName())
            itemMeta.setDisplayName(replacePlaceHolder(plugin, itemMeta.getDisplayName(), messageValues));

        if (itemMeta.hasLore()) {
            final List<String> lore = new LinkedList<>();

            replaceList(plugin, itemMeta.getLore(), messageValues).forEach(string -> lore.addAll(splitString(string, itemMeta.hasDisplayName() ? itemMeta.getDisplayName().length() < MINIMUM_LORE_LENGTH ? DEFAULT_LORE_LENGTH : itemMeta.getDisplayName().length() : DEFAULT_LORE_LENGTH)));

            itemMeta.setLore(lore);
        }

        return itemMeta;
    }

    public static List<String> splitString(final String msg, final int lineSize) {
        final List<String> strings = new LinkedList<>();

        if (!msg.contains("%%split%")) {
            strings.add(msg);
            return strings;
        }

        final Pattern pattern = Pattern.compile("\\b.{1," + (lineSize - 1) + "}\\b\\W?");
        final Matcher matcher = pattern.matcher(msg.replace("%%split%", ""));

        while (matcher.find())
            strings.add(matcher.group());

        return strings;
    }

    // %%translation:[example.string]%
    public static String replaceTranslation(final @NonNull Plugin plugin, final @NonNull String string, final @NonNull MessageValue... messageValues) {
        if (!string.contains("%%translation:["))
            return string;

        final Pattern pattern = Pattern.compile("%%translation:(.*?)]%");
        Matcher matcher = pattern.matcher(string);

        final StringBuilder stringBuilder = new StringBuilder(string);

        while (matcher.find()) {
            String key = stringBuilder.substring(matcher.start() + 15, matcher.end() - 2);

            key = replacePlaceHolder(plugin, key, messageValues);

            stringBuilder.replace(matcher.start(), matcher.end(), new MessageKey(plugin, key).getRawMessage());

            matcher = pattern.matcher(stringBuilder.toString());
        }

        return stringBuilder.toString();
    }


}

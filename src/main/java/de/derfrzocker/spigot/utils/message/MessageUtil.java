package de.derfrzocker.spigot.utils.message;

import de.derfrzocker.spigot.utils.language.Language;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class MessageUtil {

    private static final Pattern TRANSLATION_PATTERN = Pattern.compile("%%translation:(.*?)]%");

    private MessageUtil() {
    }

    @NotNull
    public static String formatToString(@Nullable Language language, @NotNull String message, @NotNull MessageValue... messageValues) {
        return toString(format(language, List.of(message), messageValues));
    }

    @NotNull
    public static String formatToString(@Nullable Language language, @NotNull String message, @NotNull StringSeparator stringSeparator, @NotNull MessageValue... messageValues) {
        return toString(format(language, List.of(message), messageValues), stringSeparator);
    }

    @NotNull
    public static List<String> format(@Nullable Language language, @NotNull String message, @NotNull MessageValue... messageValues) {
        return format(language, List.of(message), messageValues);
    }

    @NotNull
    public static List<String> format(@Nullable Language language, @NotNull List<String> messages, @NotNull MessageValue... messageValues) {
        List<String> list = new LinkedList<>();

        messages
                .stream()
                .map(line -> {
                    if (language != null) {
                        return replaceTranslation(language, line, messageValues);
                    }
                    return line;
                })
                .map(line -> replacePlaceHolder(line, messageValues))
                .map(MessageUtil::color)
                .flatMap(line -> {
                    if (line.contains("\n") || line.contains("%%new-line%")) {
                        return Stream.of(line.split("(\\n|%%new-line%)"));
                    }
                    return Stream.of(line);
                })
                .forEach(list::add);

        return list;
    }

    @NotNull
    public static String color(@NotNull String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    @NotNull
    public static String replacePlaceHolder(@NotNull String message, @NotNull MessageValue... messageValues) {
        for (MessageValue value : messageValues) {
            message = message.replace("%" + value.getKey() + "%", value.getValue());
        }

        return message;
    }

    @NotNull
    public static String toString(@NotNull List<?> messages) {
        return toString(messages, StringSeparator.SPACE);
    }

    @NotNull
    public static String toString(@NotNull List<?> messages, @NotNull StringSeparator stringSeparator) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;

        for (Object message : messages) {
            if (first) {
                first = false;
            } else {
                builder.append(stringSeparator.getSeparator());
            }

            builder.append(message);
        }

        return builder.toString();
    }

    @NotNull
    public static ItemStack format(@Nullable Language language, @NotNull ItemStack itemStack, @NotNull MessageValue... messageValues) {
        itemStack = itemStack.clone();

        if (!itemStack.hasItemMeta()) {
            return itemStack;
        }

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta == null) {
            return itemStack;
        }

        itemStack.setItemMeta(format(language, itemMeta, messageValues));

        return itemStack;
    }

    public static ItemMeta format(@Nullable Language language, @NotNull ItemMeta itemMeta, @NotNull MessageValue... messageValues) {
        if (itemMeta.hasDisplayName()) {
            itemMeta.setDisplayName(formatToString(language, itemMeta.getDisplayName(), StringSeparator.SPACE, messageValues));
        }

        if (itemMeta.hasLore()) {
            List<String> lore = itemMeta.getLore();
            if (lore != null) {
                itemMeta.setLore(format(language, lore, messageValues));
            }
        }

        return itemMeta;
    }

    // %%translation:[example.string]%
    public static String replaceTranslation(@NotNull Language language, @NotNull String message, @NotNull MessageValue... messageValues) {
        if (!message.contains("%%translation:[")) {
            return message;
        }

        StringBuilder stringBuilder = new StringBuilder(message);
        Matcher matcher = TRANSLATION_PATTERN.matcher(message);

        while (matcher.find()) {
            String key = stringBuilder.substring(matcher.start() + 15, matcher.end() - 2);

            key = replaceTranslation(language, key, messageValues);
            key = replacePlaceHolder(key, messageValues);

            String toSet = "null";
            Object value = language.getSetting().get(key);

            if (value != null) {
                if (value instanceof List) {
                    toSet = toString((List<?>) value, StringSeparator.NEW_LINE);
                } else {
                    toSet = value.toString();
                }
            }

            stringBuilder.replace(matcher.start(), matcher.end(), toSet);

            matcher = TRANSLATION_PATTERN.matcher(stringBuilder.toString());
        }

        return stringBuilder.toString();
    }

    public enum StringSeparator {

        SPACE(" "),
        NEW_LINE("\n");

        @NotNull
        private final String separator;

        StringSeparator(@NotNull String separator) {
            this.separator = separator;
        }

        @NotNull
        public String getSeparator() {
            return separator;
        }
    }
}

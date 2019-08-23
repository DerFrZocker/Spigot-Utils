package de.derfrzocker.spigot.utils.message;

import com.google.common.collect.Lists;
import de.derfrzocker.spigot.utils.Config;
import de.derfrzocker.spigot.utils.Language;
import de.derfrzocker.spigot.utils.ReloadAble;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Messages implements ReloadAble {

    private final static Map<JavaPlugin, Map<Language, Messages>> MESSAGESS = new HashMap<>();

    @NonNull
    private YamlConfiguration yaml;
    @NonNull
    private final Language language;
    @NonNull
    private final JavaPlugin plugin;

    public static Messages getMessages(final @NonNull JavaPlugin javaPlugin) {
        return getMessages(javaPlugin, Language.getDefaultLanguage());
    }

    public static Messages getMessages(final @NonNull JavaPlugin javaPlugin, final @NonNull Language language) {
        final Map<Language, Messages> map = MESSAGESS.computeIfAbsent(javaPlugin, javaPlugin1 -> new HashMap<>());

        return map.computeIfAbsent(language, language1 -> new Messages(javaPlugin, language));
    }

    public static void unLoadMessages(final @NonNull JavaPlugin javaPlugin, final @NonNull Language language) {
        final Map<Language, Messages> map = MESSAGESS.get(javaPlugin);

        if (map == null)
            return;

        final Messages messages = map.get(language);

        if (messages == null)
            return;

        RELOAD_ABLES.remove(messages);
        MESSAGESS.remove(messages);
    }

    private Messages(final JavaPlugin plugin, final Language language) {
        this.language = language;
        this.plugin = plugin;
        reload();
        RELOAD_ABLES.add(this);
    }

    void sendMessage(final @NonNull MessageKey key, final @NonNull CommandSender target, final @NonNull MessageValue... messageValues) {
        final List<String> stringList = getRawStringList(key);

        stringList.forEach(value -> target.sendMessage(MessageUtil.replacePlaceHolder(plugin, value, messageValues)));
    }

    void broadcastMessage(final @NonNull MessageKey key, final @NonNull MessageValue... messageValues) {
        final List<String> stringList = getRawStringList(key);

        stringList.forEach(value -> Bukkit.broadcastMessage(MessageUtil.replacePlaceHolder(plugin, value, messageValues)));
    }

    void broadcastMessage(final @NonNull MessageKey key, final @NonNull String permission, final @NonNull MessageValue... messageValues) {
        final List<String> stringList = getRawStringList(key);

        stringList.forEach(value -> Bukkit.broadcast(MessageUtil.replacePlaceHolder(plugin, value, messageValues), permission));
    }

    String getRawMessages(final @NonNull MessageKey key) {
        final StringBuilder stringBuilder = new StringBuilder();

        final List<String> list = getRawStringList(key);

        for (int i = 0; i < list.size(); i++) {
            stringBuilder.append(list.get(i));
            if (i != (list.size() - 1))
                stringBuilder.append(System.lineSeparator());
        }

        return stringBuilder.toString();
    }

    List<String> getRawStringList(final @NonNull MessageKey key) {
        final List<String> stringList;

        if (yaml.isList(key.getKey()))
            stringList = yaml.getStringList(key.getKey());
        else if (!yaml.isString(key.getKey()))
            stringList = Lists.newArrayList("String: " + key.getKey() + " not found!");
        else
            stringList = Lists.newArrayList(yaml.getString(key.getKey()));

        return stringList;
    }

    String getMessage(final @NonNull MessageKey key, final @NonNull MessageValue... messageValues) {
        return MessageUtil.replacePlaceHolder(plugin, getRawMessages(key), messageValues);
    }

    List<String> getStringList(final @NonNull MessageKey key, final @NonNull MessageValue... messageValues) {
        final List<String> stringList = getRawStringList(key);
        final List<String> newList = new ArrayList<>();

        stringList.stream().map(string -> MessageUtil.replacePlaceHolder(plugin, string, messageValues)).forEach(newList::add);

        return newList;
    }

    @Override
    public void reload() {
        yaml = Config.getConfig(plugin, language.getFileLocation());
    }
}

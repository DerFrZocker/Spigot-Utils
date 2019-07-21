package de.derfrzocker.spigot.utils;

import com.google.common.collect.Lists;
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
    private final Language language;
    private final JavaPlugin plugin;

    public static Messages getMessages(@NonNull JavaPlugin javaPlugin) {
        return getMessages(javaPlugin, Language.getDefaultLanguage());
    }

    public static Messages getMessages(@NonNull JavaPlugin javaPlugin, @NonNull Language language) {
        Map<Language, Messages> map = MESSAGESS.computeIfAbsent(javaPlugin, javaPlugin1 -> new HashMap<>());

        return map.computeIfAbsent(Language.getDefaultLanguage(), language1 -> new Messages(javaPlugin, language));
    }

    public static void unLoadMessages(@NonNull JavaPlugin javaPlugin, @NonNull Language language) {
        Map<Language, Messages> map = MESSAGESS.get(javaPlugin);

        if (map == null)
            return;

        Messages messages = map.get(language);

        if (messages == null)
            return;

        RELOAD_ABLES.remove(messages);
        MESSAGESS.remove(messages);
    }

    public Messages(@NonNull JavaPlugin plugin, @NonNull Language language) {
        this.language = language;
        this.plugin = plugin;
        reload();
        RELOAD_ABLES.add(this);
        MESSAGESS.computeIfAbsent(plugin, javaPlugin -> new HashMap<>()).put(language, this);
    }

    void sendMessage(@NonNull MessageKey key, @NonNull CommandSender target, @NonNull MessageValue... messageValues) {
        List<String> stringList = getRawStringList(key);

        stringList.forEach(value -> target.sendMessage(MessageUtil.replacePlaceHolder(plugin, value, messageValues)));
    }

    void broadcastMessage(@NonNull MessageKey key, @NonNull MessageValue... messageValues) {
        List<String> stringList = getRawStringList(key);

        stringList.forEach(value -> Bukkit.broadcastMessage(MessageUtil.replacePlaceHolder(plugin, value, messageValues)));
    }

    void broadcastMessage(@NonNull MessageKey key, @NonNull String permission, @NonNull MessageValue... messageValues) {
        List<String> stringList = getRawStringList(key);

        stringList.forEach(value -> Bukkit.broadcast(MessageUtil.replacePlaceHolder(plugin, value, messageValues), permission));
    }

    String getRawMessages(@NonNull MessageKey key) {
        StringBuilder stringBuilder = new StringBuilder();

        List<String> list = getRawStringList(key);

        for (int i = 0; i < list.size(); i++) {
            stringBuilder.append(list.get(i));
            if (i != (list.size() - 1))
                stringBuilder.append(System.lineSeparator());
        }

        return stringBuilder.toString();
    }

    List<String> getRawStringList(@NonNull MessageKey key) {
        List<String> stringList;

        if (yaml.isList(key.getKey()))
            stringList = yaml.getStringList(key.getKey());
        else if (!yaml.isString(key.getKey()))
            stringList = Lists.newArrayList("String: " + key.getKey() + " not found!");
        else
            stringList = Lists.newArrayList(yaml.getString(key.getKey()));

        return stringList;
    }

    String getMessage(@NonNull MessageKey key, @NonNull MessageValue... messageValues) {
        return MessageUtil.replacePlaceHolder(plugin, getRawMessages(key), messageValues);
    }

    List<String> getStringList(@NonNull MessageKey key, @NonNull MessageValue... messageValues) {
        List<String> stringList = getRawStringList(key);
        List<String> newList = new ArrayList<>();

        stringList.stream().map(string -> MessageUtil.replacePlaceHolder(plugin, string, messageValues)).forEach(newList::add);

        return newList;
    }

    @Override
    public void reload() {
        yaml = Config.getConfig(plugin, language.getFileLocation());
    }
}

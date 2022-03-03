package de.derfrzocker.spigot.utils.message;

import com.google.common.collect.Lists;
import de.derfrzocker.spigot.utils.Config;
import de.derfrzocker.spigot.utils.Language;
import de.derfrzocker.spigot.utils.ReloadAble;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Messages implements ReloadAble {

    private final static Map<Plugin, Map<Language, Messages>> MESSAGESS = new HashMap<>();

    private YamlConfiguration yaml;
    private final Language language;
    private final Plugin plugin;

    public static Messages getMessages(final Plugin plugin) {
        return getMessages(plugin, Language.getDefaultLanguage());
    }

    public static Messages getMessages(final Plugin plugin, final Language language) {
        final Map<Language, Messages> map = MESSAGESS.computeIfAbsent(plugin, plugin1 -> new HashMap<>());

        return map.computeIfAbsent(language, language1 -> new Messages(plugin, language));
    }

    public static void unLoadMessages(final Plugin plugin, final Language language) {
        final Map<Language, Messages> map = MESSAGESS.get(plugin);

        if (map == null)
            return;

        final Messages messages = map.get(language);

        if (messages == null)
            return;

        MESSAGESS.remove(messages);
    }

    private Messages(final Plugin plugin, final Language language) {
        this.language = language;
        this.plugin = plugin;
        reload();
    }

    void sendMessage(final MessageKey key, final CommandSender target, final MessageValue... messageValues) {
        final List<String> stringList = getRawStringList(key);

        stringList.forEach(value -> target.sendMessage(MessageUtil.replacePlaceHolder(plugin, value, messageValues)));
    }

    void broadcastMessage(final MessageKey key, final MessageValue... messageValues) {
        final List<String> stringList = getRawStringList(key);

        stringList.forEach(value -> Bukkit.broadcastMessage(MessageUtil.replacePlaceHolder(plugin, value, messageValues)));
    }

    void broadcastMessage(final MessageKey key, final String permission, final MessageValue... messageValues) {
        final List<String> stringList = getRawStringList(key);

        stringList.forEach(value -> Bukkit.broadcast(MessageUtil.replacePlaceHolder(plugin, value, messageValues), permission));
    }

    String getRawMessages(final MessageKey key) {
        final StringBuilder stringBuilder = new StringBuilder();

        final List<String> list = getRawStringList(key);

        for (int i = 0; i < list.size(); i++) {
            stringBuilder.append(list.get(i));
            if (i != (list.size() - 1))
                stringBuilder.append(System.lineSeparator());
        }

        return stringBuilder.toString();
    }

    List<String> getRawStringList(final MessageKey key) {
        final List<String> stringList;

        if (yaml.isList(key.getKey()))
            stringList = yaml.getStringList(key.getKey());
        else if (!yaml.isString(key.getKey()))
            stringList = Lists.newArrayList("String: " + key.getKey() + " not found!");
        else
            stringList = Lists.newArrayList(yaml.getString(key.getKey()));

        return stringList;
    }

    String getMessage(final MessageKey key, final MessageValue... messageValues) {
        return MessageUtil.replacePlaceHolder(plugin, getRawMessages(key), messageValues);
    }

    List<String> getStringList(final MessageKey key, final MessageValue... messageValues) {
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

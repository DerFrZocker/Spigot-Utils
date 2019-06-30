package de.derfrzocker.spigot.utils;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class Messages implements ReloadAble {

    private final static Map<Language, Messages> MESSAGESS = new HashMap<>();

    @NonNull
    private YamlConfiguration yaml;
    private final Language language;
    private final JavaPlugin plugin;

    public static Messages getMessages(){
        return MESSAGESS.get(Language.getDefaultLanguage());
    }

    public static Messages getMessages(Language language){
        return MESSAGESS.get(language);
    }

    public Messages(JavaPlugin plugin, Language language) {
        this.language = language;
        this.plugin = plugin;
        reload();
        RELOAD_ABLES.add(this);
        MESSAGESS.put(language, this);
    }

    void sendMessage(@NonNull MessageKey key, @NonNull CommandSender target, @NonNull MessageValue... messageValues) {
        List<String> stringList = getStringList(key);

        stringList.forEach(value -> target.sendMessage(MessageUtil.replacePlaceHolder(value, messageValues)));
    }

    void broadcastMessage(@NonNull MessageKey key, @NonNull MessageValue... messageValues) {
        List<String> stringList = getStringList(key);

        stringList.forEach(value -> Bukkit.broadcastMessage(MessageUtil.replacePlaceHolder(value, messageValues)));
    }

    void broadcastMessage(@NonNull MessageKey key, @NonNull String permission, @NonNull MessageValue... messageValues) {
        List<String> stringList = getStringList(key);

        stringList.forEach(value -> Bukkit.broadcast(MessageUtil.replacePlaceHolder(value, messageValues), permission));
    }

    String getMessages(@NonNull MessageKey key) {
        StringBuilder stringBuilder = new StringBuilder();

        List<String> list = getStringList(key);

        for (int i = 0; i < list.size(); i++) {
            stringBuilder.append(list.get(i));
            if (i != (list.size() - 1))
                stringBuilder.append(System.lineSeparator());
        }

        return stringBuilder.toString();
    }

    private List<String> getStringList(MessageKey key) {
        List<String> stringList;

        if (yaml.isList(key.getKey()))
            stringList = yaml.getStringList(key.getKey());
        else if (!yaml.isString(key.getKey()))
            stringList = Lists.newArrayList("String: " + key.getKey() + " not found!");
        else
            stringList = Lists.newArrayList(yaml.getString(key.getKey()));

        return stringList;
    }

    @Override
    public void reload() {
        yaml = Config.getConfig(plugin, language.getFileLocation());
    }
}

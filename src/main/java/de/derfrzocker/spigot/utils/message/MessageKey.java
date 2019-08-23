package de.derfrzocker.spigot.utils.message;

import de.derfrzocker.spigot.utils.Language;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@Data
public class MessageKey {

    @NonNull
    private final JavaPlugin plugin;

    @NonNull
    private final String key;

    public void sendMessage(final @NonNull CommandSender target, final @NonNull MessageValue... messageValues) {
        Messages.getMessages(plugin).sendMessage(this, target, messageValues);
    }

    public void broadcastMessage(final @NonNull MessageValue... messageValues) {
        Messages.getMessages(plugin).broadcastMessage(this, messageValues);
    }

    public void broadcastMessage(final @NonNull String permission, final @NonNull MessageValue... messageValues) {
        Messages.getMessages(plugin).broadcastMessage(this, permission, messageValues);
    }

    public String getRawMessage() {
        return Messages.getMessages(plugin).getRawMessages(this);
    }

    public List<String> getRawStringList() {
        return Messages.getMessages(plugin).getRawStringList(this);
    }

    public String getMessage(final @NonNull MessageValue... messageValues) {
        return Messages.getMessages(plugin).getMessage(this, messageValues);
    }

    public List<String> getStringList(final @NonNull MessageValue... messageValues) {
        return Messages.getMessages(plugin).getStringList(this, messageValues);
    }

    public void sendMessage(final @NonNull Language language, final @NonNull CommandSender target, final @NonNull MessageValue... messageValues) {
        Messages.getMessages(plugin, language).sendMessage(this, target, messageValues);
    }

    public void broadcastMessage(final @NonNull Language language, final @NonNull MessageValue... messageValues) {
        Messages.getMessages(plugin, language).broadcastMessage(this, messageValues);
    }

    public void broadcastMessage(final @NonNull Language language, final @NonNull String permission, final @NonNull MessageValue... messageValues) {
        Messages.getMessages(plugin, language).broadcastMessage(this, permission, messageValues);
    }

    public String getRawMessage(final @NonNull Language language) {
        return Messages.getMessages(plugin, language).getRawMessages(this);
    }

    public List<String> getRawStringList(final @NonNull Language language) {
        return Messages.getMessages(plugin, language).getRawStringList(this);
    }

    public String getMessage(final @NonNull Language language, final @NonNull MessageValue... messageValues) {
        return Messages.getMessages(plugin, language).getMessage(this, messageValues);
    }

    public List<String> getStringList(final @NonNull Language language, final @NonNull MessageValue... messageValues) {
        return Messages.getMessages(plugin, language).getStringList(this, messageValues);
    }

}

package de.derfrzocker.spigot.utils.message;

import de.derfrzocker.spigot.utils.Language;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class MessageKey {

    private final Plugin plugin;

    private final String key;

    public MessageKey(Plugin plugin, String key) {
        this.plugin = plugin;
        this.key = key;
    }

    public void sendMessage(final CommandSender target, final MessageValue... messageValues) {
        Messages.getMessages(plugin).sendMessage(this, target, messageValues);
    }

    public void broadcastMessage(final MessageValue... messageValues) {
        Messages.getMessages(plugin).broadcastMessage(this, messageValues);
    }

    public void broadcastMessage(final String permission, final MessageValue... messageValues) {
        Messages.getMessages(plugin).broadcastMessage(this, permission, messageValues);
    }

    public String getRawMessage() {
        return Messages.getMessages(plugin).getRawMessages(this);
    }

    public List<String> getRawStringList() {
        return Messages.getMessages(plugin).getRawStringList(this);
    }

    public String getMessage(final MessageValue... messageValues) {
        return Messages.getMessages(plugin).getMessage(this, messageValues);
    }

    public List<String> getStringList(final MessageValue... messageValues) {
        return Messages.getMessages(plugin).getStringList(this, messageValues);
    }

    public void sendMessage(final Language language, final CommandSender target, final MessageValue... messageValues) {
        Messages.getMessages(plugin, language).sendMessage(this, target, messageValues);
    }

    public void broadcastMessage(final Language language, final MessageValue... messageValues) {
        Messages.getMessages(plugin, language).broadcastMessage(this, messageValues);
    }

    public void broadcastMessage(final Language language, final String permission, final MessageValue... messageValues) {
        Messages.getMessages(plugin, language).broadcastMessage(this, permission, messageValues);
    }

    public String getRawMessage(final Language language) {
        return Messages.getMessages(plugin, language).getRawMessages(this);
    }

    public List<String> getRawStringList(final Language language) {
        return Messages.getMessages(plugin, language).getRawStringList(this);
    }

    public String getMessage(final Language language, final MessageValue... messageValues) {
        return Messages.getMessages(plugin, language).getMessage(this, messageValues);
    }

    public List<String> getStringList(final Language language, final MessageValue... messageValues) {
        return Messages.getMessages(plugin, language).getStringList(this, messageValues);
    }

    public String getKey() {
        return key;
    }
}

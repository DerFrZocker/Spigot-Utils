package de.derfrzocker.spigot.utils.language;

import de.derfrzocker.spigot.utils.ReloadAble;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface LanguageManager extends ReloadAble {

    /**
     * Checks if the given player has a language set or not.
     *
     * @param player the player which should be checked
     * @return true if the player has a language set otherwise false
     */
    boolean hasLanguageSet(@NotNull Player player);

    /**
     * Permanently saves the given language to the given player.
     *
     * @param player   the player to which should get the language
     * @param language the language which should be set
     */
    void setLanguage(@NotNull Player player, @NotNull Language language);

    /**
     * If the player has a language set it will get removed.
     * If the player does currently not have a language set nothing will happen.
     *
     * @param player the player which should get the language removed
     */
    void removeLanguage(@NotNull Player player);

    /**
     * Returns the language of the player or the default one if not set.
     *
     * @param player the player from which the language should be obtained.
     * @return the language the player has set or the default language
     */
    @NotNull
    Language getLanguage(@NotNull Player player);

    /**
     * Returns a list of all available languages this LanguageManager knows.
     *
     * @return a list which contains all available languages
     */
    @NotNull
    List<Language> getAvailableLanguages();
}

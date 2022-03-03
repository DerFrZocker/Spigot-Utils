package de.derfrzocker.spigot.utils.language;

import de.derfrzocker.spigot.utils.setting.Setting;
import org.apache.commons.lang.Validate;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class Language {

    @NotNull
    private final String name;
    @NotNull
    private final Set<String> authors;
    @NotNull
    private final ItemStack logo;
    @NotNull
    private final Setting setting;

    public Language(@NotNull String name, @NotNull Set<String> authors, @NotNull ItemStack logo, @NotNull Setting setting) {
        Validate.notNull(name, "Name cannot be null.");
        Validate.notNull(authors, "Authors cannot be null.");
        Validate.notNull(logo, "Logo cannot be null.");
        Validate.notNull(setting, "Setting cannot be null.");

        this.name = name;
        this.authors = authors;
        this.logo = logo;
        this.setting = setting;
    }

    /**
     * @return the name of the language
     */
    public @NotNull String getName() {
        return name;
    }

    /**
     * @return the authors of the language
     */
    public @NotNull Set<String> getAuthors() {
        return authors;
    }

    /**
     * @return the logo of the language
     */
    public @NotNull ItemStack getLogo() {
        return logo;
    }

    /**
     * @return the setting of the language
     */
    public @NotNull Setting getSetting() {
        return setting;
    }
}

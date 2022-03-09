package de.derfrzocker.spigot.utils.command;

/**
 * The HelpConfig contains the message formats needed by the Help command to send the CommandSender the help messages
 */
public interface HelpConfig {

    /**
     * The separator gets displayed between to commands, to separate them <br>
     * No MessageValues are passed
     *
     * @return the separator format
     */
    /*@NotNull
    MessageKey getSeparatorMessageFormat();*/

    /**
     * The header gets displayed on begin from the help command <br>
     * No MessageValues are passed
     *
     * @return the header format
     */
    /*@NotNull
    MessageKey getHeaderMessageFormat();*/

    /**
     * The footer gets displayed on the end of the help command <br>
     * No MessageValues are passed
     *
     * @return the footer format
     */
    /*@NotNull
    MessageKey getFooterMessageFormat();*/

    /**
     * The permissions format gets displayed when a command is specified by the help command <br>
     * The command permission is passed as "permission" and the command name as "command" <br>
     * The message gets only displayed if a permission is specified
     *
     * @return the permission format
     */
    /*@NotNull
    MessageKey getPermissionMessageFormat();*/

    /**
     * The usage format gets displayed when a command is specified by the help command <br>
     * The command usage is passed as "usage" and the command name as "command" <br>
     * The message gets only displayed if a usage is specified
     *
     * @return the usage format
     */
    /*@NotNull
    MessageKey getUsageMessageFormat();*/

    /**
     * The description format gets displayed when a command is specified by the help command <br>
     * The command description is passed as "description" and the command name as "command" <br>
     * The message gets only displayed if a description is specified
     *
     * @return the description format
     */
    /*@NotNull
    MessageKey getDescriptionMessageFormat();*/

    /**
     * The short help format gets displayed when no command is specified on the help command <br>
     * The command description is passed as "description" and the command name as "command" <br>
     * The message gets only displayed if a description and permission is specified
     *
     * @return the short help format
     */
    /*@NotNull
    MessageKey getShortHelpMessageFormat();*/

}

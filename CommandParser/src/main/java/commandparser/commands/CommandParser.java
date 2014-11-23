package commandparser.commands;

/*
* Copyright (C) 2014 www.StarNub.org - Underbalanced
*
* This file is part of org.starnub a Java Wrapper for Starbound.
*
* This above mentioned StarNub software is free software:
* you can redistribute it and/or modify it under the terms
* of the GNU General Public License as published by the Free
* Software Foundation, either version  3 of the License, or
* any later version. This above mentioned CodeHome software
* is distributed in the hope that it will be useful, but
* WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
* the GNU General Public License for more details. You should
* have received a copy of the GNU General Public License in
* this StarNub Software.  If not, see <http://www.gnu.org/licenses/>.
*/

import org.apache.commons.lang3.StringUtils;
import org.codehome.utilities.files.YamlLoader;
import server.StarNub;
import server.connectedentities.Sender;
import server.connectedentities.player.session.Player;
import server.eventsrouter.events.NonPlayerEvent;
import server.eventsrouter.events.PlayerEvent;
import server.plugins.CommandPackage;
import server.plugins.PluginPackage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * StarNub's Command Sender
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public class CommandParser {


    private static final CommandParser instance = new CommandParser();

    private CommandParser(){}

    /**
     * A simple ConcurrentHashMap that contains a list of shortcut command to plugin name for mapping..
     * <p>
     * Example: In file: starnub: ['ban', 'kick']
     *          Would map, /ban and /kick to starnub.
     */

    private static ConcurrentHashMap<String, String> shortcutCommandToPluginCommand;

    /**
     * A simple ConcurrentHashMap that contains a list of split values for unique command spliting
     * Example: {command}, #, (w, 2) would split /sn w {player} {message}
     */

    private static ConcurrentHashMap<String, Integer> commandSplitRegister;

    {
        setStarboundCommands();
    }

    public static CommandParser getInstance() {
        return instance;
    }

    public static ConcurrentHashMap<String, String> getShortcutCommandToPluginCommand() {
        return shortcutCommandToPluginCommand;
    }

    public static void setShortcutCommandToPluginCommand(ConcurrentHashMap<String, String> shortcutCommandToPluginCommand) {
        CommandParser.shortcutCommandToPluginCommand = shortcutCommandToPluginCommand;
    }

    public static ConcurrentHashMap<String, Integer> getCommandSplitRegister() {
        return commandSplitRegister;
    }

    public static void setCommandSplitRegister(ConcurrentHashMap<String, Integer> commandSplitRegister) {
        CommandParser.commandSplitRegister = commandSplitRegister;
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will set the list for Starbound commands to be added to.
     */
    public void setStarboundCommands() {
        if (CommandParser.shortcutCommandToPluginCommand != null) {
            throw new UnsupportedOperationException("Cannot redefine starboundCommands");
        }
        shortcutCommandToPluginCommand = new ConcurrentHashMap<String, String>();
        commandSplitRegister = new ConcurrentHashMap<String, Integer>();
        loadShortcutCommandsMap();
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: For internal use with StarNub.
     * <p>
     * Uses: This method will load a shortcut command to plugin name mapper. @see shortcutCommandToPluginCommand
     */
    @SuppressWarnings("unchecked")
    private void loadShortcutCommandsMap() {
        Map<String, Object> commandShortcutConfiguration = new YamlLoader().filePathYamlLoader("StarNub/shortcut_command_map.yml");
        if (commandShortcutConfiguration == null) {
            StarNub.getLogger().cDebPrint("StarNub", "No shortcut command map found, using defaults.");
            commandShortcutConfiguration = new YamlLoader().resourceYamlLoader("servers/starnub.fileextracts/default_shortcut_command_map.yml");
        }
        for (String pluginNameString : commandShortcutConfiguration.keySet()) {
            ArrayList<String> commandsToMap = null;
            try {
                commandsToMap = (ArrayList<String>) commandShortcutConfiguration.get(pluginNameString);
            } catch (ClassCastException e) {
                shortcutCommandToPluginCommand.putIfAbsent(((String) commandShortcutConfiguration.get(pluginNameString)).toLowerCase(), pluginNameString.toLowerCase());
            }
            if (commandsToMap == null) {
                return;
            }
            for (String command : commandsToMap) {
                shortcutCommandToPluginCommand.putIfAbsent(command.toLowerCase(), pluginNameString.toLowerCase());
            }
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will show what Shortcut commands are loaded and what they map to
     *
     * @param command String representing the command
     * @return String the plugin name
     */
    public String getShortcutCommandReference(String command) {
        if (!shortcutCommandToPluginCommand.containsKey(command)) {
            return null;
        }
        return shortcutCommandToPluginCommand.get(command);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will return if a shortcut is available
     *
     * @param command String representing the command
     * @return boolean the plugin name
     */
    public boolean hasShortcut(String command) {
        return shortcutCommandToPluginCommand.containsKey(command);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will register a custom splitter which will split a command specifically. You must
     * register the the custom splitter using your plugins name we will build the key using the plugin name, alias
     * and command. The keys made would look like this. For plugin starbound commands for whisper "starbound w" and "sb w".
     * The command parser will check for this key and if found will split the command using the int you provided. The splits
     * are by spaces. A integer of 1 will split the command so it only has one argument. /sb w underbalanced hey you how are you =
     * [/sb, w, underbalanced hey how are you] this would not work in case of whispering because we need 2 arguments, the player and
     * message, so we will use an integer of 2 which results in [/sb, w, underbalanced, hey how are you]
     *
     * @param pluginName String representing the plugin name
     * @param command String representing the command
     * @param splitCount int representing the number of splits
     */
    public void registerCustomSplitCount(String pluginName, String command, int splitCount){
        PluginPackage pluginPackage = StarNub.getPluginManager().getPluginPackageNameAlias(pluginName);
        commandSplitRegister.putIfAbsent(pluginPackage.getCOMMANDS_NAME().toLowerCase()+" "+command.toLowerCase(), splitCount+2);
        commandSplitRegister.putIfAbsent(pluginPackage.getCOMMANDS_ALIAS().toLowerCase()+" "+command.toLowerCase(), splitCount+2);
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will register a custom splitter which will split a command specifically. You must
     * register the the custom splitter using your plugins name we will build the key using the plugin name, alias
     * and command. The keys made would look like this. For plugin starbound commands for whisper "starbound w" and "sb w".
     * The command parser will check for this key and if found will split the command using the int you provided. The splits
     * are by spaces. A integer of 1 will split the command so it only has one argument. /sb w underbalanced hey you how are you =
     * [/sb, w, underbalanced hey how are you] this would not work in case of whispering because we need 2 arguments, the player and
     * message, so we will use an integer of 2 which results in [/sb, w, underbalanced, hey how are you]
     *
     * @param pluginName String representing the plugin name
     * @param commands String[] representing the command
     * @param splitCount int representing the number of splits
     */
    public void registerCustomSplitCount(String pluginName, String[] commands, int splitCount){
        PluginPackage pluginPackage = StarNub.getPluginManager().getPluginPackageNameAlias(pluginName);
        for (String command : commands) {
            commandSplitRegister.putIfAbsent(pluginPackage.getCOMMANDS_NAME().toLowerCase() + " " + command.toLowerCase(), splitCount + 2);
            commandSplitRegister.putIfAbsent(pluginPackage.getCOMMANDS_ALIAS().toLowerCase() + " " + command.toLowerCase(), splitCount + 2);
        }
    }

    /**
     * This represents a higher level method for StarNubs API.
     * <p>
     * Recommended: For Plugin Developers & Anyone else.
     * <p>
     * Uses: This method will remove a custom command split
     *
     * @param pluginName String representing the plugin name
     * @param command String representing the command
     */
    public void unregisterCustomSplitCount(String pluginName, String command){
        PluginPackage pluginPackage = StarNub.getPluginManager().getPluginPackageNameAlias(pluginName);
        commandSplitRegister.remove(pluginPackage.getCOMMANDS_NAME().toLowerCase() + " " + command.toLowerCase());
        commandSplitRegister.remove(pluginPackage.getCOMMANDS_ALIAS().toLowerCase() + " " + command.toLowerCase());
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: This is mostly for internal use. You may use it at your own risk.
     * <p>
     * Uses: This method will parse a command and send it to the correct plugin that
     * holds the command. If players do not use "-" for arguments then the command
     * will only be broken into two arguments total. It will be broken by the first
     * after the first argument.
     *
     * @param sender Object representing the sender, can be a player or some other connected
     *               object
     * @param commandString String the entire command being sent
     *                      Example: /starbound ban blah or /starnubplugin ban blah or /ban blah
     *
     */
    public void commandSend(Object sender, String commandString) {
        commandString = shortcutLookup(commandString);
        //TODO all eventsrouter for command sender
        /* Make sure we know who sent the command */
        Player playerSession = null;
        if (sender instanceof Player) {
            playerSession = (Player) sender;
        }

        List<String> argsList = new ArrayList<String>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(commandString);
        while (m.find()) {
            argsList.add(m.group(1));
        }

        /* Check is the command was long enough */
        if (argsList.size() <= 1) {
            if (playerSession != null) {
                PlayerEvent.eventSend_Player_Command_Failed_Argument_Count(playerSession, commandString);
            } else {
                NonPlayerEvent.eventSend_NonPlayer_Command_Failed_Argument_Count((Sender) sender, commandString);
            }
            StarNub.getMessageSender().commandResults(sender, null, "Your \"" + commandString + "\" command was to short. For help on commands \"/help commands\". For commands you can use \"/help mycommands\".", false);
            return;
        }

        String pluginNameAlias = argsList.get(0).replace("/", "");
        String command = argsList.get(1);
        String keyToFind = pluginNameAlias + " " + command;
        boolean commandCustomSplit = false;
        if (commandSplitRegister.containsKey(keyToFind)) {
            argsList.clear();
            Collections.addAll(argsList, StringUtils.stripAll(commandString.split(" ", commandSplitRegister.get(keyToFind))));
            commandCustomSplit = true;
        }

        /* Remove the plugin name or alias and command from the argsList list*/
        argsList.remove(0);
        argsList.remove(0);

        /* Make sure that this plugin exist */
        if (!StarNub.getPluginManager().hasPlugin(pluginNameAlias)) {
            if (playerSession != null) {
                PlayerEvent.eventSend_Player_Command_Failed_No_Plugin(playerSession, commandString);
            } else {
                NonPlayerEvent.eventSend_NonPlayer_Command_Failed_No_Plugin((Sender) sender, commandString);
            }
            StarNub.getMessageSender().commandResults(sender,  null, "Plugin name or alias \"" + pluginNameAlias + "\" does not exist or is not loaded.", false);
            return;
        }

        /* Make sure the plugin has the command */
        String hasCommand = StarNub.getPluginManager().hasCommand(pluginNameAlias, command);
        if (hasCommand.equalsIgnoreCase("nocommand")) {
            if (playerSession != null) {
                PlayerEvent.eventSend_Player_Command_Failed_No_Command(playerSession, commandString);
            } else {
                NonPlayerEvent.eventSend_NonPlayer_Command_Failed_No_Command((Sender) sender, commandString);
            }
            StarNub.getMessageSender().commandResults(sender,  null, "Command \"" + command + "\" is not a valid " + pluginNameAlias + " command.", false);
            return;
        }

        /* Get the associated command package */
        CommandPackage commandPackage = StarNub.getPluginManager().getPluginCommandPackage(pluginNameAlias, command);

        /* Make sure the sender is aloud to use this command package */
        if (playerSession != null && commandPackage.getCAN_USE() == 2) {
            PlayerEvent.eventSend_Player_Command_Failed_Player_Cannot_Use(playerSession, commandString);
            StarNub.getMessageSender().commandResults(sender,  null, "Player's cannot use " + pluginNameAlias + " command \"" + command + "\".", false);
            return;
        } else if (playerSession == null && commandPackage.getCAN_USE() == 1) {
            NonPlayerEvent.eventSend_NonPlayer_Command_Failed_NonPlayer_Cannot_Use((Sender) sender, commandString);
            StarNub.getMessageSender().commandResults(sender,  null, "Remote players cannot use " + pluginNameAlias + " command \"" + command + "\".", false);
            return;
        }

        /* Permission check */
        String[] args = null;
        if (playerSession != null) {
            boolean fullPermission = true;
            String main_arg = null;
            try {
                main_arg = argsList.get(0);
                args = argsList.toArray(new String[argsList.size()]);
            } catch (IndexOutOfBoundsException e) {
                fullPermission = false;
            }
            if (commandCustomSplit) {
                fullPermission = false;
            }
            if (!StarNub.getServer().getConnections().hasPermission(playerSession, commandPackage.getPERMISSIONS().split("\\.")[0], command, main_arg, fullPermission, true)) {
                PlayerEvent.eventSend_Player_Command_Failed_Permissions(playerSession, commandString);
                String sendString = "";
                if (fullPermission){
                    sendString = command + " " + main_arg;
                } else {
                    sendString = command;
                }
                StarNub.getMessageSender().commandResults(playerSession, null, "You do not have permission to use " + pluginNameAlias + " command \"" + sendString + "\".", false);
                return;
            }
        }

        if (playerSession != null) {
            PlayerEvent.eventSend_Player_Command_Delivered_To_Plugin(playerSession, commandString);
        } else {
            NonPlayerEvent.eventSend_NonPlayer_Command_Delivered_To_Plugin((Sender) sender, commandString);
        }

        if ((boolean) ((Map) StarNub.getConfiguration().getConfiguration().get("starbounddata.packets.starbounddata.packets.server starbounddata.packets.chat")).get("player_command_delivery_reply")) {
            StarNub.getMessageSender().commandResults(sender, keyToFind, "Command delivered to Plugin: \"" + pluginNameAlias + "\". Command: \"" + commandString + "\".", true);
        }
        /* Finally send the command */
        commandPackage.getCOMMAND().onCommand(sender, command, args);
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: This is for internal use.
     * <p>
     * Uses: This method will see if the command given was a shorthand version
     *
     * @param commandString String representing the shorthand command
     * @return String new command string if a look up provided results
     */
    private String shortcutLookup(String commandString) {
        String commandParse;
        try {
            commandParse = commandString.substring(commandString.indexOf("/") + 1, commandString.indexOf(" "));
        } catch (StringIndexOutOfBoundsException e) {
            commandParse = commandString.substring(commandString.indexOf("/") + 1);
        }
        if (shortcutCommandToPluginCommand.containsKey(commandParse) || shortcutCommandToPluginCommand.containsKey(commandParse.toLowerCase())) {
            commandString = commandString.replace("/", "/" + shortcutCommandToPluginCommand.get(commandParse) + " ");
        }
        return commandString;
    }

    /**
     * This represents a lower level method for StarNubs API.
     * <p>
     * Recommended: This is for internal use.
     * <p>
     * Uses: This method will rebuild a starbound command string to be used in forwarding to
     * the Starbound starbounddata.packets.starbounddata.packets.server
     *
     * @param command String representing the command
     * @param args String[] representing the arguments
     * @return String the rebuild starbound command
     */
    public String starboundCommandRebuilder(String command, String[] args) {
        String rebuiltCommand =  "/"+command;
        for (String arg : args) {
            rebuiltCommand = rebuiltCommand+" "+arg;
        }
        return rebuiltCommand;
    }

    public String hasShortcutStringBuilder(String command){
        return hasShortcut(command) ? "/" + command : "/sn " + command;
    }
}


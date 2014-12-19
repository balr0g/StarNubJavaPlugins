package commandparser.commands;

import commandparser.CommandHandler;
import commandparser.CommandParser;
import commandparser.data.Shortcut;
import starbounddata.chat.ChatReceiveChannel;
import starnubserver.StarNub;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.plugins.Command;
import utilities.exceptions.CollectionDoesNotExistException;
import utilities.strings.StringUtilities;

import java.io.IOException;
import java.util.*;

public class CommandParserCommands extends Command {

//    private CommandHandler COMMAND_HANDLER;

    public CommandParserCommands(String PLUGIN_NAME, HashSet<String> COMMANDS, HashSet<String> MAIN_ARGS, HashMap<String, Integer> CUSTOM_SPLIT, String COMMAND_CLASS, String COMMAND_NAME, int CAN_USE, String DESCRIPTION) {
        super(PLUGIN_NAME, COMMANDS, MAIN_ARGS, CUSTOM_SPLIT, COMMAND_CLASS, COMMAND_NAME, CAN_USE, DESCRIPTION);
    }

    @Override
    public void onCommand(PlayerSession playerSession, String command, String[] args) {
        command = command.toLowerCase();
        switch (command) {
            case "shortcuts": {
                shortcuts(playerSession, args);
                break;
            }
            default: {
                sendMessage(playerSession, "You did not supply a correct command.");
            }
        }
    }

    private void shortcuts(PlayerSession playerSession, String[] args) {
        final CommandHandler COMMAND_HANDLER = ((CommandParser) getPLUGIN()).getCommandHandler();
        int argsLength = args.length;
        boolean isNoArgs = argsLength == 0;
        if (isNoArgs) {
            sendMessage(playerSession, "You did not supply a correct main argument for command \"shortcuts\", available commands: get, add, remove, swap and reload.");
            return;
        }
        String mainArg = args[0].toLowerCase();
        switch (mainArg) {
            case "reload": {
                sendMessage(playerSession, "Attempting to reload shortcuts...");
                COMMAND_HANDLER.shortcutCacheReload();
                Map<String, Object> data = COMMAND_HANDLER.getSHORTCUTS().getDATA();
                int plugins = data.size();
                int shortcuts = 0;
                for (Object object : data.values()) {
                    List<String> shortcutsList = (List<String>) object;
                    shortcuts += shortcutsList.size();
                }
                sendMessage(playerSession, "Shortcuts reloaded. Plugins with shortcuts: " + plugins +
                        ". Total registered shortcuts: " + shortcuts + ".");
                break;
            }
            case "add": {
                if (argsLength < 3) {
                    sendMessage(playerSession, "Incorrect command usage. Requires more arguments. \"/shortcuts add <PluginName> <Command>\"");
                    return;
                }
                addShortcut(COMMAND_HANDLER, playerSession, args[1], args[2]);
                break;
            }
            case "remove": {
                if (argsLength < 3) {
                    sendMessage(playerSession, "Incorrect command usage. Requires more arguments. \"/shortcuts remove <PluginName> <Command>\"");
                    return;
                }
                removeShortcut(COMMAND_HANDLER, playerSession, args[1], args[2]);
                break;
            }
            case "swap": {
                if (argsLength < 4) {
                    sendMessage(playerSession, "Incorrect command usage. Requires more arguments. \"/shortcuts swap <PluginFromName> <Command> <PluginToName>\"");
                    return;
                }
                removeShortcut(COMMAND_HANDLER, playerSession, args[1], args[2]);
                addShortcut(COMMAND_HANDLER, playerSession, args[3], args[2]);
                break;
            }
            case "get": {
                if (argsLength < 3) {
                    sendMessage(playerSession, "Possible shortcut get commands. /shortcuts get \"command <Command>, plugin <pluginName>, all plugins\"");
                    return;
                }
                String request = args[1].toLowerCase();
                switch (request) {
                    case "command": {
                        String command = args[2];
                        String plugin = COMMAND_HANDLER.getSHORTCUT_CACHE().get(command);
                        if (plugin == null) {
                            sendMessage(playerSession, "Could not find the command \"" + command + "\" as a registered shortcut.");
                        } else {
                            sendMessage(playerSession, "The command \"" + command + "\" is registered to plugin \"" + plugin + "\". When you use \"/" + command + "\" it will be delivered to \"" + plugin + "\".");
                        }
                        break;
                    }
                    case "plugin": {
                        String plugin = args[2];
                        List<String> commands = (List<String>) COMMAND_HANDLER.getSHORTCUTS().getValue(plugin.toLowerCase());
                        if (commands == null) {
                            sendMessage(playerSession, "Could not find the plugin \"" + plugin + "\" as having any registered shortcuts.");
                        } else {
                            sendMessage(playerSession, "The command(s) \"" + commands + "\" are registered to plugin \"" + plugin + "\". When you use these commands they are delivered to \"" + plugin + "\".");
                        }
                        break;
                    }
                    case "all": {
                        Set<String> keys = COMMAND_HANDLER.getSHORTCUTS().getAllKeys();
                        String phrase = "The following plugins have shortcut commands and can be queried with \"/shortcuts get plugin <pluginName>\". Plugin: ";
                        if (keys.size() == 0) {
                            sendMessage(playerSession, phrase + "No plugins with registered shortcuts detected.");
                            return;
                        }
                        for (String key : keys) {
                            phrase = phrase + key + ", ";
                        }
                        phrase = StringUtilities.trimCommaForPeriod(phrase);
                        sendMessage(playerSession, phrase);
                        break;
                    }
                    default: {
                        sendMessage(playerSession, "You did not supply a correct main argument for command \"shortcuts\".");
                    }
                }
                break;
            }
            default: {
                sendMessage(playerSession, "You did not supply a correct main argument for command \"shortcuts\".");
            }
        }

    }

    private void addShortcut(CommandHandler commandHandler, PlayerSession playerSession, String pluginString, String commandString) {
        try {
            handleShortcutResults(playerSession, pluginString, commandString, "added", commandHandler.shortcutAdd(pluginString, commandString));
        } catch (IOException | CollectionDoesNotExistException e) {
            StarNub.getLogger().cErrPrint(getPLUGIN_NAME(), e.getMessage());
            sendMessage(playerSession, "There was a critical error with adding a shortcut for Plugin: " + pluginString + ". Command: " + commandString + ".");
        }
    }

    private void removeShortcut(CommandHandler commandHandler, PlayerSession playerSession, String pluginString, String commandString) {
        try {
            handleShortcutResults(playerSession, pluginString, commandString, "removed", commandHandler.removeShortcut(pluginString, commandString));
        } catch (IOException e) {
            StarNub.getLogger().cErrPrint(getPLUGIN_NAME(), e.getMessage());
            sendMessage(playerSession, "There was a critical error with removing a shortcut for Plugin: " + pluginString + ". Command: " + commandString + ".");
        }
    }

    private void handleShortcutResults(PlayerSession playerSession, String pluginString, String commandString, String type, Shortcut shortcutResults) {
        switch (shortcutResults) {
            case SUCCESS: {
                sendMessage(playerSession, "Your command shortcut \"" + commandString + "\" was successfully " + type + " for plugin \"" + pluginString + "\".");
                break;
            }
            case NO_PLUGIN: {
                sendMessage(playerSession, "Your command shortcut \"" + commandString + "\" was not successfully " + type + " for plugin \"" + pluginString + "\". No plugin with this name has exist or has shortcuts.");
                break;
            }
            case NO_COMMAND: {
                sendMessage(playerSession, "Your command shortcut \"" + commandString + "\" was not successfully " + type + " for plugin \"" + pluginString + "\". No command was found under this plugins shortcuts.");
                break;
            }
            case NO_COMMANDS: {
                sendMessage(playerSession, "Your command shortcut \"" + commandString + "\"was not successfully " + type + "for plugin \"" + pluginString + "\". No command(s) were found.");
                break;
            }
            case COMMAND_ASSIGNED: {
                sendMessage(playerSession, "Your command shortcut \"" + commandString + "\"was not successfully " + type + "for plugin \"" + pluginString + "\". Shortcut is already assigned.");
                break;
            }
            case ERROR: {
                sendMessage(playerSession, "Your command shortcut \"" + commandString + "\"was not successfully " + type + "for plugin \"" + pluginString + "\". Unknown Error.");
                break;
            }

        }
    }

    private void sendMessage(PlayerSession playerSession, String message) {
        playerSession.sendChatMessage(getPLUGIN_NAME(), ChatReceiveChannel.UNIVERSE, message);
    }
}

package commandparser;

import commandparser.data.Shortcut;
import org.apache.commons.lang3.StringUtils;
import starbounddata.types.chat.Mode;
import starnubdata.generic.CanUse;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.events.events.StarNubEventTwo;
import starnubserver.events.starnub.StarNubEventHandler;
import starnubserver.events.starnub.StarNubEventSubscription;
import starnubserver.plugins.Command;
import starnubserver.plugins.JavaPlugin;
import starnubserver.plugins.Plugin;
import starnubserver.plugins.PluginManager;
import starnubserver.plugins.generic.CommandInfo;
import starnubserver.plugins.resources.PluginConfiguration;
import starnubserver.plugins.resources.PluginYAMLWrapper;
import starnubserver.plugins.resources.YAMLFiles;
import utilities.connectivity.connection.Connection;
import utilities.connectivity.connection.ProxyConnection;
import utilities.events.Priority;
import utilities.events.types.ObjectEvent;
import utilities.exceptions.CollectionDoesNotExistException;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandHandler extends StarNubEventHandler {

    private final PluginConfiguration CONFIG;
    private final StarNubEventSubscription YAML_RELOAD;
    private final StarNubEventSubscription YAML_DUMP;
    private final StarNubEventSubscription PLUGIN_LOAD;
    private final StarNubEventSubscription STARNUB_START_COMPLETE;
    private final StarNubEventSubscription COMMAND_SHORTCUT_OVERIDE;
    private final PluginYAMLWrapper SHORTCUTS;
    private final ConcurrentHashMap<String, String> SHORTCUT_CACHE;

    public CommandHandler(PluginConfiguration configuration, YAMLFiles files) throws IOException, CollectionDoesNotExistException {
        CONFIG = configuration;
        SHORTCUTS = files.getPluginYamlWrapper("shortcuts.yml");
        SHORTCUT_CACHE = new ConcurrentHashMap<>();
        YAML_RELOAD = new StarNubEventSubscription("CommandParser", Priority.MEDIUM, "YAMLWrapper_Reloaded_CommandParser_shortcuts.yml", new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent starNubEvent) {
                shortcutCacheReload();
            }
        });
        YAML_DUMP = new StarNubEventSubscription("CommandParser", Priority.MEDIUM, "YAMLWrapper_Dumped_CommandParser_shortcuts.yml", new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent starNubEvent) {
                shortcutCacheReload();
            }
        });
        PLUGIN_LOAD = new StarNubEventSubscription("CommandParser", Priority.MEDIUM, "StarNub_Plugin_Loaded", new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent starNubEvent) {
                JavaPlugin javaPlugin = (JavaPlugin) starNubEvent.getEVENT_DATA();
                try {
                    autoShortcutAdd(javaPlugin);
                } catch (IOException | CollectionDoesNotExistException e) {
                    e.printStackTrace();
                }
            }
        });
        STARNUB_START_COMPLETE = new StarNubEventSubscription("CommandParser", Priority.CRITICAL, "StarNub_Startup_Complete", new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent starNubEvent) {
                try {
                    autoPurgeShortcuts();
                    autoShortcut();
                } catch (IOException | CollectionDoesNotExistException e) {
                    e.printStackTrace();
                }
            }
        });
        shortcutCacheReload();

        COMMAND_SHORTCUT_OVERIDE = new StarNubEventSubscription("CommandParser", Priority.MEDIUM, "Command_Shortcut_Override", new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent objectEvent) {
                String string = (String) objectEvent.getEVENT_DATA();
                String[] strings = string.split("\\.");
                if (strings.length >= 2) {
                    try {
                        addShortcut(strings[1], strings[0], true);
                        shortcutCacheReload();
                    } catch (IOException | CollectionDoesNotExistException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void shortcutCacheReload() {
        SHORTCUT_CACHE.clear();
        Map<String, Object> shortcutMap = SHORTCUTS.getDATA();
        for (Map.Entry<String, Object> entry : shortcutMap.entrySet()) {
            String pluginShortcut = entry.getKey();
            List<String> commandShortcuts = (List<String>) entry.getValue();
            for (String command : commandShortcuts) {
                SHORTCUT_CACHE.put(command, pluginShortcut);
            }
        }
    }

    private void autoShortcutAdd(Plugin plugin) throws IOException, CollectionDoesNotExistException {
        boolean hasCommands = plugin.hasCommands();
        if (hasCommands) {
            String commandsName = plugin.getCOMMAND_INFO().getCOMMANDS_NAME();
            if (!SHORTCUTS.hasKey(commandsName)) {
                SHORTCUTS.createList(commandsName);
            }
            HashSet<Command> commands = plugin.getCOMMAND_INFO().getCOMMANDS();
            for (Command command : commands) {
                HashSet<String> stringsCommands = command.getCOMMANDS();
                for (String stringCommand : stringsCommands) {
                    addShortcut(stringCommand, commandsName, false);
                }
            }
        }
        shortcutCacheReload();
    }

    private void autoPurgeShortcuts() throws IOException {
        Map<String, Object> data = SHORTCUTS.getDATA();
        if (data != null) {
            for (String key : data.keySet()) {
                Plugin plugin = PluginManager.getInstance().resolveLoadedPluginByCommand(key);
                if (plugin == null) {
                    SHORTCUTS.removeValue(key);
                }
            }
        }
    }

    private void autoShortcut() throws IOException, CollectionDoesNotExistException {
        HashSet<Plugin> loadedPlugins = PluginManager.getInstance().getAllLoadedPlugins();
        for (Plugin plugin : loadedPlugins) {
            autoShortcutAdd(plugin);
        }
        Map<String, Object> data = SHORTCUTS.getDATA();
        /* Clean shortcuts if empty */
        for (Map.Entry<String, Object> dataEntry : data.entrySet()) {
            String dataStringKey = dataEntry.getKey();
            List<String> dataListValue = (List<String>) dataEntry.getValue();
            if (dataListValue.size() == 0) {
                SHORTCUTS.removeValue(dataStringKey);
            }
        }
    }

    private void addShortcut(String command, String commandName, boolean override) throws IOException, CollectionDoesNotExistException {
        Map<String, Object> data = SHORTCUTS.getDATA();
        boolean canAdd = true;
        for (Object pluginsShortcuts : data.values()) {
            List<String> pluginList = (List<String>) pluginsShortcuts;
            if (pluginList.contains(command)) {
                if (override) {
                    pluginList.remove(command);
                    break;
                } else {
                    canAdd = false;
                }
            }
        }
        if (canAdd) {
            SHORTCUTS.addToCollection(command, false, false, commandName);
        }
    }

    public ConcurrentHashMap<String, String> getSHORTCUT_CACHE() {
        return SHORTCUT_CACHE;
    }

    public StarNubEventSubscription getYAML_RELOAD() {
        return YAML_RELOAD;
    }

    public StarNubEventSubscription getYAML_DUMP() {
        return YAML_DUMP;
    }

    public StarNubEventSubscription getPLUGIN_LOAD() {
        return PLUGIN_LOAD;
    }

    public PluginYAMLWrapper getSHORTCUTS() {
        return SHORTCUTS;
    }

    public StarNubEventSubscription getSTARNUB_START_COMPLETE() {
        return STARNUB_START_COMPLETE;
    }

    public Shortcut shortcutAdd(String pluginString, String commandString) throws IOException, CollectionDoesNotExistException {
        HashSet<String> alreadyShortcut = getAllShortcuts();
        commandString = commandString.toLowerCase();
        if (alreadyShortcut.contains(commandString)) {
            return Shortcut.COMMAND_ASSIGNED;
        }
        Plugin plugin = PluginManager.getInstance().resolveLoadedPlugin(pluginString, false);
        if (plugin == null) {
            return Shortcut.NO_PLUGIN;
        }
        CommandInfo commandInfo = plugin.getCOMMAND_INFO();
        if (commandInfo == null) {
            return Shortcut.NO_COMMANDS;
        }
        HashSet<Command> commands = commandInfo.getCOMMANDS();
        for (Command command : commands) {
            HashSet<String> strings = command.getCOMMANDS();
            if (strings.contains(commandString)) {
                String pluginName = plugin.getNAME().toLowerCase();
                if (!SHORTCUTS.hasKey(pluginName)) {
                    SHORTCUTS.createList(pluginName);
                }
                SHORTCUTS.addToCollection(commandString, false, false, pluginName);
                shortcutCacheReload();
                return Shortcut.SUCCESS;
            }
        }
        return Shortcut.NO_COMMAND;
    }

    public HashSet<String> getAllShortcuts() {
        Map<String, Object> data = SHORTCUTS.getDATA();
        HashSet<String> alreadyShortcuts = new HashSet<>();
        for (Object values : data.values()) {
            List<String> shortcuts = (List<String>) values;
            alreadyShortcuts.addAll(shortcuts);
        }
        return alreadyShortcuts;
    }

    public Shortcut removeShortcut(String pluginName, String commandString) throws IOException {
        if (!SHORTCUTS.hasKey(pluginName)) {
            return Shortcut.NO_PLUGIN;
        }
        if (!SHORTCUTS.collectionContains(commandString, pluginName)) {
            return Shortcut.NO_COMMAND;
        }
        boolean removed = SHORTCUTS.removeFromCollection(commandString, pluginName);
        if (removed) {
            List<String> list = (List<String>) SHORTCUTS.getValue(pluginName);
            if (list.size() == 0) {
                SHORTCUTS.removeValue(pluginName);
            }
            return Shortcut.SUCCESS;
        }
        return Shortcut.ERROR;
    }
    
    private String shortcutLookup(String commandString) {
        String commandParse;
        if (commandString.contains(" ")) {
            commandParse = commandString.substring(commandString.indexOf("/") + 1, commandString.indexOf(" "));
        } else {
            commandParse = commandString.substring(commandString.indexOf("/") + 1);
        }
        for (Map.Entry<String, String> shortcutEntry : SHORTCUT_CACHE.entrySet()) {
            String command = shortcutEntry.getKey();
            String plugin = shortcutEntry.getValue();
            if (command.equalsIgnoreCase(commandParse)) {
                return commandString.replace("/", "/" + plugin + " ");
            }
        }
        return commandString;
    }

    @Override
    public void onEvent(ObjectEvent starNubEvent) {
        StarNubEventTwo starNubEventTwo = (StarNubEventTwo) starNubEvent;
        PlayerSession playerSession = (PlayerSession) starNubEventTwo.getEVENT_DATA();
        String commandString = (String) starNubEventTwo.getEVENT_DATA_2();

        commandString = shortcutLookup(commandString);
        List<String> argsList = new ArrayList<String>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(commandString);
        while (m.find()) {
            argsList.add(m.group(1));
        }

        /* Check is the command was long enough */
        if (argsList.size() < 1) {
            new StarNubEventTwo("Player_Command_Failed_Argument_Count", playerSession, commandString);
            sendChatMessage(playerSession, "Your \"" + commandString + "\" command was to short. Please check the plugin this command is for.");
            return;
        }

        String commandNameOrAlias = argsList.get(0).replace("/", "");

        Plugin plugin = PluginManager.getInstance().resolveLoadedPluginByCommand(commandNameOrAlias);
        if (plugin == null) {
            new StarNubEventTwo("Player_Command_Failed_No_Plugin", playerSession, commandString);
            sendChatMessage(playerSession, "Plugin name or alias \"" + commandNameOrAlias + "\" does not exist or is not loaded.");
            return;
        }

        String pluginName = plugin.getNAME();
        String exactCommand = argsList.get(1);
        Command command = plugin.getCOMMAND_INFO().getCommandByName(exactCommand);
        if (command == null) {
            new StarNubEventTwo("Player_Command_Failed_No_Command", playerSession, commandString);
            sendChatMessage(playerSession, "Command \"" + exactCommand + "\" is not a valid " + pluginName + " command.");
            return;
        }

        /* Splits are number of splits on spaces , I.E /r hi how are you (split on 2) = [hi, how are you] */
        HashMap<String, Integer> customSplit = command.getCUSTOM_SPLIT();
        boolean fullPermission = true;
        if (customSplit.containsKey(exactCommand)) {
            argsList.clear();
            Collections.addAll(argsList, StringUtils.stripAll(commandString.split(" ", customSplit.get(exactCommand))));
            fullPermission = false;
        }

        /* Remove the plugin name/alias and command from the args list*/
        argsList.remove(0);
        argsList.remove(0);

        CanUse canUse = command.getCAN_USE();
        Connection connection = playerSession.getCONNECTION();
        boolean isInGame = true;
        if (!(connection instanceof ProxyConnection)) {
            isInGame = false;
        }

        switch (canUse) {
            case PLAYER: {
                if (!isInGame) {
                    new StarNubEventTwo("Player_Command_Failed_Remote_Player_Cannot_Use", playerSession, commandString);
                    sendChatMessage(playerSession, "Remote Player's cannot use " + pluginName + " command \"" + command + "\".");
                    return;
                }
                break;
            }
            case REMOTE_PLAYER: {
                if (isInGame) {
                    new StarNubEventTwo("Player_Command_Failed_Player_Cannot_Use", playerSession, commandString);
                    sendChatMessage(playerSession, "Player's cannot use " + pluginName + " command \"" + command + "\".");
                    return;
                }
                break;
            }
        }

        String main_arg = null;
        String args[] = null;
        boolean hasPermission = false;
        if (argsList.size() > 0) {
            main_arg = argsList.get(0);
            args = argsList.toArray(new String[argsList.size()]);
            hasPermission = playerSession.hasPermission(commandNameOrAlias, exactCommand, main_arg, true);

        } else {
            args = new String[0];
            fullPermission = false;
            hasPermission = playerSession.hasPermission(commandNameOrAlias, exactCommand, true);
        }

        if (!hasPermission) {
            String failedCommand = exactCommand;
            String permissionString = commandNameOrAlias + "." + exactCommand;
            if (fullPermission) {
                failedCommand = failedCommand + " " + main_arg;
                permissionString = permissionString + "." + main_arg;
            }
            new StarNubEventTwo("Player_Command_Failed_Permissions", playerSession, commandString);
            sendChatMessage(playerSession, "You do not have permission to use " + pluginName + "'s command \"" + failedCommand + "\". Permission required: \"" + permissionString + "\".");
            return;
        }

        new StarNubEventTwo("Player_Command_Delivered_To_Plugin", playerSession, commandString);
        command.onCommand(playerSession, exactCommand, args);
    }

    private void sendChatMessage(PlayerSession playerSession, String message) {
        playerSession.sendChatMessage("CommandParser", Mode.BROADCAST, message);
    }
}

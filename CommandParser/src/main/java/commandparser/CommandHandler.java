package commandparser;

import org.apache.commons.lang3.StringUtils;
import starbounddata.chat.ChatReceiveChannel;
import starnubdata.generic.CanUse;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.events.events.StarNubEvent;
import starnubserver.events.events.StarNubEventTwo;
import starnubserver.events.starnub.StarNubEventHandler;
import starnubserver.events.starnub.StarNubEventSubscription;
import starnubserver.plugins.Command;
import starnubserver.plugins.JavaPlugin;
import starnubserver.plugins.Plugin;
import starnubserver.plugins.PluginManager;
import starnubserver.plugins.generic.CommandInfo;
import starnubserver.plugins.resources.PluginYAMLWrapper;
import starnubserver.plugins.resources.YAMLFiles;
import starnubserver.resources.files.PluginConfiguration;
import utilities.connectivity.connection.Connection;
import utilities.connectivity.connection.ProxyConnection;
import utilities.events.Priority;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandHandler extends StarNubEventHandler {

    private final PluginConfiguration CONFIG;
    private final PluginManager PLUGIN_MANAGER = PluginManager.getInstance();
    private final StarNubEventSubscription YAML_RELOAD;
    private final StarNubEventSubscription YAML_DUMP;
    private final StarNubEventSubscription PLUGIN_LOAD;
    private final StarNubEventSubscription PLUGIN_UNLOAD;
    private final PluginYAMLWrapper SHORTCUTS;
    private final ConcurrentHashMap<String, String> SHORTCUT_CACHE = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> ALIAS_CACHE = new ConcurrentHashMap<>();

    public CommandHandler(PluginConfiguration configuration, YAMLFiles files) {
        CONFIG = configuration;
        SHORTCUTS = files.getPluginYamlWrapper("shortcuts.yml");
        YAML_RELOAD = new StarNubEventSubscription("CommandParser", Priority.MEDIUM, "YAMLWrapper_Reloaded_CommandParser_shortcuts.yml", new StarNubEventHandler() {
            @Override
            public void onEvent(StarNubEvent starNubEvent) {
                shortcutReload();
            }
        });
        YAML_DUMP = new StarNubEventSubscription("CommandParser", Priority.MEDIUM, "YAMLWrapper_Dumped_CommandParser_shortcuts.yml", new StarNubEventHandler() {
            @Override
            public void onEvent(StarNubEvent starNubEvent) {
                shortcutReload();
            }
        });
        PLUGIN_LOAD = new StarNubEventSubscription("CommandParser", Priority.MEDIUM, "StarNub_Plugin_Loaded", new StarNubEventHandler() {
            @Override
            public void onEvent(StarNubEvent starNubEvent) {
                JavaPlugin javaPlugin = (JavaPlugin) starNubEvent.getEVENT_DATA();
                CommandInfo commandInfo = javaPlugin.getCOMMAND_INFO();
                String command = commandInfo.getCOMMANDS_NAME();
                String alias = commandInfo.getCOMMANDS_ALIAS();
                ALIAS_CACHE.put(alias, command);
            }
        });
        PLUGIN_UNLOAD = new StarNubEventSubscription("CommandParser", Priority.MEDIUM, "StarNub_Plugin_Unloaded", new StarNubEventHandler() {
            @Override
            public void onEvent(StarNubEvent starNubEvent) {
                JavaPlugin javaPlugin = (JavaPlugin) starNubEvent.getEVENT_DATA();
                CommandInfo commandInfo = javaPlugin.getCOMMAND_INFO();
                String alias = commandInfo.getCOMMANDS_ALIAS();
                ALIAS_CACHE.remove(alias);
            }
        });
        aliasLoad();
        shortcutReload();
    }

    private void shortcutReload() {
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

    private void aliasLoad() {
        ALIAS_CACHE.clear();
        HashSet<Plugin> loadedPlugins = PluginManager.getInstance().getAllLoadedPlugins();
        for (Plugin plugin : loadedPlugins) {
            CommandInfo commandInfo = plugin.getCOMMAND_INFO();
            String command = commandInfo.getCOMMANDS_NAME();
            String alias = commandInfo.getCOMMANDS_ALIAS();
            ALIAS_CACHE.put(alias, command);
        }
    }

    public StarNubEventSubscription getYAML_RELOAD() {
        return YAML_RELOAD;
    }

    public StarNubEventSubscription getYAML_DUMP() {
        return YAML_DUMP;
    }

    public StarNubEventSubscription getPLUGIN_UNLOAD() {
        return PLUGIN_UNLOAD;
    }

    public StarNubEventSubscription getPLUGIN_LOAD() {
        return PLUGIN_LOAD;
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
    public void onEvent(StarNubEvent starNubEvent) {
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
        if (argsList.size() <= 1) {
            new StarNubEventTwo("Player_Command_Failed_Argument_Count", playerSession, commandString);
            playerSession.sendChatMessage("CommandParser", ChatReceiveChannel.UNIVERSE, "Your \"" + commandString + "\" command was to short. Please check the plugin this command is for.");
            return;
        }

        String commandNameOrAlias = argsList.get(0).replace("/", "");

        Plugin plugin = PLUGIN_MANAGER.resolveLoadedPluginByCommand(commandNameOrAlias);
        if (plugin == null) {
            new StarNubEventTwo("Player_Command_Failed_No_Plugin", playerSession, commandString);
            playerSession.sendChatMessage("CommandParser", ChatReceiveChannel.UNIVERSE, "Plugin name or alias \"" + commandNameOrAlias + "\" does not exist or is not loaded.");
            return;
        }

        String pluginName = plugin.getNAME();
        String exactCommand = argsList.get(1);
        Command command = plugin.getCOMMAND_INFO().getCommandByName(exactCommand);
        if (command == null) {
            new StarNubEventTwo("Player_Command_Failed_No_Command", playerSession, commandString);
            playerSession.sendChatMessage("CommandParser", ChatReceiveChannel.UNIVERSE, "Command \"" + exactCommand + "\" is not a valid " + pluginName + " command.");
            return;
        }

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
                    playerSession.sendChatMessage("CommandParser", ChatReceiveChannel.UNIVERSE, "Remote Player's cannot use " + pluginName + " command \"" + command + "\".");
                    return;
                }
                break;
            }
            case REMOTE_PLAYER: {
                if (isInGame) {
                    new StarNubEventTwo("Player_Command_Failed_Player_Cannot_Use", playerSession, commandString);
                    playerSession.sendChatMessage("CommandParser", ChatReceiveChannel.UNIVERSE, "Player's cannot use " + pluginName + " command \"" + command + "\".");
                    return;
                }
                break;
            }
        }

        String main_arg = null;
        String args[] = null;
        if (argsList.size() > 0) {
            main_arg = argsList.remove(0);
        } else {
            fullPermission = false;
        }
        if (argsList.size() > 0) {
            args = argsList.toArray(new String[argsList.size()]);
        } else {
            fullPermission = false;
        }
        boolean hasPermission = playerSession.hasPermission(commandNameOrAlias, exactCommand, main_arg, true);
        if (!hasPermission) {
            String failedCommand = exactCommand;
            if (fullPermission) {
                failedCommand = failedCommand + " " + main_arg;
            }
            new StarNubEventTwo("Player_Command_Failed_Permissions", playerSession, commandString);
            playerSession.sendChatMessage("CommandParser", ChatReceiveChannel.UNIVERSE, "You do not have permission to use " + pluginName + " command \"" + failedCommand + "\".");
            return;
        }

        new StarNubEventTwo("Player_Command_Delivered_To_Plugin", playerSession, commandString);
        command.onCommand(playerSession, exactCommand, args);
    }
}

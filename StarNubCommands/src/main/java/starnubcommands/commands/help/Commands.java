package starnubcommands.commands.help;

import org.starnub.StarNub;
import org.starnub.connectedentities.player.session.Player;
import org.starnub.plugins.Command;
import org.starnub.plugins.CommandPackage;
import org.starnub.plugins.PluginPackage;

public class Commands extends Command {

    @Override
    public void onCommand(Object sender, String command, String[] args) {

        String cs = StarNub.getCommandSender().hasShortcut(command) ? "/commands " : "/sn commands ";
        Player playerSession = StarNub.getServer().getConnections().getOnlinePlayerByAnyIdentifier(sender);

        if (args == null || args.length == 0) {
//            msgSend.playerMessage(
//                    "StarNubCommands", sender,
//                    "Here are the commands you can get more information on: " + cs + "about, " + cs + "example. " +
//                            myCommandsBase(playerSession, command));
            return;
        }
        if (args.length >= 1) {
            switch (args[0]) {
                /* This will return the base commands */
                case "about": msgSender(sender, about(playerSession, command, args)); break;
                case "example": msgSender(sender, example(playerSession, command, args)); break;
//                case "shortcut": msgSender(sender, shortcut(playerSession, command, args[1]));
                default: msgSender(sender, commandsPrint(playerSession, command, args)); break;
            }
        }
    }

    private void msgSender(Object sender, String message){
        StarNub.getMessageSender().playerMessage("StarNubCommands", sender, message);
    }

    private String myCommandsBase(Player playerSession, String command) {
        String commandShort = StarNub.getCommandSender().hasShortcut(command) ? "/commands " : "/sn commands ";
        String results = "";
        for (PluginPackage pluginPackage : StarNub.getPluginManager().getLoadedPlugins().values()){
            if (!pluginPackage.getCOMMAND_PACKAGES().isEmpty()
                    && StarNub.getServer().getConnections().hasBasePermission(playerSession, pluginPackage.getCOMMANDS_NAME())) {
                    results = results + commandShort + "{" + pluginPackage.getCOMMANDS_NAME() + " or " + pluginPackage.getCOMMANDS_ALIAS()+"}, ";
            }
        }
        if (results.length() == 0) {
            results = "It appears you have no permissions to use any of the server's commands or the server does not have any commands loaded.";
        }
        return stringTrim(results);
    }

    private String about(Player playerSession, String command, String[] args){
        return "";


        //"/commands {name or alias}, Example (/commands starbound or /commands sn). "
    }

    private String example(Player playerSession, String command, String[] args){
        return "";
    }

//    private String shortcut(Player playerSession, String command, String arg){
//        if (!StarNub.getServer().getConnections().hasPermissionPartial(playerSession, command, arg)) {
//            return "You do not have permission to use this command. Who cares is it has a shortcut?";
//        }
//        return "This command has a shortcut" + StarNub.getCommandSender().hasShortcut(command);
//    }
//
    private String commandsPrint(Player playerSession, String command, String[] args){
        return "";
    }

    private void myCommandDetails(Player playerSession, String command, String[] args){
        String commandShort = StarNub.getCommandSender().hasShortcut("my") ? "/my " : "/sn my ";
        if (args.length < 2) {
            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", playerSession, "You must type a command to get details, try\"" + commandShort + "cdetails {command/command alias}\"\".");
            return;
        }
        PluginPackage plugin = StarNub.getPluginManager().getPluginPackageNameAlias(args[1]);
        if (plugin == null) {
            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", playerSession, "Are you sure you typed the correct command name or alias? We could not find a plugin associated with: "+ args[1]);
            return;
        }
        String results = "Here are the specific \"" +plugin.getCOMMANDS_NAME() + "/" + plugin.getCOMMANDS_ALIAS() + "\" commands that you can use. If the command has a server shortcut it will be denoted with \"*\" and can be used as such /{command}. Commands: ";
        String commands = "";
        for (CommandPackage commandPackage : plugin.getCOMMAND_PACKAGES().values()) {
            for (String commandString : commandPackage.getCOMMANDS()) {
                if (StarNub.getServer().getConnections().hasPermission(playerSession, (plugin.getCOMMANDS_NAME() + "." + commandString), true)) {
                    String shortcut = StarNub.getCommandSender().hasShortcut(command) ? "*" : "";
                    commands = commands + commandString + shortcut + ", ";
                } else {
                    commands = commands + "";
                }
            }
        }
        if (commands.length() >= 0) {
            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", playerSession, stringTrim(results + commands));
        } else {
            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", playerSession, "This plugin does not have commands that you can use.");
        }
    }


    private String stringTrim(String stringToTrim){
        try {
            return stringToTrim.substring(0, stringToTrim.lastIndexOf(",")) + ". ";
        } catch (StringIndexOutOfBoundsException e) {
            return stringToTrim;
        }
    }
}

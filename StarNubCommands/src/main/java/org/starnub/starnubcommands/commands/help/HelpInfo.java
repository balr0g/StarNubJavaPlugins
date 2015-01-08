package org.starnub.starnubcommands.commands.help;

import org.starnub.StarNub;
import org.starnub.connectedentities.player.session.Player;
import org.starnub.plugins.Command;

public class HelpInfo extends Command {

    public HelpInfo(){

    }

    @SuppressWarnings("unchecked")
    @Override
    public void onCommand(Object sender, String command, String[] args) {

        Player player = StarNub.getServer().getConnections().getOnlinePlayerByAnyIdentifier(sender);

        if (command.equalsIgnoreCase("help")) {
            StarNub.getMessageSender().playerMessage("StarNub", player, "This command is current in the works. It will" +
                    " be available in 1-2 weeks.");
            return;
        }

//        if (args == null) {
//            if (plugin.helpInfoFile.containsKey(command)) {
//                availableOptions(sender, command);
//            } else {
//                doesNotContainCommand(sender);
//            }
//        }
//        if (args.length == 1) {
//            if (args[0].equals("reload")) {
//                this.plugin.setCommandHelpInfo();
//            } else {
//                if (plugin.helpInfoFile.containsKey(command)) {
//                    Map<String, String> map = (Map) plugin.helpInfoFile.get(command);
//                    if (map.containsKey(args[0])) {
//                        String toSend = "Information about "+args[0]+": "+map.get(args[0]);
//                        StarNub.getMessageSender().playerMessage(plugin.pluginName, sender, toSend);
//                    } else {
//                        availableOptions(sender, command);
//                    }
//                } else {
//                    doesNotContainCommand(sender);
//                }
//            }
//        }
//    }
//
//    private void doesNotContainCommand(Object sender){
//        String commandString = "";
//        for (String helpArgs : plugin.helpInfoFile.keySet()) {
//            commandString = commandString + "/" + helpArgs + ", ";
//        }
//        try {
//            commandString = commandString.substring(0, commandString.lastIndexOf(",")) + ".";
//        } catch (StringIndexOutOfBoundsException e) {
//                        /* Do nothing to trim */
//        }
//        StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, "We could not find a command matching your" +
//                "input. The following are available using: " + commandString );
//    }
//
//    @SuppressWarnings("unchecked")
//    private void availableOptions(Object sender, String command){
//        String commandString = "";
//        Map<String, String> menu = (Map) plugin.helpInfoFile.get(command);
//        for (String helpArgs : menu.keySet()) {
//            commandString = commandString + helpArgs + ", ";
//        }
//        try {
//            commandString = commandString.substring(0, commandString.lastIndexOf(",")) + ".";
//        } catch (StringIndexOutOfBoundsException e) {
//                        /* Do nothing to trim */
//        }
//        StarNub.getMessageSender().playerMessage(plugin.pluginName, sender,
//                "The following command options are available to use (/"+command+" -{option}, like " +
//                        "/"+command+" -commands):  "+ commandString);
    }

    private  void onHelp(Object sender, String[] args){




    }

    private  void onInfo(Object sender, String[] args){
        if (args.length == 0) {

        }

    }

    private  void onMy(Object sender, String[] args){
        if (args.length == 0) {

        }

    }

    private  void onTheir(Object sender, String[] args){
        if (args.length == 0) {

        }

    }

    private void retreiveData(){

    }

}



/* Commmands:
 *
 * Generic Infor

 *
 * /info (List generic info)
 * /info -server
 * /info -servertool
 *
 *
 * /help (generic help)
 * /help -commands
 * /help -commands -detail_sections
 * /help -mycommands
 * /help -{pluginname}
 * /help -{pluginname} -detail_sections
 *
 *
 *
 * Keep in mind if they are not logged in to only show what not logged in players can use.
 *
 */
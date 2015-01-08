package org.starnub.starnubcommands.commands.admin;

import org.starnub.StarNub;
import org.starnub.connectedentities.player.session.Player;
import org.starnub.plugins.Command;
import org.starnub.server.datatypes.chat.ChatSendChannel;

public class WordFilter extends Command{

    @Override
    public void onCommand(Object sender, String command, String[] args) {
        if (command.equalsIgnoreCase("nick")){
            nickCommands(args);
        }
    }

    public void nickCommands(String[] args){
        if (args.length == 0){

        } else if (args[0].equalsIgnoreCase("change")){
            if (args.length == 3){
                Player playerSession = StarNub.getServer().getConnections().getOnlinePlayerByAnyIdentifier(args[1]);
                if (playerSession != null){
                    StarNub.getMessageSender().serverChatMessageToServerForPlayer("StarNub", playerSession, ChatSendChannel.UNIVERSE, "/nick " + args[2]);
                } else {
                    //player not online or not found
                }
            } else {
                //Return Not enough args or to many args
            }
        }
    }
}

package org.starnub.starnubcommands.commands.admin;

import org.starnub.StarNub;
import org.starnub.connectedentities.player.session.Player;
import org.starnub.plugins.Command;

public class NickName extends Command {

    @Override
    public void onCommand(Object sender, String command, String[] args) {
        if (command.equalsIgnoreCase("nickname")){
            nickCommands(args);
        }
    }

    public void nickCommands(String[] args){
        if (args.length == 0){

        } else if (args[0].equalsIgnoreCase("change")){
            if (args.length == 4){
                Player playerSession = StarNub.getServer().getConnections().getOnlinePlayerByAnyIdentifier(args[1]);
                if (playerSession != null){
                    StarNub.getServer().getConnections().nickNameChanger("StarNub", playerSession, args[2], args[3]+".");
                } else {
                    //player not online or not found
                }
            } else {
                //Return Not enough args or to many args
            }
        }
    }
}

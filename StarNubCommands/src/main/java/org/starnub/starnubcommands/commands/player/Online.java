package org.starnub.starnubcommands.commands.player;

import org.starnub.StarNub;
import org.starnub.connectedentities.player.session.Player;
import org.starnub.plugins.Command;

public class Online extends Command {

    @Override
    public void onCommand(Object sender, String command, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            StarNub.getMessageSender().playerMessage("StarNub", sender, StarNub.getServer().getConnections().getOnlinePlayersNameList(sender, true, true));
        } else {
            // CONSOLE
        }

    }
}

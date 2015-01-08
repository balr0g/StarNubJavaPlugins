package org.starnub.starnubcommands.commands.admin;

import org.starnub.StarNub;
import org.starnub.connectedentities.player.session.Player;
import org.starnub.eventsrouter.events.PlayerEvent;
import org.starnub.plugins.Command;

public class AdminAppearance extends Command {

    @Override
    public void onCommand(Object sender, String command, String[] args) {
        Player playerSession = (Player) sender;
        if (playerSession.getCharacter().getAccount() == null) {
            StarNub.getMessageSender().playerMessage("StarNub", sender, "You need an account to go offline.");
        }
        if (args == null) {
            String online = "online";
            if (playerSession.getCharacter().getAccount().getAccountSettings().isAppearOffline()) {
                online = "offline";
            }
            String appearance = "You appear " + online + " to other players";
            StarNub.getMessageSender().playerMessage("StarNub", sender, appearance);
            return;
        }
        if (args[0].equalsIgnoreCase("online")) {
            playerSession.getCharacter().getAccount().getAccountSettings().setAppearOffline(false);
            StarNub.getMessageSender().playerMessage("StarNub", sender, "You will now appear online.");
            StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_Appears_Online", playerSession, null));
        } else if (args[0].equalsIgnoreCase("offline")) {
            playerSession.getCharacter().getAccount().getAccountSettings().setAppearOffline(true);
            StarNub.getMessageSender().playerMessage("StarNub", sender, "You will now appear offline.");
            StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_Appears_Offline", playerSession, null));
        }
    }
}

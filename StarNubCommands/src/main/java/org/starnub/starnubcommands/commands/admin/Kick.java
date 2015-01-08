package org.starnub.starnubcommands.commands.admin;

import org.starnub.StarNub;
import org.starnub.cache.objects.StringCache;
import org.starnub.cache.wrappers.PlayerUUIDCacheWrapper;
import org.starnub.connectedentities.player.session.Player;
import org.starnub.eventsrouter.events.PlayerEvent;
import org.starnub.eventsrouter.events.StarNubEvent;
import org.starnub.eventsrouter.handlers.StarNubEventHandler;
import org.starnub.plugins.Command;

import java.util.concurrent.TimeUnit;


public class Kick extends Command {

    private PlayerUUIDCacheWrapper kickCache;

    public Kick() {
        this.kickCache = new PlayerUUIDCacheWrapper("StarNubCommands", "StarNubCommands - Kick Reason", true, 50, false);
        eventListenerRegister();
    }

    private void eventListenerRegister() {
        StarNub.getStarNubEventRouter().registerEventSubscription("StarNubCommands", "Player_Connection_Success", new StarNubEventHandler() {
            @Override
            public void onEvent(StarNubEvent<String> starNubEvent) {
                Player playerSession = ((PlayerEvent) starNubEvent).getPLAYER_SESSION();
                StringCache stringCache = (StringCache) kickCache.getCache(playerSession.getCharacter().getUuid());
                if (stringCache != null) {
                    StarNub.getTask().getTaskScheduler().scheduleOneTimeTask("StarNubCommands", "StarNubCommands - Send - Player - " + ((PlayerEvent) starNubEvent).getPLAYER_SESSION().getCleanNickName() + " - Kick Reason", new Runnable() {
                        @Override
                        public void run() {
                            StarNub.getMessageSender().playerMessage("StarNub", playerSession, "You were just kicked from the server. Reason: " + stringCache.getString() + ".");
                            kickCache.removeCache(playerSession.getCharacter().getUuid());
                        }
                    }, 6, TimeUnit.SECONDS);
                }
            }
        });
    }

    @Override
    public void onCommand(Object sender, String command, String[] args) {
        Player playerSession = StarNub.getServer().getConnections().getOnlinePlayerByAnyIdentifier(args[0]);
        if (playerSession == null) {
            StarNub.getMessageSender().playerMessage("StarNub", sender, "\"" + args[0] + "\" does not appear to be online.");
            return;
        }
        if (args.length < 2) {
            StarNub.getMessageSender().playerMessage("StarNub", sender, "You did not provide a reason for kicking \"" + playerSession.getNickName() + "\".");
            return;
        }
        Player senderSession = StarNub.getServer().getConnections().getOnlinePlayerByAnyIdentifier(sender);
        StarNub.getServer().getConnections().playerDisconnectPurposely(playerSession, "Kicked");
        StarNub.getMessageSender().serverBroadcast("StarNub", senderSession.getNickName() + " has kicked \"" + playerSession.getNickName() + "\". Reason: " + args[1] + ".");
        kickCache.addCache(playerSession.getCharacter().getUuid(), new StringCache(args[1]));
    }
}

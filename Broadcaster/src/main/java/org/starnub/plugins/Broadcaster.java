package org.starnub.plugins;

import org.starnub.starbounddata.types.color.Colors;
import org.starnub.starnubserver.connections.player.session.PlayerSession;
import org.starnub.starnubserver.pluggable.Plugin;
import org.starnub.starnubserver.resources.StringTokens;
import org.starnub.starnubserver.resources.predicates.PSPredicates;
import org.starnub.utilities.numbers.RandomNumber;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Broadcaster extends Plugin {

    @Override
    public void onEnable() {
        boolean serverBroadcast = (boolean) getConfiguration().getNestedValue("static_broadcast", "enabled");
        if (serverBroadcast) {
            setBroadcastMessages();
        }
        boolean randomBroadcast = (boolean) getConfiguration().getNestedValue("random_broadcast", "enabled");
        if (randomBroadcast) {
            setRandomBroadcastMessage();
        }
    }

    @Override
    public void onDisable() {
        /* No clean up required, since StarNub will unregister our events for us */
    }

    private void setBroadcastMessages() {
        List<Map<String, Object>> broadcasts = (List<Map<String, Object>>) getConfiguration().getNestedValue("static_broadcast", "broadcast");
        for (Map<String, Object> broadcastMap : broadcasts) {
            int interval = (int) broadcastMap.get("interval");
            String color = Colors.validate((String) broadcastMap.get("color"));
            String message = (String) broadcastMap.get("message");
            newStarNubTask("Essentials - Broadcast", true, interval, interval, TimeUnit.MINUTES, () -> {
                PlayerSession.sendChatBroadcastToClientsAllFiltered("ServerName", color + message, PSPredicates.doesNotHavePermission("starnub", "motd", "ignore", false));
            });
        }
    }

    private void setRandomBroadcastMessage() {
        int interval = (int) getConfiguration().getNestedValue("random_broadcast", "interval");
        String color = Colors.validate((String) getConfiguration().getNestedValue("random_broadcast", "color"));
        List<String> message = (List<String>) getConfiguration().getNestedValue("random_broadcast", "message_pool");
        newStarNubTask("Essentials - Random Broadcast", true, interval, interval, TimeUnit.MINUTES, () -> {
            int randInt = RandomNumber.randInt(0, message.size() - 1);
            String randomMessage = message.get(randInt);
            String randomMessageReplacedTokens = StringTokens.replaceTokens(randomMessage);
            PlayerSession.sendChatBroadcastToClientsAllFiltered("ServerName", color + randomMessageReplacedTokens, PSPredicates.doesNotHavePermission("starnub", "motd", "ignore", false));
        });
    }
}

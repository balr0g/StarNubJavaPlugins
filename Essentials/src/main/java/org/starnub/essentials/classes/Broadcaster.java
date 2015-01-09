package org.starnub.essentials.classes;

import org.starnub.starbounddata.types.color.Colors;
import org.starnub.starnubserver.StarNubTask;
import org.starnub.starnubserver.connections.player.session.PlayerSession;
import org.starnub.starnubserver.plugins.resources.PluginConfiguration;
import org.starnub.starnubserver.resources.StringTokens;
import org.starnub.utilities.numbers.RandomNumber;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Broadcaster extends HashSet<StarNubTask> {

    final PluginConfiguration CONFIG;

    public Broadcaster(PluginConfiguration CONFIG) {
        this.CONFIG = CONFIG;
        boolean serverBroadcast = (boolean) CONFIG.getNestedValue("broadcast", "static_broadcast", "enabled");
        if (serverBroadcast) {
            setBroadcastMessages(CONFIG);
        }
        boolean randomBroadcast = (boolean) CONFIG.getNestedValue("broadcast", "random_broadcast", "enabled");
        if (randomBroadcast) {
            setRandomBroadcastMessage(CONFIG);
        }
    }

    private void setBroadcastMessages(PluginConfiguration CONFIG) {
        List<Map<String, Object>> broadcasts = (List<Map<String, Object>>) CONFIG.getNestedValue("broadcast", "static_broadcast", "broadcast");
        for (Map<String, Object> broadcastMap : broadcasts) {
            int interval = (int) broadcastMap.get("interval");
            String color = Colors.validate((String) broadcastMap.get("color"));
            String message = (String) broadcastMap.get("message");
            new StarNubTask("Essentials", "Essentials - Broadcast", true, interval, interval, TimeUnit.MINUTES, () -> {
                PlayerSession.sendChatBroadcastToClientsAll("Essentials", color + message);
            });
        }
    }

    private void setRandomBroadcastMessage(PluginConfiguration CONFIG) {
        int interval = (int) CONFIG.getNestedValue("broadcast", "random_broadcast", "interval");
        String color = Colors.validate((String) CONFIG.getNestedValue("broadcast", "random_broadcast", "color"));
        List<String> message = (List<String>) CONFIG.getNestedValue("broadcast", "random_broadcast", "message_pool");
        new StarNubTask("Essentials", "Essentials - Random Broadcast", true, interval, interval, TimeUnit.MINUTES, () -> {
            int randInt = RandomNumber.randInt(0, message.size() - 1);
            String randomMessage = message.get(randInt);
            String randomMessageReplacedTokens = StringTokens.replaceTokens(randomMessage);
            PlayerSession.sendChatBroadcastToClientsAll("Essentials", color + randomMessageReplacedTokens);
        });

    }

    public void unregisterEventsTask() {
        this.forEach(StarNubTask::removeTask);
    }

}

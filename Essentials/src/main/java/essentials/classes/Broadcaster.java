package essentials.classes;

import io.netty.channel.ChannelHandlerContext;
import starbounddata.packets.chat.ChatReceivePacket;
import starbounddata.types.chat.Mode;
import starbounddata.types.color.Colors;
import starnubserver.StarNub;
import starnubserver.StarNubTask;
import starnubserver.plugins.resources.PluginConfiguration;
import starnubserver.resources.StringTokens;
import utilities.numbers.RandomNumber;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Broadcaster extends HashSet<StarNubTask> {

    final PluginConfiguration CONFIG;

    public Broadcaster(PluginConfiguration CONFIG) {
        this.CONFIG = CONFIG;
        boolean serverBroadcast = (boolean) CONFIG.getNestedValue("server_broadcast", "enabled");
        if (serverBroadcast) {
            setBroadcastMessages(CONFIG);
        }
        boolean randomBroadcast = (boolean) CONFIG.getNestedValue("random_broadcast", "enabled");
        if (randomBroadcast) {
            setRandomBroadcastMessage(CONFIG);
        }
    }

    private void setBroadcastMessages(PluginConfiguration CONFIG) {
        List<Map<String, Object>> broadcasts = (List<Map<String, Object>>) CONFIG.getNestedValue("server_broadcast", "broadcast");
        for (Map<String, Object> broadcastMap : broadcasts) {
            int interval = (int) broadcastMap.get("interval");
            String color = Colors.validate((String) broadcastMap.get("color"));
            String message = (String) broadcastMap.get("message");
            new StarNubTask("Essentials", "Essentials - Broadcast", true, interval, interval, TimeUnit.MINUTES, () -> {
                ChatReceivePacket chatReceivePacket = new ChatReceivePacket(null, Mode.BROADCAST, "Essentials", 0, "Essentials", color + message);
                HashSet<ChannelHandlerContext> playersCtxs = StarNub.getConnections().getCONNECTED_PLAYERS().getOnlinePlayersCtxs();
                chatReceivePacket.routeToGroupNoFlush(playersCtxs);
            });
        }
    }

    private void setRandomBroadcastMessage(PluginConfiguration CONFIG) {
        List<Map<String, Object>> randomBroadcasts = (List<Map<String, Object>>) CONFIG.getNestedValue("server_broadcast", "broadcast");
        for (Map<String, Object> randomBroadcast : randomBroadcasts) {
            int interval = (int) randomBroadcast.get("interval");
            String color = Colors.validate((String) randomBroadcast.get("color"));
            List<String> message = (List<String>) randomBroadcast.get("message_pool");
            new StarNubTask("Essentials", "Essentials - Random Broadcast", true, interval, interval, TimeUnit.MINUTES, () -> {
                int randInt = RandomNumber.randInt(0, message.size());
                String randomMessage = message.get(randInt);
                String randomMessageReplacedTokens = StringTokens.replaceTokens(randomMessage);
                ChatReceivePacket chatReceivePacket = new ChatReceivePacket(null, Mode.BROADCAST, "Essentials", 0, "Essentials", color + randomMessageReplacedTokens);
                HashSet<ChannelHandlerContext> playersCtxs = StarNub.getConnections().getCONNECTED_PLAYERS().getOnlinePlayersCtxs();
                chatReceivePacket.routeToGroupNoFlush(playersCtxs);
            });
        }
    }

    public void unregisterEventsTask() {
        this.forEach(starnubserver.StarNubTask::removeTask);
    }

}

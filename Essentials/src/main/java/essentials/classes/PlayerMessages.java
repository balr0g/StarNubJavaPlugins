package essentials.classes;

import io.netty.channel.ChannelHandlerContext;
import starbounddata.types.color.Colors;
import starnubserver.StarNub;
import starnubserver.StarNubTask;
import starnubserver.cache.objects.PlayerSessionCache;
import starnubserver.cache.wrappers.PlayerAutoCancelTask;
import starnubserver.cache.wrappers.PlayerCtxCacheWrapper;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.events.events.DisconnectData;
import starnubserver.events.starnub.StarNubEventHandler;
import starnubserver.events.starnub.StarNubEventSubscription;
import starnubserver.plugins.resources.PluginConfiguration;
import starnubserver.resources.StringTokens;
import utilities.events.Priority;
import utilities.events.types.ObjectEvent;
import utilities.strings.StringUtilities;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class PlayerMessages {

    private final PluginConfiguration CONFIG;
    private final PlayerCtxCacheWrapper UNSUBSCRIBED_JOIN_LEAVE = new PlayerCtxCacheWrapper("Essentials", "Essentials - Unsubscribe - Join - Leave", true, TimeUnit.SECONDS, 0, 0);
    private final StarNubEventSubscription PLAYER_CONNECTED;
    private final StarNubEventSubscription PLAYER_DISCONNECTED;
    private final PlayerAutoCancelTask JOIN_TASK;

    public PlayerMessages(PluginConfiguration CONFIG) {
        this.CONFIG = CONFIG;

        boolean leaveJoinBoolean = (boolean) CONFIG.getNestedValue("player_messages", "connect_disconnect", "enabled");
        if (leaveJoinBoolean) {
            PLAYER_CONNECTED = playerConnected();
            PLAYER_DISCONNECTED = playerDisconnected();
            JOIN_TASK = new PlayerAutoCancelTask("Essentials", "Join Messages");
        } else {
            PLAYER_CONNECTED = null;
            PLAYER_DISCONNECTED = null;
            JOIN_TASK = null;
        }
    }

    private StarNubEventSubscription playerConnected() {
        return new StarNubEventSubscription("Essentials", Priority.MEDIUM, "Player_Connected", new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent objectEvent) {
                PlayerSession playerSession = (PlayerSession) objectEvent.getEVENT_DATA();
                ChannelHandlerContext clientCtx = playerSession.getCONNECTION().getCLIENT_CTX();
                String playerNameConsole = playerSession.getCleanNickName();
                String playerName = playerSession.getGameName();
                boolean nameColor = (boolean) CONFIG.getNestedValue("player_messages", "connect_disconnect", "name_color");
                if (!nameColor){
                    playerName = StringUtilities.removeColors(playerName);
                }
                int delay = (int) CONFIG.getNestedValue("player_messages", "connect_disconnect", "delay");
                String colorUnvalidated = (String) CONFIG.getNestedValue("player_messages", "connect_disconnect", "message", "color");
                String chatColor = Colors.validate(colorUnvalidated);
                String unformattedMessage = (String) CONFIG.getNestedValue("player_messages", "connect_disconnect", "message", "connect");
                String formattedMessage = String.format(unformattedMessage, playerName + chatColor);
                String completeMessage = StringTokens.replaceTokens(formattedMessage);

                StarNubTask playerJoinTask = new StarNubTask("Essentials", "Essentials - Send - Player - " + playerNameConsole + " - Has Connected Message", delay, TimeUnit.SECONDS, () -> {
                    HashSet<PlayerSession> doNotSendList = new HashSet<>();
                    doNotSendList.add(playerSession);// URGENT-FIX - Ignores not being added , BUT WORKS ON LEAVE AND JOIN IGNORES
                    UNSUBSCRIBED_JOIN_LEAVE.getCACHE_MAP().values().stream().forEach(e -> doNotSendList.add(((PlayerSessionCache) e).getPlayerSession()));
                    String serverName = (String) StarNub.getConfiguration().getNestedValue("starnub_info", "server_name");
                    PlayerSession.sendChatBroadcastToClientsAll(serverName, doNotSendList, chatColor + completeMessage);
                    StarNub.getLogger().cInfoPrint("Essentials", playerNameConsole + " has connected. (IP: " + playerSession.getSessionIpString() + ")");
                });
                JOIN_TASK.registerTask(clientCtx, playerJoinTask);
            }
        });
    }

    private StarNubEventSubscription playerDisconnected(){
        return new StarNubEventSubscription("Essentials", Priority.MEDIUM, "Player_Disconnected", new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent objectEvent) {
                DisconnectData disconnectData = (DisconnectData) objectEvent.getEVENT_DATA();
                PlayerSession playerSession = disconnectData.getPLAYER_SESSION();
                ChannelHandlerContext clientCtx = playerSession.getCONNECTION().getCLIENT_CTX();
                String playerNameConsole = playerSession.getCleanNickName();
                String playerName = playerSession.getGameName();
                boolean recentlyCanceled = JOIN_TASK.recentlyCanceled(clientCtx);
                if (!recentlyCanceled) {
                    boolean nameColor = (boolean) CONFIG.getNestedValue("player_messages", "connect_disconnect", "name_color");
                    if (!nameColor) {
                        playerName = StringUtilities.removeColors(playerName);
                    }
                    String colorUnvalidated = (String) CONFIG.getNestedValue("player_messages", "connect_disconnect", "message", "color");
                    String chatColor = Colors.validate(colorUnvalidated);
                    String unformattedMessage = (String) CONFIG.getNestedValue("player_messages", "connect_disconnect", "message", "disconnect");
                    String formattedMessage = String.format(unformattedMessage, playerName + chatColor);
                    String completeMessage = StringTokens.replaceTokens(formattedMessage);
                    HashSet<PlayerSession> doNotSendList = new HashSet<>();
                    doNotSendList.add(playerSession);
                    UNSUBSCRIBED_JOIN_LEAVE.getCACHE_MAP().values().stream().forEach(e -> doNotSendList.add(((PlayerSessionCache) e).getPlayerSession()));
                    String serverName = (String) StarNub.getConfiguration().getNestedValue("starnub_info", "server_name");
                    PlayerSession.sendChatBroadcastToClientsAll(serverName, doNotSendList, chatColor + completeMessage);
                    StarNub.getLogger().cInfoPrint("Essentials", playerNameConsole + " has disconnected. (IP: " + playerSession.getSessionIpString() + ")");
                }
            }
        });
    }

    public PlayerCtxCacheWrapper getUNSUBSCRIBED_JOIN_LEAVE() {
        return UNSUBSCRIBED_JOIN_LEAVE;
    }

    public void unregisterEventsTask() {
        PLAYER_CONNECTED.removeRegistration();
        PLAYER_DISCONNECTED.removeRegistration();
    }
}

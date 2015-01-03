package essentials.classes;

import io.netty.channel.ChannelHandlerContext;
import starbounddata.packets.chat.ChatReceivePacket;
import starbounddata.types.chat.Mode;
import starbounddata.types.color.Colors;
import starnubserver.StarNub;
import starnubserver.StarNubTask;
import starnubserver.cache.wrappers.PlayerAutoCancelTask;
import starnubserver.cache.wrappers.PlayerCtxCacheWrapper;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.events.events.DisconnectData;
import starnubserver.events.events.StarNubEventTwo;
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
    private final StarNubEventSubscription PLAYER_MESSAGES_FROM_SERVER;
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

        boolean pvpMessage = (boolean) CONFIG.getNestedValue("player_messages", "pvp", "enabled");
        if (pvpMessage) {
            PLAYER_MESSAGES_FROM_SERVER = parseChatMessage();
        } else {
            PLAYER_MESSAGES_FROM_SERVER = null;
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
                    HashSet<ChannelHandlerContext> playersCtxs = StarNub.getConnections().getCONNECTED_PLAYERS().getOnlinePlayersCtxs();
                    HashSet<ChannelHandlerContext> doNotSendList = UNSUBSCRIBED_JOIN_LEAVE.getCacheKeyList();
                    doNotSendList.add(clientCtx);
                    ChatReceivePacket chatReceivePacket = new ChatReceivePacket(null, Mode.BROADCAST, "Essentials", 0, "Essentials", chatColor + completeMessage);
                    chatReceivePacket.routeToGroupNoFlush(playersCtxs, doNotSendList);
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
                    HashSet<ChannelHandlerContext> playersCtxs = StarNub.getConnections().getCONNECTED_PLAYERS().getOnlinePlayersCtxs();
                    HashSet<ChannelHandlerContext> doNotSendList = UNSUBSCRIBED_JOIN_LEAVE.getCacheKeyList();
                    doNotSendList.add(clientCtx);
                    ChatReceivePacket chatReceivePacket = new ChatReceivePacket(null, Mode.BROADCAST, "Essentials", 0, "Essentials", chatColor + completeMessage);
                    chatReceivePacket.routeToGroupNoFlush(playersCtxs, doNotSendList);
                    StarNub.getLogger().cInfoPrint("Essentials", playerNameConsole + " has disconnected. (IP: " + playerSession.getSessionIpString() + ")");
                }
            }
        });
    }
    //messages not purging if player logs out

    private StarNubEventSubscription parseChatMessage() {
        return new StarNubEventSubscription("StarNub", Priority.CRITICAL, "Player_Chat_Parsed_From_Server", new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent objectEvent) {
                StarNubEventTwo starNubEventTwo = (StarNubEventTwo) objectEvent;
                PlayerSession playerSession = (PlayerSession) starNubEventTwo.getEVENT_DATA();
                ChatReceivePacket chatReceivePacket = (ChatReceivePacket) starNubEventTwo.getEVENT_DATA_2();
                String chatMessage = chatReceivePacket.getMessage();

                if (chatMessage.equals("PVP active") || chatMessage.equals("PVP inactive")) {
                    starNubEventTwo.recycle();
                } else if (chatMessage.contains(" is now PVP")) {
                    String playerNameConsole = playerSession.getCleanNickName();
                    String playerName = playerSession.getGameName();
                    boolean nameColor = (boolean) CONFIG.getNestedValue("player_messages", "pvp", "name_color");
                    if (!nameColor) {
                        playerName = StringUtilities.removeColors(playerName);
                    }
                    String unvalidatedColor = (String) CONFIG.getNestedValue("player_messages", "pvp", "mode", "enabled", "color");
                    String chatColor = Colors.validate(unvalidatedColor);
                    String unformattedMessage = (String) CONFIG.getNestedValue("player_messages", "pvp", "mode", "enabled", "message");
                    String formattedMessage = String.format(unformattedMessage, playerName + chatColor);
                    String completeMessage = StringTokens.replaceTokens(formattedMessage);
                    chatReceivePacket.setMessage(completeMessage);
                } else if (chatMessage.contains(" is a big wimp and is no longer PVP")) {
                    String playerNameConsole = playerSession.getCleanNickName();
                    String playerName = playerSession.getGameName();
                    boolean nameColor = (boolean) CONFIG.getNestedValue("player_messages", "pvp", "name_color");
                    if (!nameColor) {
                        playerName = StringUtilities.removeColors(playerName);
                    }
                    String unvalidatedColor = (String) CONFIG.getNestedValue("player_messages", "pvp", "mode", "disabled", "color");
                    String chatColor = Colors.validate(unvalidatedColor);
                    String unformattedMessage = (String) CONFIG.getNestedValue("player_messages", "pvp", "mode", "disabled", "message");
                    String formattedMessage = String.format(unformattedMessage, playerName + chatColor);
                    String completeMessage = StringTokens.replaceTokens(formattedMessage);
                    chatReceivePacket.setMessage(completeMessage);
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
        PLAYER_MESSAGES_FROM_SERVER.removeRegistration();
    }
}

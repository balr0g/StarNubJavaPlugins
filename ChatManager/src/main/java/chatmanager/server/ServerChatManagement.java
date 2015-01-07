package chatmanager.server;

import starbounddata.packets.chat.ChatReceivePacket;
import starbounddata.types.color.Colors;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.events.events.StarNubEventTwo;
import starnubserver.events.starnub.StarNubEventHandler;
import starnubserver.events.starnub.StarNubEventSubscription;
import starnubserver.plugins.resources.PluginConfiguration;
import starnubserver.resources.StringTokens;
import utilities.events.Priority;
import utilities.events.types.ObjectEvent;
import utilities.strings.StringUtilities;

public class ServerChatManagement {

    private final PluginConfiguration CONFIG;
    private final StarNubEventSubscription SERVER_CHAT_PARSE;

    public ServerChatManagement(PluginConfiguration CONFIG, boolean starnubHandleServerChat) {
        this.CONFIG = CONFIG;
        if (!starnubHandleServerChat) {
            SERVER_CHAT_PARSE = starboundForwardServerChatMessage();
        } else {
            SERVER_CHAT_PARSE = starnubParseChatMessage();
        }
    }

    private StarNubEventSubscription starboundForwardServerChatMessage() {
        return new StarNubEventSubscription("StarMub", Priority.CRITICAL, "Player_Chat_Parsed_From_Server", new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent objectEvent) {
                StarNubEventTwo starNubEventTwo = (StarNubEventTwo) objectEvent;
                ChatReceivePacket chatReceivePacket = (ChatReceivePacket) starNubEventTwo.getEVENT_DATA_2();
                chatReceivePacket.routeToDestination();
            }
        });
    }

    private StarNubEventSubscription starnubParseChatMessage() {
        return new StarNubEventSubscription("StarNub", Priority.CRITICAL, "Player_Chat_Parsed_From_Server", new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent objectEvent) {
                StarNubEventTwo starNubEventTwo = (StarNubEventTwo) objectEvent;
                PlayerSession playerSession = (PlayerSession) starNubEventTwo.getEVENT_DATA();
                ChatReceivePacket chatReceivePacket = (ChatReceivePacket) starNubEventTwo.getEVENT_DATA_2();
                String chatMessage = chatReceivePacket.getMessage();
                boolean recycleChat = false;

                /* PVP Message Modification or Replacement*/
                boolean pvpReplacement = (boolean) CONFIG.getNestedValue("message_replacement", "pvp", "enabled");
                if (pvpReplacement) {
                    recycleChat = pvpReplacement(playerSession, chatReceivePacket, chatMessage);
                }

                /* Nick name from server discard */
                boolean notifyOfNickChanges = (boolean) CONFIG.getNestedValue("name_rules", "notify_on_nick_changes");
                if (notifyOfNickChanges && chatMessage.contains("Nick changed to ")) {
                    recycleChat = true;
                }

                if (recycleChat) {
                    starNubEventTwo.recycle();
                } else {
                    chatReceivePacket.routeToDestination();
                }
            }
        });
    }

    private boolean pvpReplacement(PlayerSession playerSession, ChatReceivePacket chatReceivePacket, String chatMessage) {
        if (chatMessage.equals("PVP active") || chatMessage.equals("PVP inactive")) {
            return true;
        }
        if (chatMessage.contains(" is now PVP") || chatMessage.contains(" is a big wimp and is no longer PVP")) {
            String characterName = chatMessage.substring(0, chatMessage.lastIndexOf("is "));
            String name = playerSession.getPlayerCharacter().getName();
            if (characterName.contains(name)) {
                String playerName = playerSession.getGameName();
                boolean nameColor = (boolean) CONFIG.getNestedValue("message_replacement", "pvp", "name_color");
                String channel = (String) CONFIG.getNestedValue("message_replacement", "pvp", "channel");
                boolean isLocal = channel.equalsIgnoreCase("local");
                if (!nameColor) {
                    playerName = StringUtilities.removeColors(playerName);
                }
                String unvalidatedColor;
                String unformattedMessage;
                if (chatMessage.contains(" is now PVP")) {
                    unvalidatedColor = (String) CONFIG.getNestedValue("message_replacement", "pvp", "mode", "enabled", "color");
                    unformattedMessage = (String) CONFIG.getNestedValue("message_replacement", "pvp", "mode", "enabled", "message");
                } else {
                    unvalidatedColor = (String) CONFIG.getNestedValue("message_replacement", "pvp", "mode", "disabled", "color");
                    unformattedMessage = (String) CONFIG.getNestedValue("message_replacement", "pvp", "mode", "disabled", "message");
                }
                String chatColor = Colors.validate(unvalidatedColor);
                String formattedMessage = String.format(unformattedMessage, playerName + chatColor);
                String completeMessage = StringTokens.replaceTokens(formattedMessage);
                if (isLocal) {
                    playerSession.sendLocalMessageToServer("ChatManager", completeMessage);
                } else {
                    chatReceivePacket.setMessage(completeMessage);
                    chatReceivePacket.routeToDestination();
                }
                return true;
            }
        }
        return false;
    }

    public void unregisterEventsTask() {
        SERVER_CHAT_PARSE.removeRegistration();
    }


}

package chatmanager.player;

import starbounddata.packets.chat.ChatSendPacket;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.events.events.StarNubEventTwo;
import starnubserver.events.starnub.StarNubEventHandler;
import starnubserver.events.starnub.StarNubEventSubscription;
import starnubserver.plugins.resources.PluginConfiguration;
import utilities.events.Priority;
import utilities.events.types.ObjectEvent;

public class PlayerChatManagement {

    private final PluginConfiguration CONFIG;
    private final StarNubEventSubscription PLAYER_CHAT_PARSE;

    public PlayerChatManagement(PluginConfiguration CONFIG, boolean starnubHandlePlayerChat) {
        this.CONFIG = CONFIG;
        if (!starnubHandlePlayerChat) {
            PLAYER_CHAT_PARSE = starboundForwardPlayerChatMessage();
        } else {
            PLAYER_CHAT_PARSE = starnubParseChatMessage();
        }
    }

    private StarNubEventSubscription starboundForwardPlayerChatMessage() {
        return new StarNubEventSubscription("StarMub", Priority.CRITICAL, "Player_Chat_Parsed_From_Client", new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent objectEvent) {
                StarNubEventTwo starNubEventTwo = (StarNubEventTwo) objectEvent;
                ChatSendPacket chatSendPacket = (ChatSendPacket) starNubEventTwo.getEVENT_DATA_2();
                chatSendPacket.routeToDestination();
            }
        });
    }

    private StarNubEventSubscription starnubParseChatMessage() {
        return new StarNubEventSubscription("StarNub", Priority.CRITICAL, "Player_Chat_Parsed_From_Client", new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent objectEvent) {
                StarNubEventTwo starNubEventTwo = (StarNubEventTwo) objectEvent;
                PlayerSession playerSession = (PlayerSession) starNubEventTwo.getEVENT_DATA();
                ChatSendPacket chatSendPacket = (ChatSendPacket) starNubEventTwo.getEVENT_DATA_2();
                String chatMessage = chatSendPacket.getMessage();


            }
        });
    }

    public void unregisterEventsTask() {
        PLAYER_CHAT_PARSE.removeRegistration();
    }


}

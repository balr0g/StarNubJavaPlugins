package chatmanager.chat;

import chatmanager.chat.chatroom.ChatRoom;
import chatmanager.chat.settings.ChatIgnore;
import chatmanager.chat.settings.ChatSetting;
import starnubserver.connections.player.session.PlayerSession;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatSession {

    private final PlayerSession PLAYER_SESSION;
    private final ChatSetting CHAT_SETTING;
    private final ConcurrentHashMap<String, ChatRoom> CHAT_ROOMS = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, ChatIgnore> CHAT_IGNORES = new ConcurrentHashMap<>();

    public ChatSession(PlayerSession PLAYER_SESSION, ChatSetting CHAT_SETTING) {
        this.PLAYER_SESSION = PLAYER_SESSION;
        this.CHAT_SETTING = CHAT_SETTING;//Look up ort create session here
    }

    public PlayerSession getPLAYER_SESSION() {
        return PLAYER_SESSION;
    }

    public ChatSetting getCHAT_SETTING() {
        return CHAT_SETTING;
    }

    public ConcurrentHashMap<String, ChatRoom> getCHAT_ROOMS() {
        return CHAT_ROOMS;
    }

    public ConcurrentHashMap<UUID, ChatIgnore> getCHAT_IGNORES() {
        return CHAT_IGNORES;
    }

    public void addChatRoomSubscription() {

    }

    public void removeChatRoomSubscription() {

    }

    public void addIgnore() {

    }

    public void removeIgnore() {

    }
}

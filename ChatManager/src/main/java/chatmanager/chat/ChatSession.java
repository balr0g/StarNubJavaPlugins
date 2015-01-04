package chatmanager.chat;

import chatmanager.chat.chatroom.ChatRoom;
import chatmanager.chat.settings.ChatIgnores;
import chatmanager.chat.settings.ChatSettings;
import starnubserver.connections.player.session.PlayerSession;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatSession {

    private final PlayerSession PLAYER_SESSION;
    private final ChatSettings CHAT_SETTINGS;
    private final ConcurrentHashMap<String, ChatRoom> CHAT_ROOMS = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<UUID, ChatIgnores> CHAT_IGNORES = new ConcurrentHashMap<>();

    public ChatSession(PlayerSession PLAYER_SESSION, ChatSettings CHAT_SETTINGS) {
        this.PLAYER_SESSION = PLAYER_SESSION;
        this.CHAT_SETTINGS = CHAT_SETTINGS;
    }


}

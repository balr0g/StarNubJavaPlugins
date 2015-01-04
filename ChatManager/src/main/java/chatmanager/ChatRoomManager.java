package chatmanager;

import chatmanager.chat.chatroom.ChatRoom;
import starnubserver.plugins.resources.PluginConfiguration;

import java.util.concurrent.ConcurrentHashMap;

public class ChatRoomManager {

    private final PluginConfiguration CONFIG;
    private final ChatManager CHAT_MANAGER;

    private final ConcurrentHashMap<String, ChatRoom> CONNECTED_PLAYERS;

    public ChatRoomManager(PluginConfiguration CONFIG, ChatManager CHAT_MANAGER) {
        this.CONFIG = CONFIG;
        this.CHAT_MANAGER = CHAT_MANAGER;
        this.CONNECTED_PLAYERS = new ConcurrentHashMap<>();

        //ChatRoom Purge Task
        //Unload Empty Chat Rooms
    }

    public void unregisterEventTask() {

    }
}

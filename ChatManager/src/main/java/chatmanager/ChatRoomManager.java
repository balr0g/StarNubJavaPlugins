package chatmanager;

import chatmanager.chat.chatroom.ChatRoom;
import starnubserver.StarNubTask;
import starnubserver.plugins.resources.PluginConfiguration;

import java.util.concurrent.ConcurrentHashMap;

public class ChatRoomManager {

    private final PluginConfiguration CONFIG;
    private final ChatManager CHAT_MANAGER;
    private final StarNubTask CHAT_ROOM_PURGER;
    private final StarNubTask CHAT_ROOM_PRUNE;

    private final ConcurrentHashMap<String, ChatRoom> CONNECTED_PLAYERS;

    public ChatRoomManager(PluginConfiguration CONFIG, ChatManager CHAT_MANAGER) {
        this.CONFIG = CONFIG;
        this.CHAT_MANAGER = CHAT_MANAGER;
        this.CONNECTED_PLAYERS = new ConcurrentHashMap<>();

        //ChatRoom Purge Task //hourly
        //Unload Empty Chat Rooms // 5 minutes
    }

    public void unregisterEventTask() {
        CHAT_ROOM_PURGER.removeTask();
        CHAT_ROOM_PURGER.removeTask();
    }
}

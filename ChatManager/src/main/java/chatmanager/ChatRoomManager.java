package chatmanager;

import chatmanager.chat.chatroom.ChatRoom;
import chatmanager.database.ChatRooms;
import org.joda.time.DateTime;
import starnubserver.StarNubTask;
import starnubserver.plugins.resources.PluginConfiguration;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ChatRoomManager {

    private final PluginConfiguration CONFIG;
    private final ChatManager CHAT_MANAGER;
    private final StarNubTask CHAT_ROOM_PURGER;
    private final StarNubTask CHAT_ROOM_PRUNE;

    private final ConcurrentHashMap<String, ChatRoom> CHAT_ROOMS;

    public ChatRoomManager(PluginConfiguration CONFIG, ChatManager CHAT_MANAGER) {
        this.CONFIG = CONFIG;
        this.CHAT_MANAGER = CHAT_MANAGER;
        this.CHAT_ROOMS = new ConcurrentHashMap<>();
        this.CHAT_ROOM_PURGER = chatRoomPurger();
        this.CHAT_ROOM_PRUNE = chatRoomPrune();
    }

    private StarNubTask chatRoomPurger() {
        boolean autoPurge = (boolean) CONFIG.getNestedValue("chat_rooms", "auto_purge");
        if (!autoPurge) {
            return null;
        } else {
            return new StarNubTask("ChatManager", "ChatManager - ChatRooms - DB Auto Purge", true, 2, 2, TimeUnit.HOURS, () -> {
                List<ChatRoom> chatRooms = ChatRooms.getInstance().getAll();
                int ageDays = (int) CONFIG.getNestedValue("chat_rooms", "age");
                for (ChatRoom chatRoom : chatRooms) {
                    boolean neverPurge = chatRoom.isNeverPurge();
                    if (!neverPurge) {
                        DateTime lastUsed = chatRoom.getLastUsed();
                        DateTime roomExpiration = lastUsed.plusDays(ageDays);
                        boolean beforeNow = roomExpiration.isBeforeNow();
                        if (beforeNow) {
                            chatRoom.deleteFromDatabase();
                        }
                    }
                }
            });
        }
    }

    private StarNubTask chatRoomPrune() {
        return new StarNubTask("ChatManager", "ChatManager - ChatRooms - Empty Room Prune", true, 30, 30, TimeUnit.MINUTES, () -> {
            for (Map.Entry<String, ChatRoom> chatRoomEntry : CHAT_ROOMS.entrySet()) {
                String key = chatRoomEntry.getKey();
                ChatRoom chatRoom = chatRoomEntry.getValue();
                if (chatRoom.getPLAYERS().size() == 0) {
                    CHAT_ROOMS.remove(key);
                }
            }
        });
    }

    public void unregisterEventTask() {
        CHAT_ROOM_PURGER.removeTask();
        CHAT_ROOM_PURGER.removeTask();
    }
}

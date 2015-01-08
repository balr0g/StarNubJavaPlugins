package org.starnub.chatmanager.chat.chatroom;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.starnub.chatmanager.chat.settings.ChatSetting;
import org.starnub.chatmanager.database.ChatRoomSubscriptions;

@DatabaseTable(tableName = "CHAT_ROOM_SUBSCRIPTIONS")
public class ChatRoomSubscription {

    private final static ChatRoomSubscriptions CHAT_ROOM_SUBSCRIPTIONS_DB = ChatRoomSubscriptions.getInstance();

    /* COLUMN NAMES */
    private final static String CHAT_ROOM_SUBSCRIPTION_ID_COLUMN = "CHAT_ROOM_SUBSCRIPTION_ID";
    private final static String CHAT_SETTINGS_ID_COLUMN = "CHAT_SETTINGS_ID";
    private final static String CHAT_ROOM_ID_COLUMN = "CHAT_ROOM_ID";

    @DatabaseField(generatedId = true, columnName = CHAT_ROOM_SUBSCRIPTION_ID_COLUMN)
    private int chatRoomSubscriptionId;
    @DatabaseField(foreign = true, columnName = CHAT_SETTINGS_ID_COLUMN)
    private ChatSetting chatSetting;
    @DatabaseField(foreign = true, columnName = CHAT_ROOM_ID_COLUMN)
    private ChatRoom chatRoom;

    /**
     * Constructor for database purposes
     */
    public ChatRoomSubscription() {
    }
//Remove from chat room method

    public void deleteFromDatabase() {
        deleteFromDatabase(this);
    }

    public static void deleteFromDatabase(ChatRoomSubscription chatRoomSubscription) {
        CHAT_ROOM_SUBSCRIPTIONS_DB.delete(chatRoomSubscription);
    }
}

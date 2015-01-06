package chatmanager.chat.chatroom;

import chatmanager.chat.settings.ChatSetting;
import chatmanager.database.ChatRoomSubscriptions;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

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


    //Remove from chat room method
}

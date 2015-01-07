package chatmanager.chat.chatroom;

import chatmanager.chat.ChatSession;
import chatmanager.chat.settings.ChatSetting;
import chatmanager.database.ChatRooms;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import io.netty.channel.ChannelHandlerContext;
import org.joda.time.DateTime;

import java.util.concurrent.ConcurrentHashMap;

@DatabaseTable(tableName = "CHAT_ROOMS")
public class ChatRoom {

    private final static ChatRooms CHAT_ROOMS_DB = ChatRooms.getInstance();

    /* COLUMN NAMES */
    private final static String CHAT_ROOM_ID_COLUMN = "CHAT_ROOM_ID";
    private final static String ALIAS_COLUMN = "ALIAS";
    private final static String ROOM_CREATOR_ID_COLUMN = "ROOM_CREATOR_ID";
    private final static String LAST_USED_COLUMN = "LAST_USED";
    private final static String NEVER_PURGE_COLUMN = "NEVER_PURGE";
    private final static String NAME_COLOR_COLUMN = "NAME_COLOR";
    private final static String CHAT_COLOR_COLUMN = "CHAT_COLOR";
    private final static String PASSWORD_HASH_COLUMN = "PASSWORD_HASH";
    private final static String INFORMATION_COLUMN = "INFORMATION";
    private final ConcurrentHashMap<ChannelHandlerContext, ChatSession> PLAYERS = new ConcurrentHashMap<>();
    @DatabaseField(id = true, dataType = DataType.STRING, unique = true, columnName = CHAT_ROOM_ID_COLUMN)
    private String chatRoomName;
    @DatabaseField(dataType = DataType.STRING, columnName = ALIAS_COLUMN)
    private String alias;
    @DatabaseField(foreign = true, columnName = ROOM_CREATOR_ID_COLUMN)
    private ChatSetting roomCreatorStarnubId;
    @DatabaseField(dataType = DataType.DATE_TIME, columnName = LAST_USED_COLUMN)
    private DateTime lastUsed;
    @DatabaseField(dataType = DataType.BOOLEAN, columnName = NEVER_PURGE_COLUMN)
    private boolean neverPurge;
    @DatabaseField(dataType = DataType.STRING, columnName = NAME_COLOR_COLUMN)
    private String nameColor;
    @DatabaseField(dataType = DataType.STRING, columnName = CHAT_COLOR_COLUMN)
    private String chatColor;
    @DatabaseField(dataType = DataType.STRING, columnName = PASSWORD_HASH_COLUMN)
    private String password;
    @DatabaseField(dataType = DataType.STRING, columnName = INFORMATION_COLUMN)
    private String info;

    /**
     * Constructor for database purposes
     */
    public ChatRoom() {
    }

    public ChatRoom(String chatRoomName, String alias, ChatSetting roomCreatorStarnubId, DateTime lastUsed, boolean neverPurge, String nameColor, String chatColor, String password, String info) {
        this.chatRoomName = chatRoomName;
        this.alias = alias;
        this.roomCreatorStarnubId = roomCreatorStarnubId;
        this.lastUsed = lastUsed;
        this.neverPurge = neverPurge;
        this.nameColor = nameColor;
        this.chatColor = chatColor;
        this.password = password;
        this.info = info;//Update time on db load
    }

    public String getChatRoomName() {
        return chatRoomName;
    }

    public void setChatRoomName(String chatRoomName) {
        this.chatRoomName = chatRoomName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public ChatSetting getRoomCreatorStarnubId() {
        return roomCreatorStarnubId;
    }

    public void setRoomCreatorStarnubId(ChatSetting roomCreatorStarnubId) {
        this.roomCreatorStarnubId = roomCreatorStarnubId;
    }

    public DateTime getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(DateTime lastUsed) {
        this.lastUsed = lastUsed;
    }

    public boolean isNeverPurge() {
        return neverPurge;
    }

    public void setNeverPurge(boolean neverPurge) {
        this.neverPurge = neverPurge;
    }

    public String getNameColor() {
        return nameColor;
    }

    public void setNameColor(String nameColor) {
        this.nameColor = nameColor;
    }

    public String getChatColor() {
        return chatColor;
    }

    public void setChatColor(String chatColor) {
        this.chatColor = chatColor;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public ConcurrentHashMap<ChannelHandlerContext, ChatSession> getPLAYERS() {
        return PLAYERS;
    }

    public void deleteFromDatabase() {
        deleteFromDatabase(this);
    }

    public static void deleteFromDatabase(ChatRoom chatRoom) {
        CHAT_ROOMS_DB.delete(chatRoom);
    }
}

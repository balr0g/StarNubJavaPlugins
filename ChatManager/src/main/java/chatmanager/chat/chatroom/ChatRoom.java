package chatmanager.chat.chatroom;

import chatmanager.chat.ChatSession;
import chatmanager.chat.settings.ChatSettings;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import io.netty.channel.ChannelHandlerContext;
import org.joda.time.DateTime;

import java.util.concurrent.ConcurrentHashMap;

public class ChatRoom {

    /* COLUMN NAMES */
    //DB CLASS

    private final ConcurrentHashMap<ChannelHandlerContext, ChatSession> PLAYERS = new ConcurrentHashMap<>();
    private int chatRoomId;
    @DatabaseField(id = true, dataType = DataType.STRING, unique = true, columnName = "CHAT_ROOM_NAME")
    private volatile String chatRoomName;
    @DatabaseField(dataType = DataType.STRING, columnName = "ALIAS")
    private volatile String alias;
    @DatabaseField(foreign = true, columnName = "ROOM_CREATOR_STARNUB_ID")
    private ChatSettings roomCreatorStarnubId;
    @DatabaseField(dataType = DataType.DATE_TIME, columnName = "LAST_USED")
    private volatile DateTime lastUsed;
    private volatile boolean temporary; // Auto Delete
    @DatabaseField(dataType = DataType.STRING, columnName = "NAME_COLOR")
    private volatile String nameColor;
    @DatabaseField(dataType = DataType.STRING, columnName = "CHAT_COLOR")
    private volatile String chatColor;
    @DatabaseField(dataType = DataType.STRING, columnName = "PASSWORD")
    private volatile String password;
    @DatabaseField(dataType = DataType.STRING, columnName = "INFO")
    private volatile String info;

}

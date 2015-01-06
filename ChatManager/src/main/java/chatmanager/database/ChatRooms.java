package chatmanager.database;

import chatmanager.chat.chatroom.ChatRoom;
import com.j256.ormlite.support.ConnectionSource;
import starnubserver.database.DatabaseConnection;
import starnubserver.database.TableWrapper;

import java.sql.SQLException;

/**
 * Represents Characters Table that extends the TableWrapper class
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class ChatRooms extends TableWrapper<ChatRoom, String> {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final ChatRooms instance = new ChatRooms();

    /**
     * This constructor is private - Singleton Pattern
     */
    private ChatRooms() {
        super(DatabaseConnection.getInstance().getCommonConnection(), 0, ChatRoom.class, String.class);
    }

    public static ChatRooms getInstance() {
        return instance;
    }

    @Override
    public void tableUpdater(ConnectionSource connection, int oldVersion) throws SQLException {

    }
}


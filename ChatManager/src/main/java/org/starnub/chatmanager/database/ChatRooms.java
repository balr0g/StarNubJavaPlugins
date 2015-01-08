package org.starnub.chatmanager.database;

import com.j256.ormlite.support.ConnectionSource;
import org.starnub.chatmanager.chat.chatroom.ChatRoom;
import org.starnub.starnubserver.database.DatabaseConnection;
import org.starnub.starnubserver.database.TableWrapper;

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


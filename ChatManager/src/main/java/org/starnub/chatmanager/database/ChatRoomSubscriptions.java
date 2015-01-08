package org.starnub.chatmanager.database;

import com.j256.ormlite.support.ConnectionSource;
import org.starnub.chatmanager.chat.chatroom.ChatRoomSubscription;
import org.starnub.starnubserver.database.DatabaseConnection;
import org.starnub.starnubserver.database.TableWrapper;

import java.sql.SQLException;

/**
 * Represents Characters Table that extends the TableWrapper class
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class ChatRoomSubscriptions extends TableWrapper<ChatRoomSubscription, Integer> {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final ChatRoomSubscriptions instance = new ChatRoomSubscriptions();

    /**
     * This constructor is private - Singleton Pattern
     */
    private ChatRoomSubscriptions() {
        super(DatabaseConnection.getInstance().getCommonConnection(), 0, ChatRoomSubscription.class, Integer.class);
    }

    public static ChatRoomSubscriptions getInstance() {
        return instance;
    }

    @Override
    public void tableUpdater(ConnectionSource connection, int oldVersion) throws SQLException {

    }
}


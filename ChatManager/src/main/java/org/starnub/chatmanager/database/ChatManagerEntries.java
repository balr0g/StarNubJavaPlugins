package org.starnub.chatmanager.database;

import com.j256.ormlite.support.ConnectionSource;
import org.starnub.chatmanager.chat.misc.ChatManagerEntry;
import org.starnub.starnubserver.database.DatabaseConnection;
import org.starnub.starnubserver.database.TableWrapper;

import java.sql.SQLException;

/**
 * Represents Characters Table that extends the TableWrapper class
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class ChatManagerEntries extends TableWrapper<ChatManagerEntry, String> {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final ChatManagerEntries instance = new ChatManagerEntries();

    /**
     * This constructor is private - Singleton Pattern
     */
    private ChatManagerEntries() {
        super(DatabaseConnection.getInstance().getCommonConnection(), 0, ChatManagerEntry.class, String.class);
    }

    public static ChatManagerEntries getInstance() {
        return instance;
    }

    @Override
    public void tableUpdater(ConnectionSource connection, int oldVersion) throws SQLException {

    }
}


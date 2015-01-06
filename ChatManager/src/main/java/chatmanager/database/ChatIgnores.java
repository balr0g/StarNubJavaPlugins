package chatmanager.database;

import chatmanager.chat.settings.ChatIgnore;
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
public class ChatIgnores extends TableWrapper<ChatIgnore, Integer> {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final ChatIgnores instance = new ChatIgnores();

    /**
     * This constructor is private - Singleton Pattern
     */
    private ChatIgnores() {
        super(DatabaseConnection.getInstance().getCommonConnection(), 0, ChatIgnore.class, Integer.class);
    }

    public static ChatIgnores getInstance() {
        return instance;
    }

    @Override
    public void tableUpdater(ConnectionSource connection, int oldVersion) throws SQLException {

    }
}


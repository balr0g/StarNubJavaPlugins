package org.starnub.chatmanager.database;

import com.j256.ormlite.support.ConnectionSource;
import org.starnub.chatmanager.chat.settings.ChatSetting;
import org.starnub.starnubserver.database.DatabaseConnection;
import org.starnub.starnubserver.database.TableWrapper;

import java.sql.SQLException;

/**
 * Represents Characters Table that extends the TableWrapper class
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class ChatSettings extends TableWrapper<ChatSetting, Integer> {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final ChatSettings instance = new ChatSettings();

    /**
     * This constructor is private - Singleton Pattern
     */
    private ChatSettings() {
        super(DatabaseConnection.getInstance().getCommonConnection(), 0, ChatSetting.class, Integer.class);
    }

    public static ChatSettings getInstance() {
        return instance;
    }

    @Override
    public void tableUpdater(ConnectionSource connection, int oldVersion) throws SQLException {

    }
}


package org.starnub.chatmanager.database;

import com.j256.ormlite.support.ConnectionSource;
import org.starnub.chatmanager.chat.misc.Muted;
import org.starnub.starnubserver.database.DatabaseConnection;
import org.starnub.starnubserver.database.TableWrapper;

import java.sql.SQLException;

/**
 * Represents Characters Table that extends the TableWrapper class
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0 Beta
 */
public class Muteds extends TableWrapper<Muted, String> {

    /**
     * Represents the only instance of this class - Singleton Pattern
     */
    private static final Muteds instance = new Muteds();

    /**
     * This constructor is private - Singleton Pattern
     */
    private Muteds() {
        super(DatabaseConnection.getInstance().getCommonConnection(), 0, Muted.class, String.class);
    }

    public static Muteds getInstance() {
        return instance;
    }

    @Override
    public void tableUpdater(ConnectionSource connection, int oldVersion) throws SQLException {

    }
}


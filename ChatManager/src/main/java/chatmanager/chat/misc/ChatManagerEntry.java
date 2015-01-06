package chatmanager.chat.misc;

import chatmanager.database.ChatManagerEntries;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import starnubserver.connections.player.account.Account;

import java.util.List;

@DatabaseTable(tableName = "CHAT_MANAGER_ENTRY")
public class ChatManagerEntry {

    private final static ChatManagerEntries CHAT_MANAGER_ENTRIES_DB = ChatManagerEntries.getInstance();

    /* COLUMN NAMES */
    private final static String STAFF_ENTRY_ID_COLUMN = "STAFF_ENTRY_ID";
    private final static String STARNUB_ID_COLUMN = "STARNUB_ID";
    private final static String DESCRIPTION_COLUMN = "DESCRIPTION";

    @DatabaseField(dataType = DataType.INTEGER, generatedId = true, columnName = STAFF_ENTRY_ID_COLUMN)
    private volatile int staffEntryId;

    @DatabaseField(foreign = true, columnName = STARNUB_ID_COLUMN)
    private volatile Account account;

    @DatabaseField(dataType = DataType.STRING, columnName = DESCRIPTION_COLUMN)
    private volatile String description;

    /**
     * Constructor for database purposes
     */
    public ChatManagerEntry() {
    }

    /**
     * This will construct a staff entry which can be used with various classes
     *
     * @param account     Account representing the staff members account
     * @param description String representing the description for this staff entry
     * @param createEntry boolean representing if a database entry should be made
     */
    public ChatManagerEntry(Account account, String description, boolean createEntry) {
        this.account = account;
        this.description = description;
        if (createEntry) {
            CHAT_MANAGER_ENTRIES_DB.createOrUpdate(this);
        }
    }

    /* DB Methods */

    public List<ChatManagerEntry> getStaffEntryByAccount() {
        return getStaffEntryByAccount(this.account);
    }

    public static List<ChatManagerEntry> getStaffEntryByAccount(Account account) {
        return CHAT_MANAGER_ENTRIES_DB.getAllExact(STARNUB_ID_COLUMN, account);
    }

    public List<ChatManagerEntry> getStaffEntryByStaffEntry() {
        return getStaffEntryByStaffEntry(this);
    }

    public static List<ChatManagerEntry> getStaffEntryByStaffEntry(ChatManagerEntry chatManagerEntry) {
        return CHAT_MANAGER_ENTRIES_DB.getAllExact(STAFF_ENTRY_ID_COLUMN, chatManagerEntry);
    }

    public void deleteFromDatabase() {
        deleteFromDatabase(this);
    }

    public static void deleteFromDatabase(ChatManagerEntry chatManagerEntry) {
        CHAT_MANAGER_ENTRIES_DB.delete(chatManagerEntry);
    }


}

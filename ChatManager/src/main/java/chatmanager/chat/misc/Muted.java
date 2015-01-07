package chatmanager.chat.misc;

import chatmanager.database.Muteds;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;

@DatabaseTable(tableName = "MUTEDS")
public class Muted {

    private final static Muteds MUTEDS_DB = Muteds.getInstance();

    /* COLUMN NAMES */
    private final static String MUTED_ID_COLUMN = "MUTED_ID";
    private final static String MUTE_EXPIRE_COLUMN = "MUTE_EXPIRE";
    private final static String CHAT_MANAGER_ENTRY = "CHAT_MANAGER_ENTRY_ID";

    @DatabaseField(generatedId = true, columnName = MUTED_ID_COLUMN)
    private int mutedId;
    @DatabaseField(dataType = DataType.DATE_TIME, columnName = MUTE_EXPIRE_COLUMN)
    private DateTime muteExpire;
    @DatabaseField(foreign = true, columnName = CHAT_MANAGER_ENTRY)
    private ChatManagerEntry chatManagerEntry;

    /**
     * Constructor for database purposes
     */
    public Muted() {
    }

    public Muted(boolean isMuted, DateTime muteExpire, ChatManagerEntry chatManagerEntry) {
        this.muteExpire = muteExpire;
        this.chatManagerEntry = chatManagerEntry;
        MUTEDS_DB.create(this);
    }

    public DateTime getMuteExpire() {
        return muteExpire;
    }

    public void deleteFromDatabase() {
        this.getChatManagerEntry().deleteFromDatabase();
        deleteFromDatabase(this);
    }

    public ChatManagerEntry getChatManagerEntry() {
        return chatManagerEntry;
    }

    public static void deleteFromDatabase(Muted muted) {
        MUTEDS_DB.delete(muted);
    }
}

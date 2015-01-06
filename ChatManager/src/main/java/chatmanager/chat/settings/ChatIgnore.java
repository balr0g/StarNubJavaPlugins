package chatmanager.chat.settings;

import chatmanager.database.ChatIgnores;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.UUID;

@DatabaseTable(tableName = "CHAT_IGNORES")
public class ChatIgnore {

    private final static ChatIgnores CHAT_IGNORES_DB = ChatIgnores.getInstance();

    private final static String CHAT_IGNORE_ID_COLUMN = "CHAT_IGNORE_ID";
    private final static String IGNORE_OWNER_UUID_COLUMN = "IGNORE_OWNER_UUID";
    private final static String IGNORED_UUID_COLUMN = "IGNORED_UUID";

    @DatabaseField(generatedId = true, columnName = CHAT_IGNORE_ID_COLUMN)
    private int chatIgnoreId;
    @DatabaseField(dataType = DataType.UUID, columnName = IGNORE_OWNER_UUID_COLUMN)
    private UUID ignoreOwnerUuid;
    @DatabaseField(dataType = DataType.UUID, columnName = IGNORED_UUID_COLUMN)
    private UUID ignored;

    public int getChatIgnoreId() {
        return chatIgnoreId;
    }

    public void setChatIgnoreId(int chatIgnoreId) {
        this.chatIgnoreId = chatIgnoreId;
    }

    public UUID getIgnoreOwnerUuid() {
        return ignoreOwnerUuid;
    }

    public void setIgnoreOwnerUuid(UUID ignoreOwnerUuid) {
        this.ignoreOwnerUuid = ignoreOwnerUuid;
    }

    public UUID getIgnored() {
        return ignored;
    }

    public void setIgnored(UUID ignored) {
        this.ignored = ignored;
    }
}

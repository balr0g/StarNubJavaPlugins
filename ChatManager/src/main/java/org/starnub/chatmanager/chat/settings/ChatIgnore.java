package org.starnub.chatmanager.chat.settings;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.starnub.chatmanager.database.ChatIgnores;

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

    /**
     * Constructor for database purposes
     */
    public ChatIgnore() {
    }

    public ChatIgnore(UUID ignoreOwnerUuid, UUID ignored) {
        this.ignoreOwnerUuid = ignoreOwnerUuid;
        this.ignored = ignored;
        ChatIgnore chatIgnore = CHAT_IGNORES_DB.getFirstExact(IGNORED_UUID_COLUMN, ignored);
        if (chatIgnore == null) {
            CHAT_IGNORES_DB.createOrUpdate(this);
        }
    }

    public int getChatIgnoreId() {
        return chatIgnoreId;
    }

    public UUID getIgnoreOwnerUuid() {
        return ignoreOwnerUuid;
    }

    public UUID getIgnored() {
        return ignored;
    }

    public void deleteFromDatabase() {
        deleteFromDatabase(this);
    }

    public static void deleteFromDatabase(ChatIgnore chatIgnore) {
        CHAT_IGNORES_DB.delete(chatIgnore);
    }
}

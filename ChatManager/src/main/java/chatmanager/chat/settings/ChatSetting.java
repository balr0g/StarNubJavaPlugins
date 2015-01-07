package chatmanager.chat.settings;

import chatmanager.chat.chatroom.ChatRoom;
import chatmanager.chat.misc.ChatManagerEntry;
import chatmanager.chat.misc.Muted;
import chatmanager.database.ChatSettings;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import org.joda.time.DateTime;
import starnubserver.connections.player.account.Account;

import java.util.UUID;

@DatabaseTable(tableName = "CHAT_SETTINGS")
public class ChatSetting {

    private final static ChatSettings CHAT_SETTINGS_DB = ChatSettings.getInstance();

    /* COLUMN NAMES */
    private final static String CHAT_SETTINGS_ID_COLUMN = "CHAT_SETTINGS_ID";
    private final static String CHARACTER_UUID_COLUMN = "CHARACTER_UUID";
    private final static String STARNUB_ID_COLUMN = "STARNUB_ID";
    private final static String STARNUB_ACCOUNT_NAME_COLUMN = "STARNUB_ACCOUNT_NAME";
    private final static String DEFAULT_CHAT_ROOM_ID_COLUMN = "DEFAULT_CHAT_ROOM_ID";
    private final static String MUTED_COLUMN = "MUTED_ID";
    private final static String IS_IGNORE_WHISPERS_COLUMN = "IS_IGNORE_WHISPERS";

    @DatabaseField(generatedId = true, columnName = CHAT_SETTINGS_ID_COLUMN)
    private int chatSettingsId;
    /* Settings are tied to characters, unless account, then are account based */
    @DatabaseField(dataType = DataType.UUID, columnName = CHARACTER_UUID_COLUMN)
    private UUID characterUuid;
    @DatabaseField(foreign = true, columnName = STARNUB_ID_COLUMN)
    private Account starnubAccountId;
    /* If has account */
    @DatabaseField(dataType = DataType.STRING, columnName = STARNUB_ACCOUNT_NAME_COLUMN)
    private String accountName;
    @DatabaseField(foreign = true, columnName = DEFAULT_CHAT_ROOM_ID_COLUMN)
    private ChatRoom defaultChat;
    @DatabaseField(foreign = true, columnName = MUTED_COLUMN)
    private Muted muted;
    @DatabaseField(dataType = DataType.BOOLEAN, columnName = IS_IGNORE_WHISPERS_COLUMN)
    private boolean ignoreWhispers;

    /**
     * Constructor for database purposes
     */
    public ChatSetting() {
    }

    public ChatSetting(UUID characterUuid, Account starnubAccountId, String accountName, ChatRoom defaultChat, Muted muted, boolean ignoreWhispers) {
        this.characterUuid = characterUuid;
        this.starnubAccountId = starnubAccountId;
        this.accountName = accountName;
        this.defaultChat = defaultChat;
        this.muted = muted;
        this.ignoreWhispers = ignoreWhispers;
    }

    public int getChatSettingsId() {
        return chatSettingsId;
    }

    public void setChatSettingsId(int chatSettingsId) {
        this.chatSettingsId = chatSettingsId;
    }

    public UUID getCharacterUuid() {
        return characterUuid;
    }

    public void setCharacterUuid(UUID characterUuid) {
        this.characterUuid = characterUuid;
    }

    public Account getStarnubAccountId() {
        return starnubAccountId;
    }

    public void setStarnubAccountId(Account starnubAccountId) {
        this.starnubAccountId = starnubAccountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public ChatRoom getDefaultChat() {
        return defaultChat;
    }

    public void setDefaultChat(ChatRoom defaultChat) {
        this.defaultChat = defaultChat;
    }

    public Muted getMuted() {
        return muted;
    }

    public boolean isMuted() {
        return muted != null;
    }

    public void mute(Account staffAccount, DateTime expireDate, String reason) {
        if (muted != null) {
            ChatManagerEntry chatManagerEntry = new ChatManagerEntry(staffAccount, reason);
            this.muted = new Muted(true, expireDate, chatManagerEntry);
            CHAT_SETTINGS_DB.update(this);
        }
    }

    public void unMute() {
        if (muted != null) {
            this.muted.deleteFromDatabase();
            this.muted = null;
            CHAT_SETTINGS_DB.update(this);
        }
    }
    public boolean isIgnoreWhispers() {
        return ignoreWhispers;
    }

    public void ignoreWhispers() {
        this.ignoreWhispers = true;
    }

    public void receiveWhispers() {
        this.ignoreWhispers = false;
    }

    public ChatSetting getByStarnubId() {
        return getByStarnubId(this.starnubAccountId);
    }

    public static ChatSetting getByStarnubId(Account starnubId) {
        return CHAT_SETTINGS_DB.getFirstSimilar(STARNUB_ID_COLUMN, starnubId);
    }

    public ChatSetting getByUuid() {
        return getByUuid(this.characterUuid);
    }

    public static ChatSetting getByUuid(UUID uuid) {
        return CHAT_SETTINGS_DB.getFirstSimilar(CHARACTER_UUID_COLUMN, uuid);
    }

    public void deleteFromDatabase() {
        deleteFromDatabase(this);
    }

    public static void deleteFromDatabase(ChatSetting chatSetting) {
        CHAT_SETTINGS_DB.delete(chatSetting);
    }
}

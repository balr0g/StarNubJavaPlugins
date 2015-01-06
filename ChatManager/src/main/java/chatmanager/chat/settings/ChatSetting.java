package chatmanager.chat.settings;

import chatmanager.chat.chatroom.ChatRoom;
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
    private final static String IS_MUTED_COLUMN = "IS_MUTED";
    private final static String MUTE_EXPIRE_COLUMN = "MUTE_EXPIRE";
    private final static String IS_IGNORE_WHISPERS_COLUMN = "IS_IGNORE_WHISPERS";

    @DatabaseField(generatedId = true, columnName = CHAT_SETTINGS_ID_COLUMN)
    private int chatSettingsId;
    /* Settings are tied to characters, unless account, then are account based */
    @DatabaseField(dataType = DataType.UUID, columnName = CHARACTER_UUID_COLUMN)
    private UUID characterUuid;
    @DatabaseField(foreign = true, columnName = STARNUB_ID_COLUMN)
    private Account starnubAccount;
    /* If has account */
    @DatabaseField(dataType = DataType.STRING, columnName = STARNUB_ACCOUNT_NAME_COLUMN)
    private String accountName;
    @DatabaseField(foreign = true, columnName = DEFAULT_CHAT_ROOM_ID_COLUMN)
    private ChatRoom defaultChat;
    @DatabaseField(dataType = DataType.BOOLEAN, columnName = IS_MUTED_COLUMN)
    private boolean isMuted;
    @DatabaseField(dataType = DataType.DATE_TIME, columnName = MUTE_EXPIRE_COLUMN)
    private DateTime muteExpire;
    @DatabaseField(dataType = DataType.BOOLEAN, columnName = IS_IGNORE_WHISPERS_COLUMN)
    private boolean ignoreWhispers;

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

    public Account getStarnubAccount() {
        return starnubAccount;
    }

    public void setStarnubAccount(Account starnubAccount) {
        this.starnubAccount = starnubAccount;
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

    public boolean isMuted() {
        return isMuted;
    }

    public void unMute() {
        this.isMuted = false;
        this.muteExpire = null;
    }

    public void mute(DateTime length) {
        this.isMuted = true;
        this.muteExpire = length;
    }

    public DateTime getMuteExpire() {
        return muteExpire;
    }

    public void setMuteExpire(DateTime muteExpire) {
        this.muteExpire = muteExpire;
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
}

package chatmanager.chat.settings;

import chatmanager.chat.chatroom.ChatRoom;
import org.joda.time.DateTime;
import starnubserver.connections.player.account.Account;

import java.util.UUID;

public class ChatSettings {

    /* COLUMN NAMES */
    //DB CLASS
    private int chatSettingsId;

    //Settings are either UUID or Account based but can be merged if adding characters to an account
    private UUID characterUuid;
    private Account starnubAccount;

    private String accountName;
    private ChatRoom defaultChat;

    private boolean isMuted;
    private DateTime muteExpire;

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

    public void receievWhispers() {
        this.ignoreWhispers = false;
    }
}

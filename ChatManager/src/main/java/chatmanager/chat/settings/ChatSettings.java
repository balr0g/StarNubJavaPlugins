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

}

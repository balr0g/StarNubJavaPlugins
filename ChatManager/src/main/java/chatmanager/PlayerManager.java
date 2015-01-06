package chatmanager;

import chatmanager.chat.ChatSession;
import chatmanager.chat.settings.ChatSetting;
import io.netty.channel.ChannelHandlerContext;
import org.joda.time.DateTime;
import starnubserver.Connections;
import starnubserver.StarNubTask;
import starnubserver.events.starnub.StarNubEventSubscription;
import starnubserver.plugins.resources.PluginConfiguration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class PlayerManager {

    private final PluginConfiguration CONFIG;
    private final ChatManager CHAT_MANAGER;
    private final StarNubTask PLAYER_MUTE_EXPIRE_CHECK;
    private final StarNubEventSubscription PLAYER_CONNECT;
    private final StarNubEventSubscription PLAYER_DISCONNECT;
    private final StarNubEventSubscription PLAYER_ACCOUNT_CREATED;
    private final StarNubEventSubscription PLAYER_ACCOUNT_DELETED;
    private final StarNubEventSubscription PLAYER_ACCOUNT_CHARACTER_ADDED;
    private final StarNubEventSubscription PLAYER_CHARACTER_DELETED;
    private final ConcurrentHashMap<ChannelHandlerContext, ChatSession> CONNECTED_PLAYERS;

    public PlayerManager(PluginConfiguration CONFIG, ChatManager CHAT_MANAGER) {
        this.CONFIG = CONFIG;
        this.CHAT_MANAGER = CHAT_MANAGER;
        this.PLAYER_MUTE_EXPIRE_CHECK = muteExpireChecks();
        this.PLAYER_CONNECT = handlePlayerConnected();
        this.PLAYER_DISCONNECT = handlePlayerDisconnect();
        this.PLAYER_ACCOUNT_CREATED = handleNewAccountCreation();
        this.PLAYER_ACCOUNT_DELETED = handleAccountDeletion();
        this.PLAYER_ACCOUNT_CHARACTER_ADDED = handleCharacterAddToAccount();
        this.PLAYER_CHARACTER_DELETED = handleCharacterDeletion();
        int expectedPlayers = Connections.getExpectedPlayers();
        int expectedThreads = Connections.getExpectedThreads();
        this.CONNECTED_PLAYERS = new ConcurrentHashMap<>(expectedPlayers, 1.0f, expectedThreads);
    }

    private StarNubTask muteExpireChecks() {
        return new StarNubTask("ChatManager", "ChatManager - Mute Purger", true, 1, 1, TimeUnit.MINUTES, new Runnable() {
            @Override
            public void run() {
                for (ChatSession chatSession : CONNECTED_PLAYERS.values()) {
                    ChatSetting chatSetting = chatSession.getCHAT_SETTINGS();
                    long expireDate = chatSetting.getMuteExpire().getMillis();
                    if (expireDate != 0 && expireDate > DateTime.now().getMillis()) {
                        chatSetting.unMute();
                    }
                }
            }
        });
    }

    private StarNubEventSubscription handlePlayerConnected() {
        //Name Change
        //Settings
        //ChatRooms


        return null;
    }

    private StarNubEventSubscription handlePlayerDisconnect() {
        //CleanUp


        return null;
    }

    private StarNubEventSubscription handleNewAccountCreation() {

        return null;
    }

    private StarNubEventSubscription handleAccountDeletion() {

        return null;
    }

    private StarNubEventSubscription handleCharacterAddToAccount() {

        return null;
    }

    private StarNubEventSubscription handleCharacterDeletion() {

        return null;
    }

    public ConcurrentHashMap<ChannelHandlerContext, ChatSession> getCONNECTED_PLAYERS() {
        return CONNECTED_PLAYERS;
    }

    public void unregisterEventTask() {
        PLAYER_CONNECT.removeRegistration();
        PLAYER_DISCONNECT.removeRegistration();
        PLAYER_ACCOUNT_CREATED.removeRegistration();
        PLAYER_ACCOUNT_DELETED.removeRegistration();
        PLAYER_ACCOUNT_CHARACTER_ADDED.removeRegistration();
        PLAYER_CHARACTER_DELETED.removeRegistration();

    }
}

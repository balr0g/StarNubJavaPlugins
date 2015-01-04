package chatmanager;

import chatmanager.chat.ChatSession;
import io.netty.channel.ChannelHandlerContext;
import starnubserver.Connections;
import starnubserver.events.starnub.StarNubEventSubscription;
import starnubserver.plugins.resources.PluginConfiguration;

import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {

    private final PluginConfiguration CONFIG;
    private final ChatManager CHAT_MANAGER;
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
        int expectedPlayers = Connections.getExpectedPlayers();
        int expectedThreads = Connections.getExpectedThreads();
        this.CONNECTED_PLAYERS = new ConcurrentHashMap<>(expectedPlayers, 1.0f, expectedThreads);
        this.PLAYER_CONNECT = handlePlayerConnected();
        this.PLAYER_DISCONNECT = handlePlayerDisconnect();
        this.PLAYER_ACCOUNT_CREATED = handleNewAccountCreation();
        this.PLAYER_ACCOUNT_DELETED = handleAccountDeletion();
        this.PLAYER_ACCOUNT_CHARACTER_ADDED = handleCharacterAddToAccount();
        this.PLAYER_CHARACTER_DELETED = handleCharacterDeletion();
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

    public void unregisterEventTask() {
        PLAYER_CONNECT.removeRegistration();
        PLAYER_DISCONNECT.removeRegistration();
        PLAYER_ACCOUNT_CREATED.removeRegistration();
        PLAYER_ACCOUNT_DELETED.removeRegistration();
        PLAYER_ACCOUNT_CHARACTER_ADDED.removeRegistration();
        PLAYER_CHARACTER_DELETED.removeRegistration();

    }
}

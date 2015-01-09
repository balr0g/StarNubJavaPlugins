package org.starnub.essentials.classes;

import org.starnub.starbounddata.types.color.Colors;
import org.starnub.starnubserver.StarNubTask;
import org.starnub.starnubserver.cache.wrappers.PlayerUUIDCacheWrapper;
import org.starnub.starnubserver.connections.player.session.PlayerSession;
import org.starnub.starnubserver.events.events.DisconnectData;
import org.starnub.starnubserver.events.starnub.StarNubEventHandler;
import org.starnub.starnubserver.events.starnub.StarNubEventSubscription;
import org.starnub.starnubserver.plugins.resources.PluginConfiguration;
import org.starnub.utilities.cache.objects.BooleanCache;
import org.starnub.utilities.cache.objects.TimeCache;
import org.starnub.utilities.events.Priority;
import org.starnub.utilities.events.types.ObjectEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CrashedHandler extends StarNubEventHandler {

    private final PluginConfiguration CONFIG;
    private final PlayerUUIDCacheWrapper PLAYER_UUID_CACHE = new PlayerUUIDCacheWrapper("Essentials", "Essentials - Crash Notification", false, TimeUnit.SECONDS, 0, 0);
    private final StarNubEventSubscription PLAYER_DISCONNECT;
    private final StarNubEventSubscription RESTART_LISTENER;

    public CrashedHandler(PluginConfiguration CONFIG) {
        this.CONFIG = CONFIG;
        RESTART_LISTENER = new StarNubEventSubscription("Essentials", Priority.MEDIUM, "Essentials_Auto_Restart_Complete", new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent starNubEvent) {
                PLAYER_UUID_CACHE.cachePurge();
            }
        });

        PLAYER_DISCONNECT = new StarNubEventSubscription("Essentials", Priority.MEDIUM, "Player_Disconnected", new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent objectEvent) {
                StarNubTask starNubTask = new StarNubTask("Essentials", "Essentials - Disconnect Cache Removal", 120, TimeUnit.SECONDS, () -> {
                    DisconnectData disconnectData = (DisconnectData) objectEvent.getEVENT_DATA();
                    PlayerSession playerSession = disconnectData.getPLAYER_SESSION();
                    UUID uuid = playerSession.getPlayerCharacter().getUuid();
                    PLAYER_UUID_CACHE.removeCache(uuid);
                });

                StarNubEventSubscription starNubEventSubscription = new StarNubEventSubscription("Essentials", Priority.MEDIUM, "Essentials_Server_Crash", new StarNubEventHandler() {
                    @Override
                    public void onEvent(ObjectEvent objectEvent) {
                        starNubTask.removeTask();
                    }
                });

                new StarNubTask("Essentials", "Essentials - Disconnect Subscription Removal", 121, TimeUnit.SECONDS, starNubEventSubscription::removeRegistration);
            }
        });
    }

    public PlayerUUIDCacheWrapper getPLAYER_UUID_CACHE() {
        return PLAYER_UUID_CACHE;
    }

    public void unregisterEvents() {
        RESTART_LISTENER.removeRegistration();
    }

    @Override
    public void onEvent(ObjectEvent starNubEvent) {
        PlayerSession playerSession = (PlayerSession) starNubEvent.getEVENT_DATA();
        UUID uuid = playerSession.getPlayerCharacter().getUuid();
        TimeCache cache = PLAYER_UUID_CACHE.removeCache(uuid);
        if (cache != null) {
            BooleanCache booleanCache = (BooleanCache) cache;
            if (booleanCache.isBool()) {
                String color = Colors.validate((String) CONFIG.getNestedValue("monitor", "crashed_notification", "color"));
                String message = (String) CONFIG.getNestedValue("monitor", "crashed_notification", "message");
                new StarNubTask("Essentials", "Essentials - Server Crash - Notification - " + uuid.toString(), 5, TimeUnit.SECONDS, () -> playerSession.sendBroadcastMessageToClient("ServerName", color + message));
            }
        }
        PLAYER_UUID_CACHE.addCache(uuid, new BooleanCache(false));
    }


}

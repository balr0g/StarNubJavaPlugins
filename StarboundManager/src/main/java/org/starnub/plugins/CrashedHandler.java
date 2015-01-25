package org.starnub.plugins;

import org.starnub.starbounddata.types.color.Colors;
import org.starnub.starnubserver.StarNubTask;
import org.starnub.starnubserver.cache.wrappers.PlayerUUIDCacheWrapper;
import org.starnub.starnubserver.connections.player.session.PlayerSession;
import org.starnub.starnubserver.events.events.DisconnectData;
import org.starnub.starnubserver.events.starnub.StarNubEventHandler;
import org.starnub.starnubserver.events.starnub.StarNubEventSubscription;
import org.starnub.starnubserver.pluggable.resources.PluggableConfiguration;
import org.starnub.utilities.cache.objects.BooleanCache;
import org.starnub.utilities.cache.objects.TimeCache;
import org.starnub.utilities.events.Priority;
import org.starnub.utilities.events.types.ObjectEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CrashedHandler implements StarNubEventHandler {

    private final PluggableConfiguration CONFIG;
    private final StarboundMonitor STARBOUND_MONITOR;
    private final PlayerUUIDCacheWrapper PLAYER_UUID_CACHE = new PlayerUUIDCacheWrapper("Essentials", "Essentials - Crash Notification", false, TimeUnit.SECONDS, 0, 0);


    public CrashedHandler(PluggableConfiguration CONFIG, StarboundMonitor STARBOUND_MONITOR) {
        this.CONFIG = CONFIG;
        this.STARBOUND_MONITOR = STARBOUND_MONITOR;
        STARBOUND_MONITOR.newStarNubEventSubscription(Priority.MEDIUM, "Essentials_Auto_Restart_Complete", new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent starNubEvent) {
                PLAYER_UUID_CACHE.cachePurge();
            }
        });

        STARBOUND_MONITOR.newStarNubEventSubscription(Priority.MEDIUM, "Player_Disconnected", new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent objectEvent) {
                StarNubTask starNubTask = STARBOUND_MONITOR.newStarNubTask("Essentials - Disconnect Cache Removal", 120, TimeUnit.SECONDS, () -> {
                    DisconnectData disconnectData = (DisconnectData) objectEvent.getEVENT_DATA();
                    PlayerSession playerSession = disconnectData.getPLAYER_SESSION();
                    UUID uuid = playerSession.getPlayerCharacter().getUuid();
                    PLAYER_UUID_CACHE.removeCache(uuid);
                });

                StarNubEventSubscription starNubEventSubscription = STARBOUND_MONITOR.newStarNubEventSubscription(Priority.MEDIUM, "Essentials_Server_Crash", objectEvent1 -> starNubTask.unregister());

                STARBOUND_MONITOR.newStarNubTask("Essentials - Disconnect Subscription Removal", 121, TimeUnit.SECONDS, starNubEventSubscription::unregister);
            }
        });
    }

    public PlayerUUIDCacheWrapper getPLAYER_UUID_CACHE() {
        return PLAYER_UUID_CACHE;
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
                STARBOUND_MONITOR.newStarNubTask("Essentials - Server Crash - Notification - " + uuid.toString(), 5, TimeUnit.SECONDS, () -> playerSession.sendBroadcastMessageToClient("ServerName", color + message));
            }
        }
        PLAYER_UUID_CACHE.addCache(uuid, new BooleanCache(false));
    }
}

package org.starnub.plugins;

import org.starnub.starnubserver.StarNubTask;
import org.starnub.starnubserver.cache.wrappers.PlayerUUIDCacheWrapper;
import org.starnub.starnubserver.connections.player.session.PlayerSession;
import org.starnub.starnubserver.events.events.DisconnectData;
import org.starnub.starnubserver.events.starnub.StarNubEventSubscription;
import org.starnub.starnubserver.pluggable.Plugin;
import org.starnub.utilities.events.Priority;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class StarboundMonitor extends Plugin {

    private ServerMonitor SERVER_MONITOR;
    private AutoRestart AUTO_RESTART;

    public ServerMonitor getSERVER_MONITOR() {
        return SERVER_MONITOR;
    }

    @Override
    public void onEnable() {
        if ((boolean) getConfiguration().getNestedValue("monitor", "enabled")) {
            SERVER_MONITOR = new ServerMonitor(getConfiguration(), this);
        }
        if ((boolean) getConfiguration().getNestedValue("auto_restart", "enabled")) {
            AUTO_RESTART = new AutoRestart(getConfiguration(), this);
        }
    }

    @Override
    public void onDisable() {
        /* No clean up required, since StarNub will deregister our events for us */
    }

    @Override
    public void onRegister() {
        if (AUTO_RESTART != null) {
            newStarNubEventSubscription(Priority.MEDIUM, "Essentials_Server_Crash", starNubEvent -> AUTO_RESTART.clearTask());
            newStarNubEventSubscription(Priority.MEDIUM, "Starbound_Status_Restarting", starNubEvent -> AUTO_RESTART.clearTask());
            newStarNubEventSubscription(Priority.MEDIUM, "Starbound_Status_Online", starNubEvent -> AUTO_RESTART.registerRestart());
        }

        if (SERVER_MONITOR != null) {
            PlayerUUIDCacheWrapper uuidCache = SERVER_MONITOR.getPLAYER_UUID_CACHE();
            newStarNubEventSubscription(Priority.MEDIUM, "Essentials_Auto_Restart_Complete", starNubEvent -> uuidCache.cachePurge());
            newStarNubEventSubscription(Priority.MEDIUM, "Player_Disconnected", objectEvent -> {
                StarNubTask starNubTask = newStarNubTask("Essentials - Disconnect Cache Removal", 120, TimeUnit.SECONDS, () -> {
                    DisconnectData disconnectData = (DisconnectData) objectEvent.getEVENT_DATA();
                    PlayerSession playerSession = disconnectData.getPLAYER_SESSION();
                    UUID uuid = playerSession.getPlayerCharacter().getUuid();
                    uuidCache.removeCache(uuid);
                });
                StarNubEventSubscription starNubEventSubscription = newStarNubEventSubscription(Priority.MEDIUM, "Essentials_Server_Crash", objectEvent1 -> starNubTask.unregister());
                newStarNubTask("Essentials - Disconnect Subscription Removal", 121, TimeUnit.SECONDS, starNubEventSubscription::unregister);
            });
        }

        newStarNubEventSubscription(Priority.MEDIUM, "Player_Connected", SERVER_MONITOR::crashCheck);
        if ((boolean) getConfiguration().getNestedValue("monitor", "start_on_load")) {
            SERVER_MONITOR.startStarnubStartedListener();
        }
        SERVER_MONITOR.starboundStartedListener();
    }
}

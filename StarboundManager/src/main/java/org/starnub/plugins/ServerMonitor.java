package org.starnub.plugins;

import org.starnub.starnubserver.StarNub;
import org.starnub.starnubserver.events.events.StarNubEvent;
import org.starnub.starnubserver.pluggable.resources.PluggableConfiguration;
import org.starnub.starnubserver.servers.starbound.StarboundServer;
import org.starnub.utilities.cache.objects.BooleanCache;
import org.starnub.utilities.cache.objects.TimeCache;
import org.starnub.utilities.events.Priority;
import org.starnub.utilities.file.simplejson.parser.ParseException;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ServerMonitor {

    private final PluggableConfiguration CONFIG;
    private final StarboundMonitor STARBOUND_MONITOR;
    private final CrashedHandler CRASH_HANDLER;

    public ServerMonitor(PluggableConfiguration CONFIG, StarboundMonitor STARBOUND_MONITOR) {
        this.CONFIG = CONFIG;
        this.STARBOUND_MONITOR = STARBOUND_MONITOR;
        CRASH_HANDLER = new CrashedHandler(CONFIG, STARBOUND_MONITOR);
        STARBOUND_MONITOR.newStarNubEventSubscription(Priority.MEDIUM, "Player_Connected", CRASH_HANDLER);
        if ((boolean) CONFIG.getNestedValue("monitor", "start_on_load")) {
            startStarnubStartedListener();
        }
        starboundStartedListener();
    }

    private void startStarnubStartedListener() {
        STARBOUND_MONITOR.newStarNubEventSubscription(Priority.NONE, "StarNub_Startup_Complete", starNubEvent -> startServer());
    }

    private void starboundStartedListener() {
        STARBOUND_MONITOR.newStarNubEventSubscription(Priority.MEDIUM, "Starbound_Status_Online", starNubEvent -> {
            int interval = (int) CONFIG.getNestedValue("monitor", "interval");
            boolean processCheck = (boolean) CONFIG.getNestedValue("monitor", "process_crash");
            if (processCheck) {
                processCheck(interval);
            }
            boolean responsiveness = (boolean) CONFIG.getNestedValue("monitor", "responsiveness", "tcp_query");
            if (responsiveness) {
                int tries = (int) CONFIG.getNestedValue("monitor", "responsiveness", "tries");
                responsiveness(interval, tries);
            }
        });
    }

    private void startServer() {
        try {
            StarNub.getStarboundServer().start();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void processCheck(int interval) {
        STARBOUND_MONITOR.newStarNubTask("Essentials - Starbound Process Check", true, interval, interval, TimeUnit.SECONDS, () -> {
            StarboundServer starboundServer = StarNub.getStarboundServer();
            boolean alive = starboundServer.isAlive();
            if (!alive) {
                startServer();
                serverCrash();
            }
        });
    }

    private void responsiveness(int interval, int tries) {
        STARBOUND_MONITOR.newStarNubTask("Essentials - Starbound Responsive Check", true, interval + 2, interval + 2, TimeUnit.SECONDS, () -> {
            StarboundServer starboundServer = StarNub.getStarboundServer();
            boolean responsive = starboundServer.isResponsive(tries);
            if (!responsive) {
                startServer();
                serverCrash();
            }
        });
    }

    private void serverCrash() {
        new StarNubEvent("Essentials_Server_Crash", this);
        ConcurrentHashMap<UUID, TimeCache> cacheMap = CRASH_HANDLER.getPLAYER_UUID_CACHE().getCACHE_MAP();
        for (TimeCache timeCache : cacheMap.values()) {
            BooleanCache booleanCache = (BooleanCache) timeCache;
            booleanCache.setBool(true);
        }
        STARBOUND_MONITOR.newStarNubTask("Essentials - Server Crash - Cache Clear", 120, TimeUnit.SECONDS, () -> {
            for (Map.Entry entry : cacheMap.entrySet()) {
                UUID key = (UUID) entry.getKey();
                BooleanCache booleanCache = (BooleanCache) entry.getValue();
                UUID[] onlineUUIDS = StarNub.getConnections().getCONNECTED_PLAYERS().getOnlinePlayersUuids();
                if (booleanCache.isBool()) {
                    for (UUID uuid : onlineUUIDS) {
                        if (key == uuid) {
                            booleanCache.setBool(false);
                        } else {
                            cacheMap.remove(key);
                        }
                    }
                }
            }
        });
    }

    public CrashedHandler getCRASH_HANDLER() {
        return CRASH_HANDLER;
    }
}

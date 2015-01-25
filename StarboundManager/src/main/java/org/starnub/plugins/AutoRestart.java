package org.starnub.plugins;

import org.starnub.starbounddata.types.color.Colors;
import org.starnub.starnubdata.generic.DisconnectReason;
import org.starnub.starnubserver.StarNub;
import org.starnub.starnubserver.StarNubTask;
import org.starnub.starnubserver.connections.player.session.PlayerSession;
import org.starnub.starnubserver.events.events.StarNubEvent;
import org.starnub.starnubserver.pluggable.resources.PluggableConfiguration;
import org.starnub.utilities.concurrent.thread.ThreadSleep;
import org.starnub.utilities.events.Priority;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AutoRestart extends HashSet<StarNubTask> {

    private final PluggableConfiguration CONFIG;
    private final StarboundMonitor STARBOUND_MONITOR;

    public AutoRestart(PluggableConfiguration CONFIG, StarboundMonitor STARBOUND_MONITOR) {
        this.CONFIG = CONFIG;
        this.STARBOUND_MONITOR = STARBOUND_MONITOR;
        this.STARBOUND_MONITOR.newStarNubEventSubscription(Priority.MEDIUM, "Essentials_Server_Crash", starNubEvent -> clearTask());
        this.STARBOUND_MONITOR.newStarNubEventSubscription(Priority.MEDIUM, "Starbound_Status_Restarting", starNubEvent -> clearTask());
        this.STARBOUND_MONITOR.newStarNubEventSubscription(Priority.MEDIUM, "Starbound_Status_Online", starNubEvent -> {
            StarNub.getStarboundServer().setRestarting(false);
            registerRestart();
        });
    }

    public void clearTask() {
        this.forEach(StarNubTask::unregister);
        this.clear();
    }

    private void registerRestart() {
        int timer = (int) CONFIG.getNestedValue("auto_restart", "timer");
        this.add(
                new StarNubTask("Essentials", "Essentials - Auto Restart Task - Restart Execution - " + timer, timer, TimeUnit.MINUTES, () -> {
                    StarNub.getStarboundServer().setRestarting(true);
                    StarNub.getConnections().getCONNECTED_PLAYERS().disconnectAllPlayers(DisconnectReason.RESTARTING);
                    STARBOUND_MONITOR.getSERVER_MONITOR().getCRASH_HANDLER().getPLAYER_UUID_CACHE().cachePurge();
                    ThreadSleep.timerSeconds(60);
                    StarNub.getStarboundServer().restart();
                    new StarNubEvent("Essentials_Auto_Restart_Complete", this);
                }));
        boolean notification = (boolean) CONFIG.getNestedValue("auto_restart", "notification", "enabled");
        if (notification) {
            String color = Colors.validate((String) CONFIG.getNestedValue("auto_restart", "notification", "color"));
            String message = (String) CONFIG.getNestedValue("auto_restart", "notification", "message");
            String coloredMessage = color + message;
            List<Integer> times = (List<Integer>) CONFIG.getNestedValue("auto_restart", "notification", "times");
            for (Integer time : times) {
                int eventTime = timer - time;
                this.add(
                        new StarNubTask("Essentials", "Essentials - Auto Restart Task - Notification Message - " + eventTime, eventTime, TimeUnit.MINUTES, () -> {
                            String formattedMessage = String.format(coloredMessage, time);
                            String serverName = (String) StarNub.getConfiguration().getNestedValue("starnub_info", "server_name");
                            PlayerSession.sendChatBroadcastToClientsAll(serverName, formattedMessage);
                            new StarNubEvent("Essentials_Auto_Restart_In_" + time, this);
                        }));
            }
        }
    }
}

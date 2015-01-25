package org.starnub.plugins;

import org.starnub.starbounddata.types.color.Colors;
import org.starnub.starnubdata.generic.DisconnectReason;
import org.starnub.starnubserver.StarNub;
import org.starnub.starnubserver.StarNubTask;
import org.starnub.starnubserver.connections.player.session.PlayerSession;
import org.starnub.starnubserver.events.events.StarNubEvent;
import org.starnub.starnubserver.pluggable.resources.PluggableConfiguration;
import org.starnub.utilities.concurrent.thread.ThreadSleep;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AutoRestart extends HashSet<StarNubTask> {

    private final PluggableConfiguration CONFIG;
    private final StarboundMonitor STARBOUND_MONITOR;

    public AutoRestart(PluggableConfiguration CONFIG, StarboundMonitor STARBOUND_MONITOR) {
        this.CONFIG = CONFIG;
        this.STARBOUND_MONITOR = STARBOUND_MONITOR;
    }

    public void clearTask() {
        this.forEach(StarNubTask::unregister);
        this.clear();
    }

    protected void registerRestart() {
        StarNub.getStarboundServer().setRestarting(false);
        int timer = (int) CONFIG.getNestedValue("auto_restart", "timer");
        this.add(
                STARBOUND_MONITOR.newStarNubTask("Essentials - Auto Restart Task - Restart Execution - " + timer, timer, TimeUnit.MINUTES, () -> {
                    StarNub.getStarboundServer().setRestarting(true);
                    StarNub.getConnections().getCONNECTED_PLAYERS().disconnectAllPlayers(DisconnectReason.RESTARTING);
                    if (STARBOUND_MONITOR.getSERVER_MONITOR() != null) {
                        STARBOUND_MONITOR.getSERVER_MONITOR().getPLAYER_UUID_CACHE().cachePurge();
                    }
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
                        STARBOUND_MONITOR.newStarNubTask("Essentials - Auto Restart Task - Notification Message - " + eventTime, eventTime, TimeUnit.MINUTES, () -> {
                            String formattedMessage = String.format(coloredMessage, time);
                            PlayerSession.sendChatBroadcastToClientsAll("ServerName", formattedMessage);
                            new StarNubEvent("Essentials_Auto_Restart_In_" + time, this);
                        }));
            }
        }
    }
}

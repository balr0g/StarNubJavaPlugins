package essentials.classes;

import starbounddata.packets.chat.ChatReceivePacket;
import starbounddata.types.chat.Mode;
import starbounddata.types.color.Colors;
import starnubdata.generic.DisconnectReason;
import starnubserver.StarNub;
import starnubserver.StarNubTask;
import starnubserver.events.events.StarNubEvent;
import starnubserver.events.starnub.StarNubEventHandler;
import starnubserver.events.starnub.StarNubEventSubscription;
import starnubserver.resources.files.PluginConfiguration;
import utilities.concurrent.thread.ThreadSleep;
import utilities.events.Priority;
import utilities.events.types.ObjectEvent;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AutoRestart extends HashSet<StarNubTask> {

    private final PluginConfiguration CONFIG;
    private final ServerMonitor SERVER_MONITOR;
    private final StarNubEventSubscription CRASH_LISTENER;
    private final StarNubEventSubscription RESTART_LISTENER;
    private final StarNubEventSubscription ONLINE_LISTENER;

    public AutoRestart(PluginConfiguration CONFIG, ServerMonitor serverMonitor) {
        this.CONFIG = CONFIG;
        this.SERVER_MONITOR = serverMonitor;

        this.CRASH_LISTENER = new StarNubEventSubscription("Essentials", Priority.MEDIUM, "Essentials_Server_Crash", new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent starNubEvent) {
                clearTask();
            }
        });

        this.RESTART_LISTENER = new StarNubEventSubscription("Essentials", Priority.MEDIUM, "Starbound_Status_Restarting", new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent starNubEvent) {
                clearTask();
            }
        });

        this.ONLINE_LISTENER = new StarNubEventSubscription("Essentials", Priority.MEDIUM, "Starbound_Status_Online", new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent starNubEvent) {
                StarNub.getStarboundServer().setRestarting(false);
                registerRestart();
            }
        });
    }

    public void clearTask() {
        this.forEach(starnubserver.StarNubTask::removeTask);
        this.clear();
    }

    private void registerRestart() {
        int timer = (int) CONFIG.getNestedValue("auto_restart", "timer");
        this.add(
                new StarNubTask("Essentials", "Essentials - Auto Restart Task", true, timer, timer, TimeUnit.MINUTES, () -> {
                    StarNub.getStarboundServer().setRestarting(true);
                    StarNub.getConnections().getCONNECTED_PLAYERS().disconnectAllPlayers(DisconnectReason.RESTARTING);
                    SERVER_MONITOR.getCRASH_HANDLER().getPLAYER_UUID_CACHE().cachePurge();
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
                        new StarNubTask("Essentials", "Essentials - Auto Restart Task - Message - " + eventTime, true, eventTime, eventTime, TimeUnit.MINUTES, () -> {
                            String formattedMessage = String.format(coloredMessage, eventTime);
                            new ChatReceivePacket(null, Mode.BROADCAST, "Essentials", 0, "Essentials", formattedMessage);
                            new StarNubEvent("Essentials_Auto_Restart_In_" + eventTime, this);
                        }));
            }
        }
    }

    public void unregisterEventsTask() {
        this.forEach(starnubserver.StarNubTask::removeTask);
        CRASH_LISTENER.removeRegistration();
        RESTART_LISTENER.removeRegistration();
        ONLINE_LISTENER.removeRegistration();
    }
}

package essentials.classes;

import starnubserver.events.starnub.StarNubEventHandler;
import starnubserver.events.starnub.StarNubEventSubscription;
import utilities.events.Priority;
import utilities.events.types.ObjectEvent;

public class UptimeTracker {

    private final StarNubEventSubscription STARBOUND_UPTIME_EVENT;
    private final StarNubEventSubscription STARNUB_UPTIME_EVENT;
    private long starboundUptime = 0L;
    private long starnubUptime = 0L;

    public UptimeTracker() {
        this.STARBOUND_UPTIME_EVENT = new StarNubEventSubscription("Essentials", Priority.LOW, "Starbound_Uptime", new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent objectEvent) {
                starboundUptime = (long) objectEvent.getEVENT_DATA();
            }
        });
        this.STARNUB_UPTIME_EVENT = new StarNubEventSubscription("Essentials", Priority.LOW, "StarNub_Uptime", new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent objectEvent) {
                starnubUptime = (long) objectEvent.getEVENT_DATA();
            }
        });
    }

    public long getStarboundUptime() {
        return starboundUptime;
    }

    public long getStarnubUptime() {
        return starnubUptime;
    }

    public void unregisterEventsTask() {
        STARBOUND_UPTIME_EVENT.removeRegistration();
        STARNUB_UPTIME_EVENT.removeRegistration();
    }
}

package essentials.classes;

import starnubserver.events.starnub.StarNubEventHandler;
import starnubserver.events.starnub.StarNubEventSubscription;
import starnubserver.resources.files.PluginConfiguration;
import utilities.events.Priority;
import utilities.events.types.ObjectEvent;

public class Motd {

    final PluginConfiguration CONFIG;
    final StarNubEventSubscription playerConnected;

    public Motd(PluginConfiguration CONFIG) {
        this.CONFIG = CONFIG;
        playerConnected = new StarNubEventSubscription("Essentials", Priority.MEDIUM, "Player_Connected", new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent starNubEvent) {


            }
        });
    }


    public void unregisterEventsTask() {

    }


}

package essentials.classes;

import starnubserver.events.starnub.StarNubEventHandler;
import starnubserver.resources.files.PluginConfiguration;
import utilities.events.types.ObjectEvent;

public class PlayerMessages extends StarNubEventHandler {

    final PluginConfiguration CONFIG;

    public PlayerMessages(PluginConfiguration CONFIG) {
        this.CONFIG = CONFIG;

    }

    @Override
    public void onEvent(ObjectEvent starNubEvent) {

    }

    public void registerEventsTask() {

    }

    public void unregisterEventsTask() {

    }
}

package essentials.classes;

import starnubserver.events.events.StarNubEvent;
import starnubserver.events.starnub.StarNubEventHandler;
import starnubserver.resources.files.PluginConfiguration;

public class PlayerMessages extends StarNubEventHandler {

    final PluginConfiguration CONFIG;

    public PlayerMessages(PluginConfiguration CONFIG) {
        this.CONFIG = CONFIG;

    }

    @Override
    public void onEvent(StarNubEvent starNubEvent) {

    }

    public void registerEventsTask() {

    }

    public void unregisterEventsTask() {

    }
}

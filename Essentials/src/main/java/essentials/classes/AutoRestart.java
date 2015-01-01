package essentials.classes;

import starnubserver.resources.files.PluginConfiguration;

public class AutoRestart {

    final PluginConfiguration CONFIG;
    final ServerMonitor SERVER_MONITOR;

    public AutoRestart(PluginConfiguration CONFIG, ServerMonitor serverMonitor) {
        this.CONFIG = CONFIG;
        this.SERVER_MONITOR = serverMonitor;

        //Clear crash cache
    }

    public void registerEventsTask() {

    }

    public void unregisterEventsTask() {

    }
}

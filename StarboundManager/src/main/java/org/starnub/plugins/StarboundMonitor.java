package org.starnub.plugins;

import org.starnub.starnubserver.pluggable.Plugin;

public class StarboundMonitor extends Plugin {

    private ServerMonitor SERVER_MONITOR;
    private AutoRestart AUTO_RESTARTER;

    public ServerMonitor getSERVER_MONITOR() {
        return SERVER_MONITOR;
    }

    @Override
    public void onEnable() {
        /* Do not need to do anything since the Plugin Manager will call register for us and submit our event listener */
    }

    @Override
    public void onDisable() {
        /* No clean up required, since StarNub will deregister our events for us */
    }

    @Override
    public void onRegister() {
        if ((boolean) getConfiguration().getNestedValue("monitor", "enabled")) {
            SERVER_MONITOR = new ServerMonitor(getConfiguration(), this);
        } else {
            SERVER_MONITOR = null;
        }
        if ((boolean) getConfiguration().getNestedValue("auto_restart", "enabled")) {
            AUTO_RESTARTER = new AutoRestart(getConfiguration(), this);
        } else {
            AUTO_RESTARTER = null;
        }
    }
}

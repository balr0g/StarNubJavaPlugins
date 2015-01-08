/*
* Copyright (C) 2014 www.StarNub.org - Underbalanced
*
* This file is part of org.starnub a Java Wrapper for Starbound.
*
* This above mentioned StarNub software is free software:
* you can redistribute it and/or modify it under the terms
* of the GNU General Public License as published by the Free
* Software Foundation, either version  3 of the License, or
* any later version. This above mentioned CodeHome software
* is distributed in the hope that it will be useful, but
* WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
* the GNU General Public License for more details. You should
* have received a copy of the GNU General Public License in
* this StarNub Software.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.starnub.essentials;

import org.starnub.essentials.classes.*;
import org.starnub.starnubserver.plugins.JavaPlugin;
import org.starnub.starnubserver.plugins.generic.CommandInfo;
import org.starnub.starnubserver.plugins.generic.PluginDetails;
import org.starnub.starnubserver.plugins.resources.PluginConfiguration;
import org.starnub.starnubserver.plugins.resources.PluginRunnables;
import org.starnub.starnubserver.plugins.resources.YAMLFiles;

import java.io.File;

public final class Essentials extends JavaPlugin {

    private ServerMonitor serverMonitor;
    private AutoRestart autoRestart;
    private Motd motd;
    private Broadcaster broadcaster;
    private PlayerMessages playerMessages;
    private UptimeTracker uptimeTracker;

    public Essentials(String NAME, File FILE, String MAIN_CLASS, PluginDetails PLUGIN_DETAILS, PluginConfiguration CONFIGURATION, YAMLFiles FILES, CommandInfo COMMAND_INFO, PluginRunnables PLUGIN_RUNNABLES) {
        super(NAME, FILE, MAIN_CLASS, PLUGIN_DETAILS, CONFIGURATION, FILES, COMMAND_INFO, PLUGIN_RUNNABLES);
    }

    public PlayerMessages getPlayerMessages() {
        return playerMessages;
    }

    public UptimeTracker getUptimeTracker() {
        return uptimeTracker;
    }

    @Override
    public void onPluginEnable() {
        if ((boolean) getCONFIGURATION().getNestedValue("monitor", "enabled")) {
            serverMonitor = new ServerMonitor(getCONFIGURATION());
        }
        if ((boolean) getCONFIGURATION().getNestedValue("auto_restart", "enabled")) {
            autoRestart = new AutoRestart(getCONFIGURATION(), serverMonitor);
        }
        if ((boolean) getCONFIGURATION().getNestedValue("motd", "enabled")) {
            motd = new Motd(getCONFIGURATION());
        }

        broadcaster = new Broadcaster(getCONFIGURATION());

        if ((boolean) getCONFIGURATION().getNestedValue("player_messages", "enabled")) {
            playerMessages = new PlayerMessages(getCONFIGURATION());
        }
        uptimeTracker = new UptimeTracker();
    }

    @Override
    public void onPluginDisable() {
        serverMonitor.unregisterEventsTask();
        autoRestart.unregisterEventsTask();
        motd.unregisterEventsTask();
        broadcaster.unregisterEventsTask();
        playerMessages.unregisterEventsTask();
        uptimeTracker.unregisterEventsTask();
    }
}

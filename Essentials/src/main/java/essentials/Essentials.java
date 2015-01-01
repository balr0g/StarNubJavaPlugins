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

package essentials;

import essentials.classes.*;
import starnubserver.plugins.JavaPlugin;

public final class Essentials extends JavaPlugin {

    private ServerMonitor serverMonitor;
    private AutoRestart autoRestart;
    private Motd motd;
    private Broadcaster broadcaster;
    private PlayerMessages playerMessages;

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
        if ((boolean) getCONFIGURATION().getNestedValue("server_broadcast", "enabled")) {
            broadcaster = new Broadcaster(getCONFIGURATION());
        }
        if ((boolean) getCONFIGURATION().getNestedValue("player_messages", "enabled")) {
            playerMessages = new PlayerMessages(getCONFIGURATION());
        }
    }

    @Override
    public void onPluginDisable() {
        serverMonitor.unregisterEventsTask();
        autoRestart.unregisterEventsTask();
        motd.unregisterEventsTask();
        broadcaster.unregisterEventsTask();
        playerMessages.unregisterEventsTask();
    }
}

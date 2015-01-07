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

package starnubcommands;

import org.codehome.utilities.files.JarResourceToDisk;
import org.starnub.StarNub;
import starnubserver.plugins.JavaPlugin;

/**
 * Empty class as this plugin is a Starnub Commands Plugin
 * <p>
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public final class StarNubCommands extends JavaPlugin {

    public final String pluginName = getClass().getName();
    public volatile String serverName;
    public volatile String serverUrl;

    @Override
    public void onPluginEnable() {
        registerCustomSplit();
        registerNoCommandDelivery();
    }

    @Override
    public void onPluginDisable() {
    }

    private void registerCustomSplit() {
        StarNub.getCommandSender().registerCustomSplitCount("StarNubCommands", new String[] {"kick"}, 2);
    }

    private void registerNoCommandDelivery() {
        StarNub.getMessageSender().registerNoCommandDelivery("StarNubCommands", new String[]{"kick", "online", "who", "online"});
    }

    private void setCaches() {

    }

    public void setCommandHelpInfo(){
        JarResourceToDisk fileExtractor = new JarResourceToDisk();
        try {
            fileExtractor.fileUnpack("defaultfiles/default_help_info.yml", "StarNub/Plugins/StarNubCommands/help_info.yml", false);
        } catch (Exception e) {
            System.err.println("Unable to extract groups.yml. You should create a groups.yml and notify the StarNub.org developers.");
        }
        serverName = (String) StarNub.getPluginManager().getConfiguration("server info").get("server_name");
        serverUrl = (String) StarNub.getPluginManager().getConfiguration("server info").get("server_url");
    }


//RESTART -timer, RESTART now, RESTART interval
}

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

package commandparser;

import starnubserver.events.starnub.StarNubEventSubscription;
import starnubserver.plugins.JavaPlugin;
import starnubserver.plugins.generic.CommandInfo;
import starnubserver.plugins.generic.PluginDetails;
import starnubserver.plugins.resources.PluginRunnables;
import starnubserver.plugins.resources.YAMLFiles;
import starnubserver.resources.files.PluginConfiguration;
import utilities.events.Priority;

import java.io.File;

/**
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public final class CommandParser extends JavaPlugin {

    private StarNubEventSubscription commandEventHandler;
    private CommandHandler commandHandler;

    public CommandParser(String NAME, File FILE, String MAIN_CLASS, PluginDetails PLUGIN_DETAILS, PluginConfiguration CONFIGURATION, YAMLFiles FILES, CommandInfo COMMAND_INFO, PluginRunnables PLUGIN_RUNNABLES) {
        super(NAME, FILE, MAIN_CLASS, PLUGIN_DETAILS, CONFIGURATION, FILES, COMMAND_INFO, PLUGIN_RUNNABLES);

    }

    @Override
    public void onPluginEnable() {
        commandHandler = new CommandHandler(getCONFIGURATION(), getFILES());
        commandEventHandler = new StarNubEventSubscription("StarNub", Priority.CRITICAL, "Player_Command_Parsed_From_Client", commandHandler);
    }

    @Override
    public void onPluginDisable() {
        commandEventHandler.removeRegistration();
        commandHandler.getYAML_RELOAD().removeRegistration();
        commandHandler.getYAML_DUMP().removeRegistration();
    }
}

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

package org.starnub.commandparser;

import org.starnub.starnubserver.events.starnub.StarNubEventSubscription;
import org.starnub.starnubserver.plugins.JavaPlugin;
import org.starnub.starnubserver.plugins.generic.CommandInfo;
import org.starnub.starnubserver.plugins.generic.PluginDetails;
import org.starnub.starnubserver.plugins.resources.PluginConfiguration;
import org.starnub.starnubserver.plugins.resources.PluginRunnables;
import org.starnub.starnubserver.plugins.resources.YAMLFiles;
import utilities.events.Priority;
import utilities.exceptions.CollectionDoesNotExistException;

import java.io.File;
import java.io.IOException;

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

    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

    @Override
    public void onPluginEnable() {
        try {
            commandHandler = new CommandHandler(getCONFIGURATION(), getFILES());
        } catch (IOException | CollectionDoesNotExistException e) {
            e.printStackTrace();
        }
        commandEventHandler = new StarNubEventSubscription("StarNub", Priority.CRITICAL, "Player_Command_Parsed_From_Client", commandHandler);
    }

    @Override
    public void onPluginDisable() {
        commandEventHandler.removeRegistration();
        commandHandler.getYAML_RELOAD().removeRegistration();
        commandHandler.getYAML_DUMP().removeRegistration();
        commandHandler.getPLUGIN_LOAD().removeRegistration();
        commandHandler.getSTARNUB_START_COMPLETE().removeRegistration();
    }
}

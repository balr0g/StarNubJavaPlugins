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
import org.starnub.starnubserver.pluggable.Plugin;
import org.starnub.utilities.events.Priority;

public final class CommandParser extends Plugin {

    @Override
    public void onPluginEnable() {
        /* Do not need to do anything since the Plugin Manager will call register for us and submit our event listeners */
    }

    @Override
    public void onPluginDisable() {
        /* No clean up required, since StarNub will unregister our events for us */
    }

    @Override
    public void register() {
        new StarNubEventSubscription("StarNub", Priority.CRITICAL, "Player_Command_Parsed_From_Client", new CommandHandler(configuration));
    }
}

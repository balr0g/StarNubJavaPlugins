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

import org.apache.commons.lang3.StringUtils;
import org.starnub.starnubdata.generic.CanUse;
import org.starnub.starnubserver.connections.player.session.PlayerSession;
import org.starnub.starnubserver.events.events.StarNubEventTwo;
import org.starnub.starnubserver.pluggable.Command;
import org.starnub.starnubserver.pluggable.PluggableManager;
import org.starnub.starnubserver.pluggable.Plugin;
import org.starnub.utilities.connectivity.connection.Connection;
import org.starnub.utilities.connectivity.connection.ProxyConnection;
import org.starnub.utilities.events.Priority;
import org.starnub.utilities.events.types.ObjectEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CommandParser extends Plugin {

    @Override
    public void onEnable() {
        /* Do not need to do anything since the Plugin Manager will call register for us and submit our event listeners */
    }

    @Override
    public void onDisable() {
        /* No clean up required, since StarNub will unregister our events for us */
    }

    @Override
    public void onRegister() {
        newStarNubEventSubscription(Priority.CRITICAL, "Player_Command_Parsed_From_Client", this::handleCommand);
    }

    private void handleCommand(ObjectEvent objectEvent) {
        StarNubEventTwo starNubEventTwo = (StarNubEventTwo) objectEvent;
        PlayerSession playerSession = (PlayerSession) starNubEventTwo.getEVENT_DATA();
        String fullCommandString = (String) starNubEventTwo.getEVENT_DATA_2();

        List<String> argsList = new ArrayList<>();
        Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(fullCommandString);
        while (m.find()) {
            argsList.add(m.group(1));
        }

        if (argsList.size() < 1) {
            return;
        }

        String commandString = argsList.get(0).replace("/", "").toLowerCase();
        ConcurrentHashMap<String, Command> commands = PluggableManager.getInstance().getCOMMANDS();
        Command command = commands.get(commandString);

        if (command == null) {
            new StarNubEventTwo("Player_Command_Failed_No_Command", playerSession, fullCommandString);
            sendChatMessage(playerSession, "Plugin name or alias \"" + commandString + "\" does not exist or is not loaded.");
            return;
        }

        /* Splits are number of splits on spaces , I.E /r hi how are you (split on 2) = [hi, how are you] */
        int customSplit = command.getCustomSplit();
        boolean fullPermission = true;
        if (customSplit != 0) {
            System.out.println(argsList);
            argsList.clear();
            Collections.addAll(argsList, StringUtils.stripAll(fullCommandString.split(" ", customSplit + 2)));
            System.out.println(argsList);
            fullPermission = false;
        }

        /* Remove the plugin command from the args list*/
        argsList.remove(0);

        CanUse canUse = command.getCanUse();
        Connection connection = playerSession.getCONNECTION();
        boolean isInGame = true;
        if (!(connection instanceof ProxyConnection)) {
            isInGame = false;
        }

        switch (canUse) {
            case PLAYER: {
                if (!isInGame) {
                    new StarNubEventTwo("Player_Command_Failed_Remote_Player_Cannot_Use", playerSession, fullCommandString);
                    sendChatMessage(playerSession, "Remote Player's cannot use the command \"" + commandString + "\".");
                    return;
                }
                break;
            }
            case REMOTE_PLAYER: {
                if (isInGame) {
                    new StarNubEventTwo("Player_Command_Failed_Player_Cannot_Use", playerSession, fullCommandString);
                    sendChatMessage(playerSession, "Player's cannot use the command \"" + commandString + "\".");
                    return;
                }
                break;
            }
        }

        String commandOwner = command.getDetails().getOWNER().toLowerCase();
        String main_arg = null;
        String args[];
        boolean hasPermission;
        if (argsList.size() > 0) {
            main_arg = argsList.get(0);
            args = argsList.toArray(new String[argsList.size()]);
            hasPermission = playerSession.hasPermission(commandOwner, commandString, main_arg, true);
        } else {
            args = new String[0];
            fullPermission = false;
            hasPermission = playerSession.hasPermission(commandOwner, commandString, true);
        }

        if (!hasPermission) {
            String failedCommand = commandString;
            String permissionString = commandOwner + "." + commandString;
            if (fullPermission) {
                failedCommand = failedCommand + " " + main_arg;
                permissionString = permissionString + "." + main_arg;
            }
            new StarNubEventTwo("Player_Command_Failed_Permissions", playerSession, fullCommandString);
            sendChatMessage(playerSession, "You do not have permission to use the command \"" + failedCommand + "\"." + " Permission required: \"" + permissionString + "\".");
            return;
        }

        new StarNubEventTwo("Player_Command_Delivered_To_Plugin", playerSession, fullCommandString);
        command.onCommand(playerSession, commandString, args.length, args);
    }

    private void sendChatMessage(PlayerSession playerSession, String message) {
        playerSession.sendBroadcastMessageToClient("CommandParser", message);
    }
}

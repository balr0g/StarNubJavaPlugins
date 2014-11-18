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

package starboundcommands;

import org.starnub.StarNub;
import org.starnub.plugins.Plugin;
import starboundcommands.commands.player.Whisper;

/**
 * Empty class as this plugin is a Starnub Command bridge
 * <p>
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public final class StarboundCommands extends Plugin {

    @Override
    public void onPluginEnable() {
        new Whisper().startEvents();
        registerCustomSplit();
        registerNoCommandDelivery();
    }

    @Override
    public void onPluginDisable() {
        //STOP EVENTS
    }

    private void registerCustomSplit() {
        StarNub.getCommandSender().registerCustomSplitCount("StarboundCommands", new String[] {"w", "r", "nick"}, 2);
    }

    private void registerNoCommandDelivery() {
        StarNub.getMessageSender().registerNoCommandDelivery("StarboundCommands", new String[] {"w", "r", "pvp", "nick"});
    }

}

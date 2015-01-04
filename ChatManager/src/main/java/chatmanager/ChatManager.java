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

package chatmanager;

import chatmanager.chat.ChatFilter;
import chatmanager.player.PlayerChatManagement;
import chatmanager.server.ServerChatManagement;
import starnubserver.events.events.StarNubEvent;
import starnubserver.plugins.JavaPlugin;
import starnubserver.plugins.generic.CommandInfo;
import starnubserver.plugins.generic.PluginDetails;
import starnubserver.plugins.resources.PluginConfiguration;
import starnubserver.plugins.resources.PluginRunnables;
import starnubserver.plugins.resources.YAMLFiles;

import java.io.File;
import java.util.Map;

/**
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public final class ChatManager extends JavaPlugin {

    private ServerChatManagement serverChatManagement;
    private PlayerChatManagement playerChatManagement;

    private PlayerManager playerManager;
    private ChatRoomManager chatRoomManager;

    private ChatFilter chatFilter;

    public ChatManager(String NAME, File FILE, String MAIN_CLASS, PluginDetails PLUGIN_DETAILS, PluginConfiguration CONFIGURATION, YAMLFiles FILES, CommandInfo COMMAND_INFO, PluginRunnables PLUGIN_RUNNABLES) {
        super(NAME, FILE, MAIN_CLASS, PLUGIN_DETAILS, CONFIGURATION, FILES, COMMAND_INFO, PLUGIN_RUNNABLES);
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public ChatRoomManager getChatRoomManager() {
        return chatRoomManager;
    }

    public ChatFilter getChatFilter() {
        return chatFilter;
    }

    @Override
    public void onPluginEnable() {
        Map<String, Object> commandOverrides = (Map<String, Object>) getCONFIGURATION().getNestedValue("command_overrides");
        for (Map.Entry<String, Object> objectEntry : commandOverrides.entrySet()) {
            String commandOverride = objectEntry.getKey();
            boolean override = (boolean) objectEntry.getValue();
            if (override) {
                new StarNubEvent("Command_Shortcut_Override", "chatman." + commandOverride);
            }
        }
        String whoHandlesServerChat = (String) getCONFIGURATION().getNestedValue("chat_handling", "handler", "from_server");
        boolean starnubHandleServerChat = whoHandlesServerChat.equalsIgnoreCase("starnub");
        String whoHandlesPlayerChat = (String) getCONFIGURATION().getNestedValue("chat_handling", "handler", "from_player");
        boolean starnubHandlePlayerChat = whoHandlesPlayerChat.equalsIgnoreCase("starnub");
        serverChatManagement = new ServerChatManagement(getCONFIGURATION(), starnubHandleServerChat);
        playerChatManagement = new PlayerChatManagement(getCONFIGURATION(), starnubHandlePlayerChat);
        playerManager = new PlayerManager(getCONFIGURATION(), this);
        chatRoomManager = new ChatRoomManager(getCONFIGURATION(), this);
        chatFilter = new ChatFilter(getCONFIGURATION(), this);
    }

    @Override
    public void onPluginDisable() {
        serverChatManagement.unregisterEventsTask();
        playerChatManagement.unregisterEventsTask();
        playerManager.unregisterEventTask();
        chatRoomManager.unregisterEventTask();
    }
}




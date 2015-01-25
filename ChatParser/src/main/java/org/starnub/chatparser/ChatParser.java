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

package org.starnub.chatparser;

import io.netty.channel.ChannelHandlerContext;
import org.starnub.starbounddata.packets.Packet;
import org.starnub.starbounddata.packets.chat.ChatReceivePacket;
import org.starnub.starbounddata.packets.chat.ChatSendPacket;
import org.starnub.starbounddata.types.color.Colors;
import org.starnub.starnubserver.cache.wrappers.PermissionCacheWrapper;
import org.starnub.starnubserver.cache.wrappers.PlayerCtxCacheWrapper;
import org.starnub.starnubserver.connections.player.session.PlayerSession;
import org.starnub.starnubserver.events.events.StarNubEvent;
import org.starnub.starnubserver.events.events.StarNubEventTwo;
import org.starnub.starnubserver.pluggable.Plugin;
import org.starnub.utilities.cache.objects.BooleanCache;
import org.starnub.utilities.cache.objects.TimeCache;
import org.starnub.utilities.events.Priority;

public final class ChatParser extends Plugin {

    private PlayerCtxCacheWrapper CTX_CACHE_CHAT;
    private PermissionCacheWrapper CHAT_PERMISSION;
    private PlayerCtxCacheWrapper CTX_CACHE_COMMAND;
    private PermissionCacheWrapper COMMAND_PERMISSION;

    @Override
    public void onEnable() {
        CTX_CACHE_CHAT = new PlayerCtxCacheWrapper(getRegistrationName(), "StarNub - Chat Parser - Chat Rate", true);
        CHAT_PERMISSION = new PermissionCacheWrapper(getRegistrationName(), "starnub.chatparser.chat");
        CTX_CACHE_COMMAND = new PlayerCtxCacheWrapper(getRegistrationName(), "StarNub - Chat Parser - Command Rate", true);
        COMMAND_PERMISSION = new PermissionCacheWrapper(getRegistrationName(), "starnub.chatparser.command");
        newPacketEventSubscription(Priority.CRITICAL, ChatSendPacket.class, this::chatSend);
        newPacketEventSubscription(Priority.CRITICAL, ChatReceivePacket.class, this::chatReceive);
    }

    @Override
    public void onDisable() {
        /* No clean up required, since StarNub will unregister our events for us */
    }

    public void chatReceive(Packet packet) {
        ChatReceivePacket chatReceivePacket = (ChatReceivePacket) packet;
        ChatReceivePacket chatReceivePacketCopy = chatReceivePacket.copy();
        packet.recycle();
        chatReceivePacketCopy.setFromName("Starbound");
        PlayerSession playerSession = PlayerSession.getPlayerSession(chatReceivePacketCopy);
        new StarNubEventTwo("Player_Chat_Parsed_From_Server", playerSession, chatReceivePacketCopy);
    }


    public void chatSend(Packet packet) {
        ChatSendPacket chatSendPacket = (ChatSendPacket) packet;
        chatSendPacket.recycle();
        PlayerSession playerSession = PlayerSession.getPlayerSession(chatSendPacket);
        String chatMessage = chatSendPacket.getMessage();
        chatMessage = Colors.shortcutReplacement(chatMessage);
        boolean command = chatMessage.startsWith("/");
        if (!command) {
            chatHandle(playerSession, chatSendPacket);
        } else {
            commandHandle(playerSession, chatSendPacket);
        }
    }

    private void chatHandle(PlayerSession playerSession, ChatSendPacket chatSendPacket) {
        ChannelHandlerContext clientCtx = playerSession.getCONNECTION().getCLIENT_CTX();
        BooleanCache cache = (BooleanCache) CHAT_PERMISSION.getCache(clientCtx);
        if (!cache.isBool()) {
            if (cache.isPastDesignatedTimeRefreshTimeNowIfPast(5000)) {
                sendChatMessage(playerSession, "You do not have permission to chat. Permission required: \"starnub.chatparser.chat\".");
                new StarNubEvent("Player_Chat_Failed_No_Permission_Client", playerSession);
                return;
            }
        }
        TimeCache timeCache = CTX_CACHE_CHAT.getCache(clientCtx);
        if (timeCache == null) {
            CTX_CACHE_CHAT.addCache(clientCtx, new TimeCache());
        } else {
            int chatRate = playerSession.getSpecificPermissionInteger("starnub.chatparser_chat");
            if (chatRate < -10000) {
                chatRate = (int) getConfiguration().getValue("global_chat_rate");
            } else if (chatRate == -10000) {
                chatRate = 0;
            }
            boolean isPast = timeCache.isPastDesignatedTimeRefreshTimeNowIfPast(chatRate);
            if (!isPast) {
                String chatSpamMessage = (String) getConfiguration().getValue("spam_message_chat");
                sendChatMessage(playerSession, chatSpamMessage);
                new StarNubEvent("Player_Chat_Failed_Spam_Client", playerSession);
                return;
            }
        }
        ChatSendPacket chatSendPacketCopy = chatSendPacket.copy();
        new StarNubEventTwo("Player_Chat_Parsed_From_Client", playerSession, chatSendPacketCopy);
    }

    private void commandHandle(PlayerSession playerSession, ChatSendPacket chatSendPacket) {
        ChannelHandlerContext clientCtx = playerSession.getCONNECTION().getCLIENT_CTX();
        BooleanCache cache = (BooleanCache) COMMAND_PERMISSION.getCache(clientCtx);
        if (!cache.isBool()) {
            if (cache.isPastDesignatedTimeRefreshTimeNowIfPast(5000)) {
                sendChatMessage(playerSession, "You do not have permission to use commands. Permission required: \"starnub.chatparser.command\".");
                new StarNubEvent("Player_Command_Failed_No_Permission_Client", playerSession);
                return;
            }
        }
        TimeCache timeCache = CTX_CACHE_COMMAND.getCache(clientCtx);
        if (timeCache == null) {
            CTX_CACHE_COMMAND.addCache(clientCtx, new TimeCache());
        } else {
            int commandRate = playerSession.getSpecificPermissionInteger("starnub.chatparser_command");
            if (commandRate < -10000) {
                commandRate = (int) getConfiguration().getValue("global_command_rate");
            } else if (commandRate == -10000) {
                commandRate = 0;
            }
            boolean isPast = timeCache.isPastDesignatedTimeRefreshTimeNowIfPast(commandRate);
            if (!isPast) {
                String chatSpamMessage = (String) getConfiguration().getValue("spam_message_command");
                sendChatMessage(playerSession, chatSpamMessage);
                new StarNubEvent("Player_Command_Failed_Spam_Client", playerSession);
                return;
            }
        }
        new StarNubEventTwo("Player_Command_Parsed_From_Client", playerSession, chatSendPacket.getMessage());
    }

    private void sendChatMessage(PlayerSession playerSession, String message) {
        playerSession.sendBroadcastMessageToClient("ChatParser", message);
    }
}

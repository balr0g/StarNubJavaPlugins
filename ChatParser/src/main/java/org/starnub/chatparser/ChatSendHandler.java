package org.starnub.chatparser;

import io.netty.channel.ChannelHandlerContext;
import org.starnub.starbounddata.packets.Packet;
import org.starnub.starbounddata.packets.chat.ChatSendPacket;
import org.starnub.starnubserver.cache.wrappers.PermissionCacheWrapper;
import org.starnub.starnubserver.cache.wrappers.PlayerCtxCacheWrapper;
import org.starnub.starnubserver.connections.player.session.PlayerSession;
import org.starnub.starnubserver.events.events.StarNubEvent;
import org.starnub.starnubserver.events.events.StarNubEventTwo;
import org.starnub.starnubserver.events.packet.PacketEventHandler;
import org.starnub.starnubserver.pluggable.resources.PluginConfiguration;
import org.starnub.utilities.cache.objects.BooleanCache;
import org.starnub.utilities.cache.objects.TimeCache;

public class ChatSendHandler implements PacketEventHandler {

    private final PluginConfiguration CONFIG;
    private final PlayerCtxCacheWrapper CTX_CACHE_CHAT = new PlayerCtxCacheWrapper("StarNub", "StarNub - Chat Parser - Chat Rate", true);
    private final PermissionCacheWrapper CHAT_PERMISSION = new PermissionCacheWrapper("ChatParser", "starnub.chatparser.chat");
    private final PlayerCtxCacheWrapper CTX_CACHE_COMMAND = new PlayerCtxCacheWrapper("StarNub", "StarNub - Chat Parser - Command Rate", true);
    private final PermissionCacheWrapper COMMAND_PERMISSION = new PermissionCacheWrapper("ChatParser", "starnub.chatparser.command");

    public ChatSendHandler(PluginConfiguration configuration) {
        CONFIG = configuration;
    }

    @Override
    public void onEvent(Packet packet) {
        ChatSendPacket chatSendPacket = (ChatSendPacket) packet;
        chatSendPacket.recycle();
        PlayerSession playerSession = PlayerSession.getPlayerSession(chatSendPacket);
        String chatMessage = chatSendPacket.getMessage();
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
                chatRate = (int) CONFIG.getValue("global_chat_rate");
            } else if (chatRate == -10000) {
                chatRate = 0;
            }
            boolean isPast = timeCache.isPastDesignatedTimeRefreshTimeNowIfPast(chatRate);
            if (!isPast) {
                String chatSpamMessage = (String) CONFIG.getValue("spam_message_chat");
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
                commandRate = (int) CONFIG.getValue("global_command_rate");
            } else if (commandRate == -10000) {
                commandRate = 0;
            }
            boolean isPast = timeCache.isPastDesignatedTimeRefreshTimeNowIfPast(commandRate);
            if (!isPast) {
                String chatSpamMessage = (String) CONFIG.getValue("spam_message_command");
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

package chatparser;

import io.netty.channel.ChannelHandlerContext;
import starbounddata.chat.ChatReceiveChannel;
import starbounddata.packets.Packet;
import starbounddata.packets.chat.ChatSendPacket;
import starnubserver.cache.wrappers.PermissionCacheWrapper;
import starnubserver.cache.wrappers.PlayerCtxCacheWrapper;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.events.events.StarNubEvent;
import starnubserver.events.events.StarNubEventTwo;
import starnubserver.events.packet.PacketEventHandler;
import starnubserver.resources.files.PluginConfiguration;
import utilities.cache.objects.BooleanCache;
import utilities.cache.objects.TimeCache;

public class ChatSendHandler extends PacketEventHandler {

    private final PluginConfiguration CONFIG;
    private final PlayerCtxCacheWrapper CTX_CACHE_CHAT = new PlayerCtxCacheWrapper("StarNub", "StarNub - Chat Parser - Chat Rate", true);
    private final PermissionCacheWrapper CHAT_PERMISSION = new PermissionCacheWrapper("ChatParser", "chatparser.chat");
    private final PlayerCtxCacheWrapper CTX_CACHE_COMMAND = new PlayerCtxCacheWrapper("StarNub", "StarNub - Chat Parser - Command Rate", true);
    private final PermissionCacheWrapper COMMAND_PERMISSION = new PermissionCacheWrapper("ChatParser", "chatparser.command");

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
                playerSession.sendChatMessage("StarNub", ChatReceiveChannel.UNIVERSE, "You do not have permission to chat. Permission required: \"chatparser.chat\".");
                new StarNubEvent("Player_Chat_Failed_No_Permission_Client", playerSession);
                return;
            }
        }
        TimeCache timeCache = CTX_CACHE_CHAT.getCache(clientCtx);
        if (timeCache == null) {
            CTX_CACHE_CHAT.addCache(clientCtx, new TimeCache());
        } else {
            int chatRate = playerSession.getSpecificPermissionInteger("chatparser.chat");
            if (chatRate < -10000) {
                chatRate = (int) CONFIG.getValue("global_chat_rate");
            } else if (chatRate == -10000) {
                chatRate = 0;
            }
            boolean isPast = timeCache.isPastDesignatedTimeRefreshTimeNowIfPast(chatRate);
            if (!isPast) {
                String chatSpamMessage = (String) CONFIG.getValue("spam_message_chat");
                playerSession.sendChatMessage("StarNub", ChatReceiveChannel.UNIVERSE, chatSpamMessage);
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
                playerSession.sendChatMessage("StarNub", ChatReceiveChannel.UNIVERSE, "You do not have permission to use commands. Permission required: \"chatparser.command\".");
                new StarNubEvent("Player_Command_Failed_No_Permission_Client", playerSession);
                return;
            }
        }
        TimeCache timeCache = CTX_CACHE_COMMAND.getCache(clientCtx);
        if (timeCache == null) {
            CTX_CACHE_COMMAND.addCache(clientCtx, new TimeCache());
        } else {
            int commandRate = playerSession.getSpecificPermissionInteger("chatparser.command");
            if (commandRate < -10000) {
                commandRate = (int) CONFIG.getValue("global_command_rate");
            } else if (commandRate == -10000) {
                commandRate = 0;
            }
            boolean isPast = timeCache.isPastDesignatedTimeRefreshTimeNowIfPast(commandRate);
            if (!isPast) {
                String chatSpamMessage = (String) CONFIG.getValue("spam_message_command");
                playerSession.sendChatMessage("StarNub", ChatReceiveChannel.UNIVERSE, chatSpamMessage);
                new StarNubEvent("Player_Command_Failed_Spam_Client", playerSession);
                return;
            }
        }
        new StarNubEventTwo("Player_Command_Parsed_From_Client", playerSession, chatSendPacket.getMessage());
    }
}

package chatmanager.commands;

import chatmanager.ChatManager;
import chatmanager.PlayerManager;
import chatmanager.chat.ChatSession;
import chatmanager.chat.settings.ChatSetting;
import io.netty.channel.ChannelHandlerContext;
import starbounddata.packets.chat.ChatReceivePacket;
import starbounddata.types.chat.Mode;
import starnubserver.Connections;
import starnubserver.cache.objects.ChannelHandlerContextCache;
import starnubserver.cache.wrappers.PlayerCtxCacheWrapper;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.plugins.Command;
import starnubserver.resources.NameBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Whisper extends Command {

    private final PlayerCtxCacheWrapper WHISPER_CACHE;

    public Whisper(String PLUGIN_NAME, HashSet<String> COMMANDS, HashSet<String> MAIN_ARGS, HashMap<String, Integer> CUSTOM_SPLIT, String COMMAND_CLASS, String COMMAND_NAME, int CAN_USE, String DESCRIPTION) {
        super(PLUGIN_NAME, COMMANDS, MAIN_ARGS, CUSTOM_SPLIT, COMMAND_CLASS, COMMAND_NAME, CAN_USE, DESCRIPTION);
        this.WHISPER_CACHE = new PlayerCtxCacheWrapper("ChatManager", "ChatManager - Whisper Cache", true, 2, TimeUnit.SECONDS, 0, 0);
    }

    @Override
    public void onCommand(PlayerSession playerSession, String command, String[] args) {
        ChatManager CHAT_MANAGER = (ChatManager) getPLUGIN();
        PlayerManager PLAYER_MANAGER = CHAT_MANAGER.getPlayerManager();
        ChannelHandlerContext clientCTX = playerSession.getCONNECTION().getCLIENT_CTX();
        int argsLen = args.length;
        switch (command) {
            case "w": {
                switch (argsLen) {
                    case 0: {
                        sendChatMessage(clientCTX, "You did not provide a player name when trying to whisper.");
                        break;
                    }
                    case 1: {
                        sendChatMessage(clientCTX, "You did not provide a message or player name when trying to whisper.");
                        break;
                    }
                    case 2: {
                        String playerId = args[0];
                        PlayerSession playerSessionToWhisper = Connections.getInstance().getCONNECTED_PLAYERS().getOnlinePlayerByAnyIdentifier(playerId);
                        if (playerSessionToWhisper == null) {
                            sendChatMessage(clientCTX, "StarNub could not find a player using the identification provided \"" + playerId + "\".");
                        } else {
                            ChatSession whisperOrigination = PLAYER_MANAGER.getCONNECTED_PLAYERS().get(clientCTX);
                            ChannelHandlerContext whisperDestinationCTX = playerSession.getCONNECTION().getCLIENT_CTX();
                            ChatSession whisperDestination = PLAYER_MANAGER.getCONNECTED_PLAYERS().get(whisperDestinationCTX);
                            handleWhisper(CHAT_MANAGER, whisperOrigination, whisperDestination, args[1]);
                        }
                        break;
                    }
                }
                break;
            }
            case "r": {
                handleReply(CHAT_MANAGER, clientCTX, args[1]);
                break;
            }
        }
    }

    private void sendChatMessage(ChannelHandlerContext clientCTX, String chatMessage) {
        new ChatReceivePacket(clientCTX, Mode.BROADCAST, "ChatManager", 0, "ChatManager", chatMessage).routeToDestination();
    }

    private void handleWhisper(ChatManager chatManager, ChatSession whisperOrigination, ChatSession whisperDestination, String chatMessage) {
        ChatSetting woChatSetting = whisperOrigination.getCHAT_SETTINGS();
        ChannelHandlerContext woCTX = whisperOrigination.getPLAYER_SESSION().getCONNECTION().getCLIENT_CTX();
        ChatSetting wdChatSetting = whisperDestination.getCHAT_SETTINGS();
        ChannelHandlerContext wdCTX = whisperDestination.getPLAYER_SESSION().getCONNECTION().getCLIENT_CTX();

        boolean woIsAccepting = woChatSetting.isIgnoreWhispers();
        boolean wdIsAccepting = wdChatSetting.isIgnoreWhispers();
        if (!woIsAccepting) {
            sendChatMessage(woCTX, "You currently have whispers disabled, you must enable whispers to be able to whisper.");
            return;
        } else if (!wdIsAccepting) {
            sendChatMessage(woCTX, "The player you are trying to whisper has whispers turned disabled.");
            return;
        }

        boolean woIsMuted = woChatSetting.isMuted();
        boolean wdIsMuted = wdChatSetting.isMuted();
        if (woIsMuted) {
            sendChatMessage(woCTX, "You are currently muted and cannot send chat messages.");
            return;
        } else if (wdIsMuted) {
            sendChatMessage(woCTX, "The player you are trying to whisper is muted and cannot received whispers.");
            return;
        }

        PlayerSession woSession = whisperOrigination.getPLAYER_SESSION();
        PlayerSession wdSession = whisperDestination.getPLAYER_SESSION();
        UUID woUUID = woSession.getPlayerCharacter().getUuid();
        UUID wdUUID = wdSession.getPlayerCharacter().getUuid();
        boolean isIgnoring = whisperOrigination.getCHAT_IGNORES().containsKey(wdUUID);
        boolean isBeingIgnored = whisperDestination.getCHAT_IGNORES().containsKey(woUUID);
        if (isIgnoring) {
            sendChatMessage(woCTX, "You are currently ignoring this player. Please unignore them in order to chat with them.");
            return;
        } else if (isBeingIgnored) {
            sendChatMessage(woCTX, "The player you are trying to whisper has you ignored and will not receive your whispers.");
            return;
        }


        sendWhisper(chatManager, woCTX, woSession, wdSession, chatMessage);

        ChannelHandlerContextCache woCtxCache = (ChannelHandlerContextCache) WHISPER_CACHE.getCache(woCTX);
        ChannelHandlerContextCache wdCtxCache = (ChannelHandlerContextCache) WHISPER_CACHE.getCache(wdCTX);
        if (woCtxCache == null) {
            WHISPER_CACHE.addCache(woCTX, new ChannelHandlerContextCache(wdCTX));
        } else {
            woCtxCache.setCtx(wdCTX);
        }
        if (wdCtxCache == null) {
            WHISPER_CACHE.addCache(wdCTX, new ChannelHandlerContextCache(woCTX));
        } else {
            wdCtxCache.setCtx(woCTX);
        }
    }

    private void handleReply(ChatManager chatManager, ChannelHandlerContext clientCTX, String chatMessage) {
        ChatSession chatSession = chatManager.getPlayerManager().getCONNECTED_PLAYERS().get(clientCTX);
        PlayerSession woSession = chatSession.getPLAYER_SESSION();
        ChannelHandlerContextCache wdCacheCtx = (ChannelHandlerContextCache) WHISPER_CACHE.getCache(clientCTX);
        PlayerSession wdSession = Connections.getInstance().getCONNECTED_PLAYERS().getOnlinePlayerByAnyIdentifier(wdCacheCtx);
        if (wdSession == null) {
            sendChatMessage(clientCTX, "The player you are replying to is no longer online.");
            return;
        }
        sendWhisper(chatManager, clientCTX, woSession, wdSession, chatMessage);
    }

    private void sendWhisper(ChatManager chatManager, ChannelHandlerContext clientCTX, PlayerSession sender, PlayerSession receiver, String chatMessage) {
        String filteredChatMessage = chatManager.getChatFilter().filterChat(sender, chatMessage);
        String senderString = NameBuilder.getInstance().msgPlayerNameBuilderFinal(sender, true, false);
        String receiverString = NameBuilder.getInstance().msgPlayerNameBuilderFinal(receiver, true, false);
        String packetName = senderString + " -> " + receiverString;
        new ChatReceivePacket(clientCTX, Mode.WHISPER, "Whisper", 0, packetName, filteredChatMessage).routeToDestination();
    }


}

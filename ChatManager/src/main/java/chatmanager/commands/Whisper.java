package chatmanager.commands;

import chatmanager.ChatManager;
import chatmanager.PlayerManager;
import chatmanager.chat.ChatSession;
import chatmanager.chat.settings.ChatSetting;
import io.netty.channel.ChannelHandlerContext;
import starnubserver.Connections;
import starnubserver.cache.objects.ChannelHandlerContextCache;
import starnubserver.cache.wrappers.PlayerCtxCacheWrapper;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.plugins.Command;

import java.util.Arrays;
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
        System.out.println(Arrays.toString(args));
        switch (command) {
            case "w": {
                switch (argsLen) {
                    case 0: {
                        sendChatMessage(playerSession, "You did not provide a player name when trying to whisper.");
                        break;
                    }
                    case 1: {
                        sendChatMessage(playerSession, "You did not provide a message or player name when trying to whisper.");
                        break;
                    }
                    case 2: {
                        String playerId = args[0];
                        PlayerSession playerSessionToWhisper = Connections.getInstance().getCONNECTED_PLAYERS().getOnlinePlayerByAnyIdentifier(playerId);
                        if (playerSessionToWhisper == null) {
                            sendChatMessage(playerSession, "StarNub could not find a player using the identification provided \"" + playerId + "\".");
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
                handleReply(CHAT_MANAGER, playerSession, clientCTX, args[1]);
                break;
            }
        }
    }

    private void sendChatMessage(PlayerSession playerSession, String chatMessage) {
        playerSession.sendBroadcastMessageToClient("ChatManager", chatMessage);
    }

    private void handleWhisper(ChatManager chatManager, ChatSession whisperOrigination, ChatSession whisperDestination, String chatMessage) {
        PlayerSession woPlayerSession = whisperOrigination.getPLAYER_SESSION();
        PlayerSession wdPlayerSession = whisperDestination.getPLAYER_SESSION();
        ChatSetting woChatSetting = whisperOrigination.getCHAT_SETTING();
        ChannelHandlerContext woCTX = woPlayerSession.getCONNECTION().getCLIENT_CTX();
        ChatSetting wdChatSetting = whisperDestination.getCHAT_SETTING();
        ChannelHandlerContext wdCTX = wdPlayerSession.getCONNECTION().getCLIENT_CTX();

        boolean woIsAccepting = woChatSetting.isIgnoreWhispers();
        boolean wdIsAccepting = wdChatSetting.isIgnoreWhispers();
        if (!woIsAccepting) {
            sendChatMessage(woPlayerSession, "You currently have whispers disabled, you must enable whispers to be able to whisper.");
            return;
        } else if (!wdIsAccepting) {
            sendChatMessage(woPlayerSession, "The player you are trying to whisper has whispers turned disabled.");
            return;
        }

        boolean woIsMuted = woChatSetting.getMuted() != null;
        boolean wdIsMuted = wdChatSetting.getMuted() != null;
        if (woIsMuted) {
            sendChatMessage(woPlayerSession, "You are currently muted and cannot send chat messages.");
            return;
        } else if (wdIsMuted) {
            sendChatMessage(woPlayerSession, "The player you are trying to whisper is muted and cannot received whispers.");
            return;
        }

        UUID woUUID = woPlayerSession.getPlayerCharacter().getUuid();
        UUID wdUUID = wdPlayerSession.getPlayerCharacter().getUuid();
        boolean isIgnoring = whisperOrigination.getCHAT_IGNORES().containsKey(wdUUID);
        boolean isBeingIgnored = whisperDestination.getCHAT_IGNORES().containsKey(woUUID);
        if (isIgnoring) {
            sendChatMessage(woPlayerSession, "You are currently ignoring this player. Please unignore them in order to chat with them.");
            return;
        } else if (isBeingIgnored) {
            sendChatMessage(woPlayerSession, "The player you are trying to whisper has you ignored and will not receive your whispers.");
            return;
        }


        sendWhisper(woPlayerSession, wdPlayerSession, chatMessage);

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

    private void handleReply(ChatManager chatManager, PlayerSession playerSession, ChannelHandlerContext clientCTX, String chatMessage) {
        ChatSession chatSession = chatManager.getPlayerManager().getCONNECTED_PLAYERS().get(clientCTX);
        PlayerSession woSession = chatSession.getPLAYER_SESSION();
        ChannelHandlerContextCache wdCacheCtx = (ChannelHandlerContextCache) WHISPER_CACHE.getCache(clientCTX);
        PlayerSession wdSession = Connections.getInstance().getCONNECTED_PLAYERS().getOnlinePlayerByAnyIdentifier(wdCacheCtx);
        if (wdSession == null) {
            sendChatMessage(playerSession, "The player you are replying to is no longer online.");
            return;
        }
        sendWhisper(woSession, wdSession, chatMessage);
    }

    private void sendWhisper(PlayerSession sender, PlayerSession receiver, String chatMessage) {
        sender.sendWhisperMessageToClient(sender, receiver, chatMessage);
    }


}

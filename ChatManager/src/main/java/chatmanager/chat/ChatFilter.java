package chatmanager.chat;

import chatmanager.ChatManager;
import io.netty.channel.ChannelHandlerContext;
import starbounddata.packets.chat.ChatReceivePacket;
import starbounddata.types.chat.Mode;
import starnubserver.StarNub;
import starnubserver.cache.wrappers.PlayerCtxCacheWrapper;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.events.events.StarNubEvent;
import starnubserver.plugins.resources.PluginConfiguration;
import starnubserver.plugins.resources.PluginYAMLWrapper;
import utilities.strings.StringUtilities;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ChatFilter {

    private final PluginConfiguration CONFIG;
    private final ChatManager CHAT_MANAGER;
    private final PlayerCtxCacheWrapper MESSAGE_CACHE;

    public ChatFilter(PluginConfiguration CONFIG, ChatManager CHAT_MANAGER) {
        this.CONFIG = CONFIG;
        this.CHAT_MANAGER = CHAT_MANAGER;
        this.MESSAGE_CACHE = new PlayerCtxCacheWrapper("ChatManager", "ChatManager - Same Message Cache", true, TimeUnit.SECONDS, 0, 0);
    }

    ///////////     CHAT FILTER     ///////////

    private String filterChat(PlayerSession playerSession, String chatMessage) {
        boolean bypassChatFilter = playerSession.hasPermission("chatman", "bypass", "chat", true);
        if (!bypassChatFilter) {
            chatMessage = wordFilter(playerSession, chatMessage);
            chatMessage = filterRepeatCharacters(playerSession, chatMessage);
            chatMessage = filterChatColor(playerSession, chatMessage);
            chatMessage = filterCapitalization(playerSession, chatMessage);
        }
        return chatMessage;
    }

    private String wordFilter(PlayerSession playerSession, String chatMessage) {
        boolean wordFilter = (boolean) CONFIG.getNestedValue("spam_filter", "word_filter", "enabled");
        if (wordFilter && !playerSession.hasPermission("chatman", "bypass", "word_filter", true)) {
            String replacement = (String) CONFIG.getNestedValue("spam_filter", "word_filter", "replacement");
            PluginYAMLWrapper pluginYamlWrapper = CHAT_MANAGER.getFILES().getPluginYamlWrapper("word_filter_list.yml");
            List<String> blacklist = (List<String>) pluginYamlWrapper.getValue("blacklist");
            for (String filteredWord : blacklist) {
                chatMessage = StringUtilities.exactWordReplacement(chatMessage, filteredWord, replacement);
            }
        }
        return chatMessage;
    }

    private String filterRepeatCharacters(PlayerSession playerSession, String chatMessage) {
        boolean wordFilterRepeatCharacters = (boolean) CONFIG.getNestedValue("spam_filter", "block", "repeat", "characters");
        if (wordFilterRepeatCharacters && !playerSession.hasPermission("chatman", "bypass", "chat_repeat_characters", true)) {
            chatMessage = StringUtilities.removeRepeatingCharacters(chatMessage);
        }
        return chatMessage;
    }

    private String filterChatColor(PlayerSession playerSession, String chatMessage) {
        boolean wordFilterChatColor = (boolean) CONFIG.getNestedValue("spam_filter", "block", "color_chat");
        if (wordFilterChatColor && !playerSession.hasPermission("chatman", "bypass", "chat_color", true)) {
            chatMessage = StringUtilities.removeColors(chatMessage);
        }
        return chatMessage;
    }

    private String filterCapitalization(PlayerSession playerSession, String chatMessage) {
        boolean wordFilterCapitalization = (boolean) CONFIG.getNestedValue("spam_filter", "block", "capitalization", "enabled");
        if (wordFilterCapitalization && !playerSession.hasPermission("chatman", "bypass", "chat_capitalization", true)) {
            double percentageLimit = (double) CONFIG.getNestedValue("spam_filter", "block", "capitalization", "percentage");
            chatMessage = StringUtilities.stringCapitalizationCheckAndChange(chatMessage, percentageLimit);
        }
        return chatMessage;
    }

    private String repeatMessageCheck(PlayerSession playerSession, String chatMessage) {
        boolean wordFilterRepeatMessages = (boolean) CONFIG.getNestedValue("spam_filter", "block", "repeat", "messages");
        if (wordFilterRepeatMessages && !playerSession.hasPermission("chatman", "bypass", "chat_repeat_messages", true)) {
            ChannelHandlerContext clientCtx = playerSession.getCONNECTION().getCLIENT_CTX();
            ChatMessageCache cache = (ChatMessageCache) MESSAGE_CACHE.getCache(clientCtx);
            if (cache != null) {
                boolean isSame = cache.cacheUpdate(chatMessage);
                if (isSame) {
                    chatMessage = null;
                }
            } else {
                MESSAGE_CACHE.addCache(clientCtx, new ChatMessageCache(chatMessage));
            }
        }
        return chatMessage;
    }

    ///////////     NAME FILTER     ///////////

    private void nameVerificationAndChange(PlayerSession playerSession, String nickName) {
        boolean nameRules = (boolean) CONFIG.getNestedValue("name_rules", "enabled");
        if (nameRules && !playerSession.hasPermission("chatman", "bypass", "name", true)) {
            if (nickName == null || nickName.isEmpty()) {
                nickName = playerSession.getNickName();
            }
            String newNickName = nameFilter(playerSession, nickName);
            if (newNickName == null || newNickName.isEmpty()) {
                newNickName = "InvalidNickName";
            }
            String originalNick = playerSession.getNickName();
            if (!newNickName.equalsIgnoreCase(originalNick)) {
                boolean autoNameChange = (boolean) CONFIG.getNestedValue("name_rules", "auto_name_change");
                if (autoNameChange) {
                    playerSession.setNickName(newNickName);
                    new StarNubEvent("Player_Nickname_Changed", playerSession);
                    boolean notifyServerOfNickChange = (boolean) CONFIG.getNestedValue("name_rules", "notify_on_nick_changes");
                    if (notifyServerOfNickChange) {
                        HashSet<ChannelHandlerContext> onlinePlayers = StarNub.getConnections().getCONNECTED_PLAYERS().getOnlinePlayersCtxs();
                        String nickChanged = originalNick + " has changed their Nickname to " + newNickName;
                        String serverName = (String) StarNub.getConfiguration().getNestedValue("starnub_info", "server_name");
                        new ChatReceivePacket(null, Mode.BROADCAST, "ChatManager", 0, serverName, nickChanged);
                    }
                }
            }
        }
    }

    private String nameFilter(PlayerSession playerSession, String name) {
        boolean filterColor = (boolean) CONFIG.getNestedValue("name_rules", "color");
        if (filterColor && !playerSession.hasPermission("chatman", "bypass", "name_color", true)) {
            name = StringUtilities.removeColors(name);
        }
        boolean filterIllegalNicks = (boolean) CONFIG.getNestedValue("name_rules", "use", "illegal_nicks");
        if (filterIllegalNicks && !playerSession.hasPermission("chatman", "bypass", "name_illegal_nicks", true)) {
            PluginYAMLWrapper pluginYamlWrapper = CHAT_MANAGER.getFILES().getPluginYamlWrapper("illegal_nick_names.yml");
            List<String> blacklist = (List<String>) pluginYamlWrapper.getValue("blacklist");
            for (String filteredWord : blacklist) {
                name = StringUtilities.exactWordReplacement(name, filteredWord, "");
            }
        }
        boolean filterNameWordFilter = (boolean) CONFIG.getNestedValue("name_rules", "use", "word_filter");
        if (filterNameWordFilter && !playerSession.hasPermission("chatman", "bypass", "name_word_filter", true)) {
            PluginYAMLWrapper pluginYamlWrapper = CHAT_MANAGER.getFILES().getPluginYamlWrapper("word_filter_list.yml");
            List<String> blacklist = (List<String>) pluginYamlWrapper.getValue("blacklist");
            for (String filteredWord : blacklist) {
                name = StringUtilities.exactWordReplacement(name, filteredWord, "");
            }
        }
        boolean filterMultiSpaces = (boolean) CONFIG.getNestedValue("name_rules", "spaces", "multi_space");
        if (filterMultiSpaces && !playerSession.hasPermission("chatman", "bypass", "name_multi_space", true)) {
            name = StringUtilities.removeRepeatingCharacters(name);
        }
        boolean filterSingleSpaces = (boolean) CONFIG.getNestedValue("name_rules", "spaces", "single");
        if (filterSingleSpaces && !playerSession.hasPermission("chatman", "bypass", "name_single_space", true)) {
            name = StringUtilities.removeSpaces(name);
        }
        boolean filterSpecialCharacters = (boolean) CONFIG.getNestedValue("name_rules", "special_characters");
        if (filterSpecialCharacters && !playerSession.hasPermission("chatman", "bypass", "name_special_characters", true)) {
            name = StringUtilities.removeSpecialCharacters(name);
        }
        int allowedNameLength = (int) CONFIG.getNestedValue("name_rules", "length_cap");
        int nameLen = name.length();
        if (nameLen > allowedNameLength) {
            name = name.substring(0, nameLen);
        }
        return name;
    }
}

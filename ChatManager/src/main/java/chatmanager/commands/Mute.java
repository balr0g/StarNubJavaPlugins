package chatmanager.commands;

import chatmanager.ChatManager;
import chatmanager.chat.ChatSession;
import chatmanager.chat.misc.Muted;
import chatmanager.chat.settings.ChatSetting;
import io.netty.channel.ChannelHandlerContext;
import org.joda.time.DateTime;
import starnubserver.connections.player.account.Account;
import starnubserver.connections.player.character.PlayerCharacter;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.plugins.Command;
import utilities.strings.StringUtilities;
import utilities.time.DateAndTimes;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class Mute extends Command {

    public Mute(String PLUGIN_NAME, HashSet<String> COMMANDS, HashSet<String> MAIN_ARGS, HashMap<String, Integer> CUSTOM_SPLIT, String COMMAND_CLASS, String COMMAND_NAME, int CAN_USE, String DESCRIPTION) {
        super(PLUGIN_NAME, COMMANDS, MAIN_ARGS, CUSTOM_SPLIT, COMMAND_CLASS, COMMAND_NAME, CAN_USE, DESCRIPTION);
    }

    @Override
    public void onCommand(PlayerSession playerSession, String command, String[] args) {
        ChannelHandlerContext clientCTX = playerSession.getCONNECTION().getCLIENT_CTX();
        ChatManager chatManager = (ChatManager) getPLUGIN();

        int argsLength = args.length;
        switch (argsLength) {
            case 0: {
                sendChatMessage(playerSession, "You did not provide enough arguments for the \"" + command + "\" command. Options are info, add, remove.");
                break;
            }
            case 1: {
                String arg1 = args[0];
                switch (arg1) {
                    case "info": {
                        muteInfo(playerSession, chatManager);
                        break;
                    }
                    case "add": {
                        muteAdd(playerSession, chatManager, command, argsLength, args);
                        break;
                    }
                    case "addoffline": {
                        muteAdd(playerSession, chatManager, command, argsLength, args);
                        break;
                    }
                    case "remove": {
                        muteRemove(playerSession, chatManager, argsLength, args);
                        break;
                    }
                    case "example": {
                        muteExamples(playerSession);
                        break;
                    }
                }
                break;
            }
        }
    }

    private void sendChatMessage(PlayerSession playerSession, String chatMessage) {
        playerSession.sendBroadcastMessageToClient("ChatManager", chatMessage);
    }

    private void muteInfo(PlayerSession playerSession, ChatManager chatManager) {
        ChannelHandlerContext clientCtx = playerSession.getCONNECTION().getCLIENT_CTX();
        ChatSession chatSession = chatManager.getPlayerManager().getCONNECTED_PLAYERS().get(clientCtx);
        ChatSetting chatSetting = chatSession.getCHAT_SETTING();
        Muted muted = chatSetting.getMuted();
        if (muted == null) {
            sendChatMessage(playerSession, "You are not currently mute.");
        } else {
            DateTime muteExpireDateTime = muted.getMuteExpire();
            if (muteExpireDateTime == null) {
                sendChatMessage(playerSession, "You are permanently muted.");
            } else {
                long period = muteExpireDateTime.getMillis() - DateTime.now().getMillis();
                String formattedFromPeriod = DateAndTimes.getPeriodFormattedFromMilliseconds(period, "-", "-", "-", "-", "-", "-", "-");
                String dateTimeString = DateAndTimes.getFormattedDate("MMMM dd @ h:m a, yyyy", muteExpireDateTime);
                sendChatMessage(playerSession, "You are currently muted until " + dateTimeString + ". You have " + formattedFromPeriod +
                        " remaining.");
            }
        }
    }

    private void muteAdd(PlayerSession playerSession, ChatManager chatManager, String command, int argsLength, String[] args) {
        DateTime dateTime = null;
        boolean muteSuccess = false;
        if (argsLength == 1) {
            sendChatMessage(playerSession, "To mute someone you must supply arguments \"/mute add {playerId} {length} {reason}\"");
            return;
        } else if (argsLength == 2) {
            dateTime = null;
        } else if (argsLength == 3) {
            dateTime = DateAndTimes.parseFutureTime(args[1]);
        }
        String playerId = args[0];
        String reason = args[2];
        PlayerSession toBeMutedSession;
        Account staffAccount = playerSession.getPlayerCharacter().getAccount();
        if (staffAccount == null) {
            sendChatMessage(playerSession, "You cannot mute someone without having a StarNub account.");
            return;
        }
        if (command.equals("add")) {
            toBeMutedSession = PlayerSession.getSession(playerId);
            if (toBeMutedSession == null) {
                sendChatMessage(playerSession, "The player you are trying to mute is not currently online. Here are the recent Player Sessions for the last 15 minutes that match your identifier: ");
                DateTime dateTimeSearch = DateTime.now();
                dateTimeSearch.minusMinutes(15);
                HashSet<PlayerSession> playerSessionsList = PlayerSession.getRecentSessionsByIdentifier(playerId, dateTimeSearch);
                String toSend = "(Session ID) | (StarNubID) | (CharacterName): ";
                for (PlayerSession playerSessionSearch : playerSessionsList) {
                    PlayerCharacter playerCharacter = playerSessionSearch.getPlayerCharacter();
                    Account account = playerCharacter.getAccount();
                    int starnubId = 0;
                    if (account != null) {
                        starnubId = account.getStarnubId();
                    }
                    List<PlayerCharacter> characterByName = playerCharacter.getCharacterByName();
                    int sessionID = playerSessionSearch.getSessionID();
                    toSend = toSend + sessionID + " | " + starnubId + " | " + characterByName + ", ";
                }
                String trimmedString = StringUtilities.trimCommaForPeriod(toSend);
                sendChatMessage(playerSession, trimmedString);
                sendChatMessage(playerSession, "You can reuse the mute command for the offline player by using /mute addoffline {sessionId} {length} {reason}");
            } else {
                ChannelHandlerContext toBeMutedCtx = toBeMutedSession.getCONNECTION().getCLIENT_CTX();
                ChatSession chatSession = chatManager.getPlayerManager().getCONNECTED_PLAYERS().get(toBeMutedCtx);
                ChatSetting chatSetting = chatSession.getCHAT_SETTING();
                chatSetting.mute(staffAccount, dateTime, reason);
                muteSuccess = true;
            }
        } else {
            int sessionId = Integer.parseInt(playerId);
            toBeMutedSession = PlayerSession.getSpecificSession(sessionId);
            Account toBeMutedAccount = toBeMutedSession.getPlayerCharacter().getAccount();
            if (toBeMutedAccount != null) {
                ChatSetting chatSetting = ChatSetting.getByStarnubId(toBeMutedAccount);
                if (chatSetting != null) {
                    chatSetting.mute(staffAccount, dateTime, reason);
                    muteSuccess = true;
                }
            }
            if (!muteSuccess) {
                UUID toBeMutedUuid = toBeMutedSession.getPlayerCharacter().getUuid();
                ChatSetting chatSetting = ChatSetting.getByUuid(toBeMutedUuid);
                if (chatSetting == null) {
                    sendChatMessage(playerSession, "Something went critically wrong while trying to mute a offline player. Player Session Id: " + sessionId + ".");
                    return;
                } else {
                    chatSetting.mute(staffAccount, dateTime, reason);
                }
            }
        }
        if (muteSuccess) {
            String mutedName = playerSession.getPlayerCharacter().getName();
            String unMuteDate;
            if (dateTime == null) {
                unMuteDate = "is permanent.";
            } else {
                unMuteDate = "will end on " + DateAndTimes.getFormattedDate("MMMM dd @ h:m a, yyyy", dateTime) + ".";
            }
            sendChatMessage(playerSession, "You have successfully muted " + mutedName + ". The mute " + unMuteDate);
        }
    }

    private void muteRemove(PlayerSession playerSession, ChatManager chatManager, int argsLength, String[] args) {


    }

    private void muteExamples(PlayerSession playerSession) {
        sendChatMessage(playerSession, "To mute someone you must supply arguments \"/mute add {playerId} {length} {reason}\"");
        sendChatMessage(playerSession, "Example 1 (Temporary): /mute add 5 1y2m3w4d5h6min \"For spamming profanity\"");
        sendChatMessage(playerSession, "Example 2 (Temporary): /mute add {player-name} 24h30min \"For spamming profanity\"");
        sendChatMessage(playerSession, "Example 3 (Permanent): /mute add {player-name} \"For spamming profanity\"");
    }
}

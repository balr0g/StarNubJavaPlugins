package org.starnub.plugins;

import io.netty.channel.ChannelHandlerContext;
import org.starnub.starbounddata.types.color.Colors;
import org.starnub.starnubserver.StarNub;
import org.starnub.starnubserver.StarNubTask;
import org.starnub.starnubserver.cache.wrappers.PlayerAutoCancelTask;
import org.starnub.starnubserver.cache.wrappers.PlayerCtxCacheWrapper;
import org.starnub.starnubserver.connections.player.session.PlayerSession;
import org.starnub.starnubserver.events.events.DisconnectData;
import org.starnub.starnubserver.pluggable.Plugin;
import org.starnub.starnubserver.resources.StringTokens;
import org.starnub.starnubserver.resources.predicates.PSPredicates;
import org.starnub.utilities.events.Priority;
import org.starnub.utilities.events.types.ObjectEvent;
import org.starnub.utilities.strings.StringUtilities;

import java.util.concurrent.TimeUnit;

public class JoinLeaveBroadcast extends Plugin {

    private PlayerCtxCacheWrapper UNSUBSCRIBED_JOIN_LEAVE;
    private PlayerAutoCancelTask JOIN_TASK;

    public PlayerCtxCacheWrapper getUNSUBSCRIBED_JOIN_LEAVE() {
        return UNSUBSCRIBED_JOIN_LEAVE;
    }

    @Override
    public void onEnable() {
        this.UNSUBSCRIBED_JOIN_LEAVE = new PlayerCtxCacheWrapper(getRegistrationName(), "Essentials - Unsubscribe - Join - Leave", true, TimeUnit.SECONDS, 0, 0);
        this.JOIN_TASK = new PlayerAutoCancelTask(getRegistrationName(), "Join Messages");
        boolean leaveJoinBoolean = (boolean) getConfiguration().getNestedValue("enabled");
        if (leaveJoinBoolean) {
            newStarNubEventSubscription(Priority.MEDIUM, "Player_Connected", this::playerConnected);
            newStarNubEventSubscription(Priority.MEDIUM, "Player_Disconnected", this::playerDisconnected);
        }
    }


    @Override
    public void onDisable() {

    }

    private void playerConnected(ObjectEvent objectEvent) {
        PlayerSession playerSession = (PlayerSession) objectEvent.getEVENT_DATA();
        ChannelHandlerContext clientCtx = playerSession.getCONNECTION().getCLIENT_CTX();
        String playerNameConsole = playerSession.getCleanNickName();
        String playerName = playerSession.getGameName();
        boolean nameColor = (boolean) getConfiguration().getNestedValue("name_color");
        if (!nameColor) {
            playerName = StringUtilities.removeColors(playerName);
        }
        int delay = (int) getConfiguration().getNestedValue("delay");
        String colorUnvalidated = (String) getConfiguration().getNestedValue("message", "color");
        String chatColor = Colors.validate(colorUnvalidated);
        String unformattedMessage = (String) getConfiguration().getNestedValue("message", "connect");
        String formattedMessage = String.format(unformattedMessage, playerName + chatColor);
        String completeMessage = StringTokens.replaceTokens(formattedMessage);
        StarNubTask playerJoinTask = newStarNubTask("JoinLeave - Send - Player - " + playerNameConsole + " - Has Connected Message", delay, TimeUnit.SECONDS, () -> {
            PlayerSession.sendChatBroadcastToClientsAllFiltered(
                    "ServerName",
                    chatColor + completeMessage,
                    PSPredicates.multiFilter(
                            PSPredicates.filterOutSpecificPlayer(playerSession),
                            PSPredicates.isCtxNotListed(UNSUBSCRIBED_JOIN_LEAVE.getCACHE_MAP()),
                            PSPredicates.doesNotHavePermission("starnub", "joinleave", "ignore", false))
            );
            StarNub.getLogger().cInfoPrint("JoinLeave", playerNameConsole + " has connected. (IP: " + playerSession.getSessionIpString() + ")");
        });
        JOIN_TASK.registerTask(clientCtx, playerJoinTask);
    }

    private void playerDisconnected(ObjectEvent objectEvent) {
        DisconnectData disconnectData = (DisconnectData) objectEvent.getEVENT_DATA();
        PlayerSession playerSession = disconnectData.getPLAYER_SESSION();
        ChannelHandlerContext clientCtx = playerSession.getCONNECTION().getCLIENT_CTX();
        String playerNameConsole = playerSession.getCleanNickName();
        String playerName = playerSession.getGameName();
        boolean recentlyCanceled = JOIN_TASK.recentlyCanceled(clientCtx);
        if (!recentlyCanceled) {
            boolean nameColor = (boolean) getConfiguration().getNestedValue("name_color");
            if (!nameColor) {
                playerName = StringUtilities.removeColors(playerName);
            }
            String colorUnvalidated = (String) getConfiguration().getNestedValue("message", "color");
            String chatColor = Colors.validate(colorUnvalidated);
            String unformattedMessage = (String) getConfiguration().getNestedValue("message", "disconnect");
            String formattedMessage = String.format(unformattedMessage, playerName + chatColor);
            String completeMessage = StringTokens.replaceTokens(formattedMessage);
            PlayerSession.sendChatBroadcastToClientsAllFiltered(
                    "ServerName",
                    chatColor + completeMessage,
                    PSPredicates.multiFilter(
                            PSPredicates.filterOutSpecificPlayer(playerSession),
                            PSPredicates.isCtxNotListed(UNSUBSCRIBED_JOIN_LEAVE.getCACHE_MAP()),
                            PSPredicates.doesNotHavePermission("starnub", "joinleave", "ignore", false))
            );
            StarNub.getLogger().cInfoPrint("JoinLeave", playerNameConsole + " has disconnected. (IP: " + playerSession.getSessionIpString() + ")");
        }
    }
}

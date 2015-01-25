package org.starnub.plugins;

import io.netty.channel.ChannelHandlerContext;
import org.starnub.starbounddata.types.color.Colors;
import org.starnub.starnubserver.StarNubTask;
import org.starnub.starnubserver.cache.wrappers.PlayerAutoCancelTask;
import org.starnub.starnubserver.connections.player.session.PlayerSession;
import org.starnub.starnubserver.pluggable.Plugin;
import org.starnub.starnubserver.resources.StringTokens;
import org.starnub.utilities.events.Priority;
import org.starnub.utilities.numbers.RandomNumber;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Motd extends Plugin {

    private PlayerAutoCancelTask AUTO_CANCEL_MOTD_TASK;
    private volatile int rotatingIndex = 0;

    @Override
    public void onEnable() {
        this.AUTO_CANCEL_MOTD_TASK = new PlayerAutoCancelTask(getRegistrationName(), "MOTD");
    }

    @Override
    public void onDisable() {
        /* No clean up required, since StarNub will deregister our events for us */
    }

    @Override
    public void onRegister() {
        newStarNubEventSubscription(Priority.MEDIUM, "Player_Connected", objectEvent -> {
            PlayerSession playerSession = (PlayerSession) objectEvent.getEVENT_DATA();
            boolean doNotSend = playerSession.hasPermission("starnub", "motd", "ignore", false);
            if (!doNotSend) {
                String type1 = (String) getConfiguration().getValue("type");
                String color = Colors.validate((String) getConfiguration().getValue("color"));
                List<String> messages = (List<String>) getConfiguration().getValue("messages");
                if (messages.size() > 0) {
                    String stringMotd = "";
                    if (type1.equalsIgnoreCase("static")) {
                        stringMotd = messages.get(0);
                    } else if (type1.equalsIgnoreCase("rotating")) {
                        stringMotd = messages.get(rotatingIndex);
                        rotatingIndex++;
                        if (rotatingIndex >= messages.size()) {
                            rotatingIndex = 0;
                        }
                    } else if (type1.equalsIgnoreCase("random")) {
                        int randomInt = RandomNumber.randInt(0, messages.size() - 1);
                        stringMotd = messages.get(randomInt);
                    }
                    String replacedTokensMotd = StringTokens.replaceTokens(stringMotd);
                    playerMessage(playerSession, color + replacedTokensMotd);
                }
            }
        });
    }

    private void playerMessage(PlayerSession playerSession, String motd) {
        ChannelHandlerContext clientCtx = playerSession.getCONNECTION().getCLIENT_CTX();
        String cleanName = playerSession.getPlayerCharacter().getCleanName();
        StarNubTask motdTask = newStarNubTask("StarNub - Player MOTD - " + cleanName, 5, TimeUnit.SECONDS, () -> playerSession.sendBroadcastMessageToClient("ServerName", motd));
        AUTO_CANCEL_MOTD_TASK.registerTask(clientCtx, motdTask);
    }
}

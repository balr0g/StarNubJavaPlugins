package org.starnub.plugins.motd;

import io.netty.channel.ChannelHandlerContext;
import org.starnub.starbounddata.types.color.Colors;
import org.starnub.starnubserver.StarNubTask;
import org.starnub.starnubserver.cache.wrappers.PlayerAutoCancelTask;
import org.starnub.starnubserver.connections.player.session.PlayerSession;
import org.starnub.starnubserver.events.starnub.StarNubEventHandler;
import org.starnub.starnubserver.pluggable.Plugin;
import org.starnub.starnubserver.resources.StringTokens;
import org.starnub.utilities.events.Priority;
import org.starnub.utilities.events.types.ObjectEvent;
import org.starnub.utilities.numbers.RandomNumber;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Motd extends Plugin {

    private final PlayerAutoCancelTask MOTD_TASK = new PlayerAutoCancelTask("Essentials", "MOTD");
    private volatile int rotatingIndex = 0;

    @Override
    public void onPluginEnable() {
        /* Do not need to do anything since the Plugin Manager will call register for us and submit our event listener */
    }

    @Override
    public void onPluginDisable() {
        /* No clean up required, since StarNub will deregister our events for us */
    }

    @Override
    public void register() {
        super.newStarNubEventSubscription(Priority.MEDIUM, "Player_Connected", new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent objectEvent) {
                PlayerSession playerSession = (PlayerSession) objectEvent.getEVENT_DATA();
                boolean doNotSend = playerSession.hasPermission("starnub", "motd", "ignore", true);
                if (!doNotSend) {
                    String type = (String) configuration.getNestedValue("type");
                    String color = Colors.validate((String) configuration.getNestedValue("color"));
                    List<String> messages = (List<String>) configuration.getNestedValue("messages");
                    if (messages.size() > 0) {
                        String stringMotd = "";
                        if (type.equalsIgnoreCase("static")) {
                            stringMotd = messages.get(0);
                        } else if (type.equalsIgnoreCase("rotating")) {
                            stringMotd = messages.get(rotatingIndex);
                            rotatingIndex++;
                            if (rotatingIndex >= messages.size()) {
                                rotatingIndex = 0;
                            }
                        } else if (type.equalsIgnoreCase("random")) {
                            int randomInt = RandomNumber.randInt(0, messages.size() - 1);
                            stringMotd = messages.get(randomInt);
                        }
                        String replacedTokensMotd = StringTokens.replaceTokens(stringMotd);
                        playerMessage(playerSession, color + replacedTokensMotd);
                    }
                }
            }
        });
    }

    private void playerMessage(PlayerSession playerSession, String motd) {
        ChannelHandlerContext clientCtx = playerSession.getCONNECTION().getCLIENT_CTX();
        String cleanName = playerSession.getPlayerCharacter().getCleanName();
        StarNubTask motdTask = new StarNubTask("Essentials", "StarNub - Player MOTD - " + cleanName, 5, TimeUnit.SECONDS, () -> playerSession.sendBroadcastMessageToClient("ServerName", motd));
        MOTD_TASK.registerTask(clientCtx, motdTask);
    }
}

package org.starnub.essentials.classes;

import io.netty.channel.ChannelHandlerContext;
import org.starnub.starbounddata.types.color.Colors;
import org.starnub.starnubserver.StarNubTask;
import org.starnub.starnubserver.cache.wrappers.PlayerAutoCancelTask;
import org.starnub.starnubserver.connections.player.session.PlayerSession;
import org.starnub.starnubserver.events.starnub.StarNubEventHandler;
import org.starnub.starnubserver.events.starnub.StarNubEventSubscription;
import org.starnub.starnubserver.plugins.resources.PluginConfiguration;
import org.starnub.starnubserver.resources.StringTokens;
import org.starnub.utilities.events.Priority;
import org.starnub.utilities.events.types.ObjectEvent;
import org.starnub.utilities.numbers.RandomNumber;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class Motd {

    private final PluginConfiguration CONFIG;
    private final StarNubEventSubscription PLAYER_CONNECTED;
    private final PlayerAutoCancelTask MOTD_TASK;
    private volatile int rotatingIndex = 0;

    public Motd(PluginConfiguration CONFIG) {
        this.CONFIG = CONFIG;
        boolean isMotd = (boolean) CONFIG.getNestedValue("motd", "enabled");
        if (isMotd) {
            PLAYER_CONNECTED = setMotdMessage();
            MOTD_TASK = new PlayerAutoCancelTask("Essentials", "MOTD");
        } else {
            PLAYER_CONNECTED = null;
            MOTD_TASK = null;
        }

    }

    private StarNubEventSubscription setMotdMessage() {
        return new StarNubEventSubscription("Essentials", Priority.MEDIUM, "Player_Connected", new StarNubEventHandler() {
            @Override
            public void onEvent(ObjectEvent starNubEvent) {
                PlayerSession playerSession = (PlayerSession) starNubEvent.getEVENT_DATA();
                String type = (String) CONFIG.getNestedValue("motd", "type");
                String color = Colors.validate((String) CONFIG.getNestedValue("motd", "color"));
                List<String> messages = (List<String>) CONFIG.getNestedValue("motd", "messages");
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
        });
    }


    private void playerMessage(PlayerSession playerSession, String motd) {
        ChannelHandlerContext clientCtx = playerSession.getCONNECTION().getCLIENT_CTX();
        String cleanName = playerSession.getPlayerCharacter().getCleanName();
        StarNubTask motdTask = new StarNubTask("Essentials", "Essentials - Player MOTD - " + cleanName, 5, TimeUnit.SECONDS, () -> playerSession.sendBroadcastMessageToClient("ServerName", motd));
        MOTD_TASK.registerTask(clientCtx, motdTask);
    }

    public void unregisterEventsTask() {
        if (PLAYER_CONNECTED != null) {
            PLAYER_CONNECTED.removeRegistration();
        }
    }
}

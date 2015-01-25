package org.starnub.starboundmanager.commands;

import org.starnub.starboundmanager.Essentials;
import org.starnub.starboundmanager.classes.UptimeTracker;
import org.starnub.starnubserver.connections.player.session.PlayerSession;
import org.starnub.starnubserver.plugins.Command;

import java.util.HashMap;
import java.util.HashSet;

public class Uptime extends Command {

    public Uptime(String PLUGIN_NAME, HashSet<String> COMMANDS, HashSet<String> MAIN_ARGS, HashMap<String, Integer> CUSTOM_SPLIT, String COMMAND_CLASS, String COMMAND_NAME, int CAN_USE, String DESCRIPTION) {
        super(PLUGIN_NAME, COMMANDS, MAIN_ARGS, CUSTOM_SPLIT, COMMAND_CLASS, COMMAND_NAME, CAN_USE, DESCRIPTION);
    }

    @Override
    public void onCommand(PlayerSession playerSession, String command, String[] args) {
        int argsLength = args.length;
        if (argsLength == 0) {
            playerSession.sendBroadcastMessageToClient("Essentials", "You did not supply enough arguments. /uptime {starbound} or /uptime starnub}");
        } else {
            Essentials essentialsPlugin = (Essentials) getPLUGIN();
            UptimeTracker uptimeTracker = essentialsPlugin.getUptimeTracker();
            String arg = args[0];
            long uptime = 0L;
            if (arg.equals("starbound")) {
                uptime = uptimeTracker.getStarboundUptime();
            } else if (arg.equals("starnub")) {
                uptime = uptimeTracker.getStarnubUptime();
            }
//            String formattedTime = DateAndTimes.getPeriodFormattedFromMilliseconds(uptime, "-", "-", "-", "-", "-", "-", "-");
        }
    }
}

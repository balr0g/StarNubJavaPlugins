package starnubcommands.commands.player;

import org.starnub.StarNub;
import org.starnub.plugins.Command;

public class Ping extends Command {

    @Override
    public void onCommand(Object sender, String command, String[] args) {
        StarNub.getMessageSender().playerMessage("StarNub", sender, "Pong.");
    }
}

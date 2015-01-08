package org.starnub.starnubcommands.commands.admin;

import org.starnub.StarNub;
import org.starnub.plugins.Command;

public class ReloadShortcuts extends Command {

    @Override
    public void onCommand(Object sender, String command, String[] args) {
        StarNub.getCommandSender().setStarboundCommands();
    }

}

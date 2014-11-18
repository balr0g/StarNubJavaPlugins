package starboundcommands.commands.debug;

import org.starnub.connectedentities.player.session.Player;
import org.starnub.plugins.Command;

import static starboundcommands.CommandForward.forward;

public class ToggleLayer extends Command {

    @Override
    public void onCommand(Object sender, String command, String[] args) {
        forward((Player) sender, command, args);
    }
}

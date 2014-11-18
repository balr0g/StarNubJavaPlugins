package starboundcommands;

import org.starnub.StarNub;
import org.starnub.connectedentities.player.session.Player;
import org.starnub.server.datatypes.chat.ChatSendChannel;
import org.starnub.server.packets.chat.ChatSendPacket;

public class CommandForward {

    public static void forward(Player sender, String command, String[] args){
         /* This command will be forwarded to the server */
        StarNub.getPacketSender().serverPacketSender(
                (Player) sender,
                new ChatSendPacket(
                        ChatSendChannel.UNIVERSE,
                        StarNub.getCommandSender().starboundCommandRebuilder(command, args)
                ));
    }
}

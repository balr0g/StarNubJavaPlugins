package chatmanager.commands;

import chatmanager.ChatManager;
import io.netty.channel.ChannelHandlerContext;
import org.joda.time.DateTime;
import starbounddata.packets.chat.ChatReceivePacket;
import starbounddata.types.chat.Mode;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.plugins.Command;
import utilities.time.DateAndTimes;

import java.util.HashMap;
import java.util.HashSet;

public class Mute extends Command {

    public Mute(String PLUGIN_NAME, HashSet<String> COMMANDS, HashSet<String> MAIN_ARGS, HashMap<String, Integer> CUSTOM_SPLIT, String COMMAND_CLASS, String COMMAND_NAME, int CAN_USE, String DESCRIPTION) {
        super(PLUGIN_NAME, COMMANDS, MAIN_ARGS, CUSTOM_SPLIT, COMMAND_CLASS, COMMAND_NAME, CAN_USE, DESCRIPTION);
    }

    @Override
    public void onCommand(PlayerSession playerSession, String command, String[] args) {
        ChannelHandlerContext clientCXT = playerSession.getCONNECTION().getCLIENT_CTX();
        ChatManager chatManager = (ChatManager) getPLUGIN();

        int argsLength = args.length;
        switch (argsLength) {
            case 0: {
                sendChatMessage(clientCXT, "You did not provide enough arguments for the \"" + command + "\" command. Options are info, add, remove.");
                break;
            }
            case 1: {
                String arg1 = args[0];
                switch (arg1) {
                    case "info": {

                        break;
                    }
                    case "add": {
                        DateTime dateTime;
                        if (argsLength == 1) {
                            sendChatMessage(clientCXT, "To mute someone you must supply arguments \"/mute add {playerId} \"");
                            return;
                        } else if (argsLength == 2) {
                            dateTime = null;
                        } else if (argsLength == 3) {
                            dateTime = DateAndTimes.
                        }
                        //add
                        break;
                    }
                    case "remove": {

                        break;
                    }
                }
                break;
            }
        }
        //"MMMM dd, yyyy"
    }

    private void sendChatMessage(ChannelHandlerContext clientCTX, String chatMessage) {
        new ChatReceivePacket(clientCTX, Mode.BROADCAST, "ChatManager", 0, "ChatManager", chatMessage).routeToDestination();
    }
}

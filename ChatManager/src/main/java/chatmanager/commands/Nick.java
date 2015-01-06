package chatmanager.commands;

import chatmanager.ChatManager;
import io.netty.channel.ChannelHandlerContext;
import starbounddata.packets.chat.ChatReceivePacket;
import starbounddata.types.chat.Mode;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.plugins.Command;

import java.util.HashMap;
import java.util.HashSet;

public class Nick extends Command {

    public Nick(String PLUGIN_NAME, HashSet<String> COMMANDS, HashSet<String> MAIN_ARGS, HashMap<String, Integer> CUSTOM_SPLIT, String COMMAND_CLASS, String COMMAND_NAME, int CAN_USE, String DESCRIPTION) {
        super(PLUGIN_NAME, COMMANDS, MAIN_ARGS, CUSTOM_SPLIT, COMMAND_CLASS, COMMAND_NAME, CAN_USE, DESCRIPTION);
    }

    @Override
    public void onCommand(PlayerSession playerSession, String command, String[] args) {
        ChatManager chatManager = (ChatManager) getPLUGIN();
        ChannelHandlerContext clientCTX = playerSession.getCONNECTION().getCLIENT_CTX();
        int length = args.length;
        switch (length) {
            case 0: {
                sendChatMessage(clientCTX, "You did not provide a new nick name to change to.");
                break;
            }
            case 1: {
                chatManager.getChatFilter().nameVerificationAndChange(playerSession, args[0]);
                break;
            }
        }
    }

    private void sendChatMessage(ChannelHandlerContext clientCTX, String chatMessage) {
        new ChatReceivePacket(clientCTX, Mode.BROADCAST, "ChatManager", 0, "ChatManager", chatMessage).routeToDestination();
    }
}

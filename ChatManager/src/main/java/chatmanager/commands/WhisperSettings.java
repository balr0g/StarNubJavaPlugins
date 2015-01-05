package chatmanager.commands;

import chatmanager.ChatManager;
import chatmanager.PlayerManager;
import chatmanager.chat.ChatSession;
import io.netty.channel.ChannelHandlerContext;
import starbounddata.packets.chat.ChatReceivePacket;
import starbounddata.types.chat.Mode;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.plugins.Command;

import java.util.HashMap;
import java.util.HashSet;

public class WhisperSettings extends Command {


    public WhisperSettings(String PLUGIN_NAME, HashSet<String> COMMANDS, HashSet<String> MAIN_ARGS, HashMap<String, Integer> CUSTOM_SPLIT, String COMMAND_CLASS, String COMMAND_NAME, int CAN_USE, String DESCRIPTION) {
        super(PLUGIN_NAME, COMMANDS, MAIN_ARGS, CUSTOM_SPLIT, COMMAND_CLASS, COMMAND_NAME, CAN_USE, DESCRIPTION);
    }

    @Override
    public void onCommand(PlayerSession playerSession, String command, String[] args) {
        ChatManager CHAT_MANAGER = (ChatManager) getPLUGIN();
        PlayerManager PLAYER_MANAGER = CHAT_MANAGER.getPlayerManager();
        ChannelHandlerContext clientCTX = playerSession.getCONNECTION().getCLIENT_CTX();
        ChatSession chatSession = PLAYER_MANAGER.getCONNECTED_PLAYERS().get(clientCTX);
        switch (command) {
            case "whisperon": {
                chatSession.getCHAT_SETTINGS().receievWhispers();
                sendChatMessage(clientCTX, "Whispering Enabled: You will now receive whispers.");
                break;
            }
            case "whisperoff": {
                chatSession.getCHAT_SETTINGS().ignoreWhispers();
                sendChatMessage(clientCTX, "Whispering Disabled: You will no longer received whispers.");
                break;
            }
        }
    }

    private void sendChatMessage(ChannelHandlerContext clientCTX, String chatMessage) {
        new ChatReceivePacket(clientCTX, Mode.BROADCAST, "ChatManager", 0, "ChatManager", chatMessage).routeToDestination();
    }
}

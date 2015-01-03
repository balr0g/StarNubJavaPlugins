package starboundcommands.commands;


import starbounddata.types.chat.ChatSendMode;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.plugins.Command;

import java.util.HashMap;
import java.util.HashSet;

public class CommandBridge extends Command {

    public CommandBridge(String PLUGIN_NAME, HashSet<String> COMMANDS, HashSet<String> MAIN_ARGS, HashMap<String, Integer> CUSTOM_SPLIT, String COMMAND_CLASS, String COMMAND_NAME, int CAN_USE, String DESCRIPTION) {
        super(PLUGIN_NAME, COMMANDS, MAIN_ARGS, CUSTOM_SPLIT, COMMAND_CLASS, COMMAND_NAME, CAN_USE, DESCRIPTION);
    }

    @Override
    public void onCommand(PlayerSession playerSession, String command, String[] args) {
        String commandRebuild = "/" + command;
        for (String arg : args) {
            commandRebuild = commandRebuild + " " + arg;
        }
        playerSession.sendServerChatMessage(ChatSendMode.BROADCAST, commandRebuild);
    }
}
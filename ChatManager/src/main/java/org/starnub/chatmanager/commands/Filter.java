package org.starnub.chatmanager.commands;

import org.starnub.starnubserver.connections.player.session.PlayerSession;
import org.starnub.starnubserver.plugins.Command;

import java.util.HashMap;
import java.util.HashSet;

public class Filter extends Command {

    public Filter(String PLUGIN_NAME, HashSet<String> COMMANDS, HashSet<String> MAIN_ARGS, HashMap<String, Integer> CUSTOM_SPLIT, String COMMAND_CLASS, String COMMAND_NAME, int CAN_USE, String DESCRIPTION) {
        super(PLUGIN_NAME, COMMANDS, MAIN_ARGS, CUSTOM_SPLIT, COMMAND_CLASS, COMMAND_NAME, CAN_USE, DESCRIPTION);
    }

    @Override
    public void onCommand(PlayerSession playerSession, String command, String[] args) {

    }
}

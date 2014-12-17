package commandparser.commands;

import starnubserver.plugins.Command;

import java.util.HashMap;
import java.util.HashSet;

public class CommandParserCommands extends Command {

    public CommandParserCommands(HashSet<String> COMMANDS, HashSet<String> MAIN_ARGS, HashMap<String, Integer> CUSTOM_SPLIT, String COMMAND_CLASS, String COMMAND_NAME, int CAN_USE, String DESCRIPTION) {
        super(COMMANDS, MAIN_ARGS, CUSTOM_SPLIT, COMMAND_CLASS, COMMAND_NAME, CAN_USE, DESCRIPTION);
    }

    @Override
    public void onCommand(Object sender, String command, String[] args) {

    }
}

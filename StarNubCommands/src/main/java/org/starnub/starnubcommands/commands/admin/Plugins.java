package org.starnub.starnubcommands.commands.admin;

import org.starnub.StarNub;
import org.starnub.plugins.Command;

public class Plugins extends Command {

    @Override
    public void onCommand(Object sender, String command, String[] args) {
        switch (args[0]) {
            case "update" :
                StarNub.getPluginManager().updatePlugin(sender, args[1]);
                break;
            case "loaded" :
                StarNub.getPluginManager().getLoadedPluginsPrint(sender);
                break;
        }




//see loaded plugins
    }
//['load', 'unload', 'enable', 'disable', 'update']




    private void updatePlugin (String pluginName) {



    }


}

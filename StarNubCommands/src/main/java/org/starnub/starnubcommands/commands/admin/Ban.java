package org.starnub.starnubcommands.commands.admin;

import org.starnub.plugins.Command;

import java.util.Arrays;

public class Ban extends Command {

    @Override
    public void onCommand(Object sender, String command, String[] args) {
//        Object test = null;
//        if (args.length == 1) {
//            test = StarNub.getServer().getConnections().getOfflinePlayerByName((Sender) sender, args[0]);
//        } else if (args.length == 2) {
//            test = StarNub.getServer().getConnections().getOfflinePlayerByIdCached((Sender) sender, args[0], Integer.parseInt(args[1]));
//        }
//
//        if (test instanceof PlayerCharacter) {
//            System.out.println(((PlayerCharacter) test).getCharacterId());
//        } else if (test instanceof String) {
//            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, (String) test);
//        } else if (test == null) {
//            StarNub.getMessageSender().playerOrConsoleMessage("StarNub", sender, "Error banning player.");
//        }
//



        System.out.println("Plugin, I got some stuff!: \nSender: "+ sender);
        System.out.println("\n Command: "+command);
        System.out.println("\n Strings: "+ Arrays.toString(args));

//./ban {starnubid} -current -uuid, /ban {starnubid} -current -ip, /ban {starnubid} -all -ip, /ban {starnubid} -all -uuid, /ban {starnubid} -all -all

        // /ban {ip/uuid/character} for offline

        // ONLINE OFFLINE BANNING SEPARATION

        // ONLINE requires a Starnub or starbound id
        // OFFLINE ip or uuid, or clean character name, if multiple names, cach results with last time off - this requires a new class to be created
        // it will also require a hashmap to store the database results in with that class. the sender will be marked in the class so that we know
        // if multiple people use commands that we know which ban results belong to which person that used the command.

        //Use of uuid, ip, will result in only that banned unless booleans set, else starnubid = current session, or switch for all char or ip

    }
}

//TODO online commad cache, also nick name consideration of _1
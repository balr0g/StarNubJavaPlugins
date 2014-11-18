package starnubcommands.commands.player;

import org.starnub.StarNub;
import org.starnub.connectedentities.player.session.Player;
import org.starnub.plugins.Command;

public class Chat extends Command{



    @Override
    public void onCommand(Object sender, String command, String[] args) {
        Player player = StarNub.getServer().getConnections().getOnlinePlayerByAnyIdentifier(sender);
        System.out.println(command);

        String mainArgument = args[0].toLowerCase();
        switch (mainArgument) {
            case "join": break;
            case "create": break;
            case "leave": break;
            case "info": break;
            case "delete": break;
            case "kick": break;
            case "subscribe": break;
            case "unsubscribe": break;
            case "set": {
                String subArgument = args[1].toLowerCase();
                switch (subArgument) {
                    case "name": break;
                    case "namecolor": break;
                    case "chatcolor": break;
                    case "password": break;
                    case "info": break;
                    case "owner": break;
                }
                break;
            }
        }
    }

//    private boolean hasPermission(Player player, String permission, String permission2){
//        ArrayList<String> permissions = player.getCharacter().getAccount().getPermission("starnub", "chatrooms", "chat");
//
//
//    }

}

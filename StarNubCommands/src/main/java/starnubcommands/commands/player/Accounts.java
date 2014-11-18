package starnubcommands.commands.player;

import org.starnub.StarNub;
import org.starnub.connectedentities.player.account.Account;
import org.starnub.connectedentities.player.session.Player;
import org.starnub.eventsrouter.events.PlayerEvent;
import org.starnub.eventsrouter.events.StarNubEvent;
import org.starnub.eventsrouter.handlers.StarNubEventHandler;
import org.starnub.plugins.Command;

import java.util.Random;
import java.util.concurrent.TimeUnit;



public class Accounts extends Command {

    public Accounts(){
        StarNub.getStarNubEventRouter().registerEventSubscription("StarNubCommands", "Player_Connection_Success", new StarNubEventHandler() {
                @Override
                public void onEvent(StarNubEvent<String> starNubEvent) {
                    Player player = ((PlayerEvent) starNubEvent).getPLAYER_SESSION();
                    if (player.getCharacter().getAccount() == null) {
                        StarNub.getTask().getTaskScheduler().scheduleOneTimeTask("StarNubCommands", "StarnubCommands - Send - Player - " + ((PlayerEvent) starNubEvent).getPLAYER_SESSION().getCleanNickName() + " - Account Reminder", new Runnable() {
                            @Override
                            public void run() {
                                StarNub.getMessageSender().playerMessage("StarNub", ((PlayerEvent) starNubEvent).getPLAYER_SESSION(),
                                        "StarNub has detected that you currently have no account with this server. Please use \"/account\" for " +
                                                "more details on creating an account. Accounts do give you more access!"
                                );

                            }
                        }, 5, TimeUnit.MINUTES);
                    }

                }
        });
    }

    public volatile String testField = null;

    // ", 'del', 'admindel', 'delchar', 'admindel'"

    @Override
    public void onCommand(Object sender, String command, String[] args) {
        String commandShort = StarNub.getCommandSender().hasShortcutStringBuilder("account");
        if (sender instanceof Player) {
            if (args == null) {
                Player playerSession = StarNub.getServer().getConnections().getOnlinePlayerByAnyIdentifier(sender);
                if (playerSession.getCharacter().getAccount() != null) {
                    StarNub.getMessageSender().playerMessage("StarNub", playerSession, "Your account name is: \""+playerSession.getCharacter().getAccount().getAccountName()+"\". " +
                            "you can reset your password by using \""+commandShort+" changepass {password}\"");
                } else {
                    StarNub.getMessageSender().playerMessage("StarNub", playerSession, "You do not have an account for this character. To create an account use \""+commandShort+" new {accountname} {password}\""+
                            " To add this character to an existing account use \""+commandShort+" addchar {accountname} {password}\"");
                }
            } else {
                if (args[0].equals("new") || args[0].equals("addchar")) {
                    newAccount(sender, command, args, false);
                    return;
                }
                if (args[0].equals("adminnew") || args[0].equals("adminaddchar")) {
                    newAccount(sender, command, args, true);
                    return;
                }
                if (args[0].equals("changepass")) {
                    passwordChange(sender, command, args);
                    return;
                }
            }
        }
    }

    private void newAccount(Object sender, String command, String[] args, boolean adminOverride){
        if (args.length == 3) {
            if (adminOverride) {
                /* Admin add or create account */
                long randomPassword = new Random().nextLong();
                Player playerSession = StarNub.getServer().getConnections().getOnlinePlayerByAnyIdentifier(args[1]);
                if (playerSession.getCharacter().getAccount() != null) {
                    StarNub.getMessageSender().playerMessage("StarNub", playerSession, "This character is already attached to a StarNub account.");
                    return;
                }
                Account account = null;
                if (args[0].equalsIgnoreCase("adminnew")) {
                    if (StarNub.getDatabaseTables().getAccounts().getAccountByName(args[1]) != null) {
                        StarNub.getMessageSender().playerMessage("StarNub", playerSession, "There is already an account by the name \"" + args[1] + "\".");
                        return;
                    } else {
                        account = new Account(playerSession.getCharacter(), args[1], Long.toString(randomPassword));
//                        StarNub.getDatabase().createAccount(account);
                    }
                } else if (args[0].equalsIgnoreCase("adminaddchar")) {
                    account = StarNub.getDatabaseTables().getAccounts().getAccountByName(args[1]);
                }
                if (account == null) {
                    //Password wrong?
                    StarNub.getMessageSender().playerMessage("StarNub", playerSession, "Something went wrong on creating or adding your characters to an account. Perhaps you used the wrong password.");
                    return;
                } else if (args[0].equalsIgnoreCase("adminnew")) {
                    StarNub.getMessageSender().playerMessage("StarNub", sender, "Account created. The account name is \"" + args[1] + "\".");
                    StarNub.getMessageSender().playerMessage("StarNub", playerSession, "Account created. You account name is \"" + args[1] + "\". You can set your password " +
                            "as long as your using a character attached to the account by using /changepass {new password}. You may also add other characters " +
                            "to this account logging into that character and using /addchar {account name} {account password}.");
                } else if (args[0].equalsIgnoreCase("adminaddchar")) {
                    StarNub.getMessageSender().playerMessage("StarNub", sender, playerSession.getCharacter().getCleanName() + " was added to account: \"" + args[1] + "\" .");
                    StarNub.getMessageSender().playerMessage("StarNub", playerSession, playerSession.getCharacter().getCleanName() + " was added to account: \"" + args[1] + "\" .");
                }
                playerSession.getCharacter().setAccount(account);
                playerSession.setAccount(account.getStarnubId());
            } else {
                /* Player add or create account */
                Player player = (Player) sender;
                if (player.getCharacter().getAccount() != null) {
                    StarNub.getMessageSender().playerMessage("StarNub", sender, "This character is already attached to a StarNub account.");
                    return;
                }
                Account account = null;
                if (args[0].equalsIgnoreCase("new")) {
                if (StarNub.getDatabaseTables().getAccounts().getAccountByName(args[1]) != null) {
                    StarNub.getMessageSender().playerMessage("StarNub", sender, "There is already an account by the name \"" + args[1] + "\".");
                    return;
                } else {
                    account = new Account(player.getCharacter(), args[1], args[2]);
//                    StarNub.getDatabase().createAccount(account);
                }
                } else if (args[0].equalsIgnoreCase("addchar")) {
                    account = StarNub.getDatabaseTables().getAccounts().getAccount(args[1], args[2]);
                }
                if (account == null) {
                    StarNub.getMessageSender().playerMessage("StarNub", sender, "Something went wrong on creating or adding your characters to an account.");
                    return;
                } else if (args[0].equalsIgnoreCase("new")) {
                    StarNub.getMessageSender().playerMessage("StarNub", sender, "Account created. You account name is \"" + args[1] + "\". You can set your password " +
                            "as long as your using a character attached to the account by using /changepass {new password}. You may also add other characters " +
                            "to this account logging into that character and using /addchar {account name} {account password}.");
                    StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_Created_StarNub_Account", player, null));
                } else if (args[0].equalsIgnoreCase("addchar")) {
                    StarNub.getMessageSender().playerMessage("StarNub", sender, player.getCharacter().getCleanName() + " was added to your account: \"" + args[1] + "\" .");
                    StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_Added_Character_StarNub_Account", player, null));
                }
                player.getCharacter().setAccount(account);
                player.setAccount(account.getStarnubId());

            }
        } else {
            StarNub.getMessageSender().playerMessage("StarNub", sender, "Not enough or to many arguments to create your account with StarNub.");
            return;
        }
    }

    private void passwordChange(Object sender, String command, String[] args){
        if (args.length == 2) {
            Player player = StarNub.getServer().getConnections().getOnlinePlayerByAnyIdentifier(sender);
            if (player.getCharacter().getAccount() == null) {
                StarNub.getMessageSender().playerMessage("StarNub", sender, "You must be logged into your account to change your password");
            } else if (player.getCharacter().getAccount().setAccountPassword(args[1])) {
                StarNub.getMessageSender().playerMessage("StarNub", sender, "Your password was successfully changed!");
                StarNub.getDatabaseTables().getAccounts().update(player.getCharacter().getAccount());
            }
        } else {
            StarNub.getMessageSender().playerMessage("StarNub", sender, "Not enough or to many arguments to change your StarNub account password.");
        }
    }
}

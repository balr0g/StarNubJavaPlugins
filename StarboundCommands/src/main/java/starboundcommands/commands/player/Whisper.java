package starboundcommands.commands.player;

import io.netty.channel.ChannelHandlerContext;
import org.starnub.StarNub;
import org.starnub.connectedentities.player.session.Player;
import org.starnub.eventsrouter.events.PlayerEvent;
import org.starnub.eventsrouter.events.StarNubEvent;
import org.starnub.eventsrouter.handlers.StarNubEventHandler;
import org.starnub.plugins.Command;
import org.starnub.senders.MessageSender;

import java.util.concurrent.ConcurrentHashMap;

public class Whisper extends Command {

    private volatile ConcurrentHashMap<ChannelHandlerContext, ChannelHandlerContext> whisperCache =  new ConcurrentHashMap<ChannelHandlerContext, ChannelHandlerContext>();;

    @Override
    public void onCommand(Object sender, String command, String[] args) {
        MessageSender msgSend = StarNub.getMessageSender();
        if (args.length == 0) {
            msgSend.playerOrConsoleMessage("StarNub", sender, "You did not provide enough arguments for /w or /r, to whisper use /w {player} {message}," +
                    " to reply to who you have whispered to last or received a message from use /r {message}.");
        }

        Player playerSender = (Player) sender;
        ChannelHandlerContext senderCtx = playerSender.getClientCtx();

        if (command.equalsIgnoreCase("r")) {

            if (whisperCache.containsKey(senderCtx)) {
                String args1;
                try {
                    args1 = " "+args[1];
                } catch (IndexOutOfBoundsException e) {
                    args1 = "";
                }
                StarNub.getMessageSender().playerMessageWhisper(sender, whisperCache.get(senderCtx), args[0] + args1);
                return;
            } else {
                msgSend.playerOrConsoleMessage("StarNub", sender, "You have not whispered or been whispered by anyone.");
                return;
            }
        }

        if (args[0].equalsIgnoreCase("on") && playerSender.getCharacter().getAccount() != null) {
            playerSender.getCharacter().getAccount().getAccountSettings().setWhisperBlocking(false);
            msgSend.playerOrConsoleMessage("StarNub", sender, "You are now accepting whispers.");
            //EVENTS
            return;
        } else if (args[0].equalsIgnoreCase("off") && playerSender.getCharacter().getAccount() != null) {
            playerSender.getCharacter().getAccount().getAccountSettings().setWhisperBlocking(true);
            msgSend.playerOrConsoleMessage("StarNub", sender, "You are now blocking whispers.");
            //EVENTS
            return;
        } else if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")) {
            msgSend.playerOrConsoleMessage("StarNub", sender,  "You must create an account or add this character to an existing account to block/unblock whispers.");
            return;
        }

//            StarNub.getMessageSender().commandResults(sender, "Only players can block/unblock whispers.", false);
//            return;

        Player playerReceiver = StarNub.getServer().getConnections().getOnlinePlayerByAnyIdentifier(args[0]);
        if (playerReceiver != null) {
            ChannelHandlerContext receiverCtx = playerReceiver.getClientCtx();
            if (command.equalsIgnoreCase("w") || command.equalsIgnoreCase("pm") || command.equalsIgnoreCase("msg")) {
                if (whisperCache.containsKey(playerSender.getClientCtx())) {
                    whisperCache.replace(senderCtx, receiverCtx);
                } else {
                    whisperCache.put(senderCtx, receiverCtx);
                }
                if (whisperCache.containsKey(receiverCtx)) {
                    whisperCache.replace(receiverCtx, senderCtx);
                } else {
                    whisperCache.put(receiverCtx, senderCtx);
                }
            }
        }
        StarNub.getMessageSender().playerMessageWhisper(sender, args[0], args[1]);
    }

    public void startEvents(){
        StarNub.getStarNubEventRouter().registerEventSubscription("StarNub", "Player_Disconnected", new StarNubEventHandler() {
            @Override
            public void onEvent(StarNubEvent eventData) {
                ChannelHandlerContext senderToRemove = ((PlayerEvent) eventData).getPLAYER_SESSION().getClientCtx();
                if (whisperCache.containsKey(senderToRemove)) {
                    whisperCache.remove(senderToRemove);
                }
            }
        });

//        StarNub.getEventSystem().getStarnubReactor().on($("NonPlayer_Disconnected"), ev -> {
//            ChannelHandlerContext senderToRemove = ((NonPlayerEvent) ev.getData()).getPlayerSession().getSENDER_CTX();
//            if (whisperCache.containsKey(senderToRemove)) {
//                whisperCache.remove(senderToRemove);
//            }
//        });
    }
}

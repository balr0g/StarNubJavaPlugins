package starboundcommands.commands.player;

import io.netty.channel.ChannelHandlerContext;
import org.starnub.StarNub;
import org.starnub.cache.objects.BooleanCache;
import org.starnub.cache.objects.TimeCache;
import org.starnub.cache.wrappers.PlayerCtxCacheWrapper;
import org.starnub.connectedentities.player.session.Player;
import org.starnub.eventsrouter.events.PlayerEvent;
import org.starnub.plugins.Command;
import org.starnub.server.datatypes.chat.ChatSendChannel;
import org.starnub.server.datatypes.color.GameColors;

import java.util.concurrent.TimeUnit;

public class PVP extends Command {

    private PlayerCtxCacheWrapper lastPvp;
    private PlayerCtxCacheWrapper pvpMode;

    public PVP() {
        lastPvp = new PlayerCtxCacheWrapper("StarboundCommands", "StarboundCommands - PVP - Last Use Cache", true);
        pvpMode = new PlayerCtxCacheWrapper("StarboundCommands", "StarboundCommands - PVP - Current Mode", true);
    }

    @Override
    public void onCommand(Object sender, String command, String[] args) {
        Player playerSession = StarNub.getServer().getConnections().getOnlinePlayerByAnyIdentifier(sender);
        ChannelHandlerContext ctx = ((Player) sender).getClientCtx();

        if (args != null) {
            switch (args.length){
                case 1: { oneArgCommand(args, playerSession); break; }
            }
        } else {
            if (isBlocked(playerSession)) {
                return;
            } else if (!StarNub.getServer().getConnections().hasPermission(playerSession, "starbound.pvp.bypass", true) && !canUseCommandPVPRate(playerSession)){
                return;
            } else {
                GameColors gc = StarNub.getMessageSender().getGameColors();
                /* PVP Mode Section */
                String chatMessage = null;
                BooleanCache pvpOnOff = (BooleanCache) pvpMode.getCache(ctx);
                if (pvpOnOff == null || !pvpOnOff.isBool()) {
                    chatMessage = gc.getDefaultNameColor() + playerSession.getGameName() + gc.validateColor("#DC143C") + " has entered PVP Mode.";
                    pvpMode.replaceCache(ctx, new BooleanCache(true));
                    StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_PVP_On", playerSession, null));
                } else {
                    chatMessage = gc.getDefaultNameColor() + playerSession.getGameName() + gc.validateColor("#00FF00") +  " has left PVP Mode.";
                    StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_PVP_Off", playerSession, null));
                    pvpMode.replaceCache(ctx, new BooleanCache(false));
                }
                String chatMessageString = chatMessage;
                StarNub.getMessageSender().serverChatMessageToServerForPlayer(playerSession, playerSession, ChatSendChannel.UNIVERSE, "/pvp");
                StarNub.getTask().getTaskScheduler().scheduleOneTimeTask("StarboundCommands", "Starbound Commands - Send - Player - " + playerSession.getCleanNickName() + " - PVP Message Send", new Runnable() {
                    @Override
                    public void run() {
                        StarNub.getMessageSender().serverChatMessageToServerForPlayer(playerSession, playerSession, ChatSendChannel.PLANET, chatMessageString);
                    }
                }, 1, TimeUnit.SECONDS);
            }
        }
    }

    private void oneArgCommand(String[] args, Player playerSession){
        if (args[0].equalsIgnoreCase("mode")){
            BooleanCache pvpOnOff = (BooleanCache) pvpMode.getCache(playerSession.getClientCtx());
            if (pvpOnOff == null || !pvpOnOff.isBool()){
                StarNub.getMessageSender().playerMessage("StarNub", playerSession, StarNub.getMessageSender().getGameColors().validateColor("#00FF00") + "You are not in PVP Mode.");
            } else {
                StarNub.getMessageSender().playerMessage("StarNub", playerSession, StarNub.getMessageSender().getGameColors().validateColor("#DC143C") + "You are in PVP Mode.");
            }
        }
    }

    private boolean isBlocked(Player playerSession){
        if (StarNub.getServer().getConnections().hasPermission(playerSession, "starbound.pvp.block", false)) {
            StarNub.getMessageSender().playerMessage("StarNub", playerSession, "You have been blocked from entering or leaving PVP Mode.");
            StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_PVP_Denied_Block_Permission", playerSession, null));
            return true;
        } else {
            return false;
        }
    }

    private boolean canUseCommandPVPRate(Player playerSession) {
        /* PVP Time Section */
        int integer = StarNub.getServer().getConnections().getPermissionVariableInteger(playerSession, "starbound.pvprate");
        if (integer < -100000) {
            integer = 1;
        } else if (integer == -100000) {
            integer = 0;
        }
        if (integer != 0) {
            TimeCache cache = (TimeCache) lastPvp.getCache(playerSession.getClientCtx());
            if (cache != null) {
                if (!cache.isPastDesignatedTime(integer)) {
                    StarNub.getMessageSender().playerMessage("StarNub", playerSession, "You cannot leave or enter PVP Mode more the once every " + (integer / 1000) +
                            " seconds.");
                    StarNub.getStarNubEventRouter().notify(new PlayerEvent("Player_PVP_Denied_Timer", playerSession, null));
                    return false;
                }
            } else {
                lastPvp.replaceCache(playerSession.getClientCtx(), new TimeCache());
            }
        }
        return true;
    }

}
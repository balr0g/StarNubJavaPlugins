package starboundcommands.commands.player;

import io.netty.channel.ChannelHandlerContext;
import org.starnub.StarNub;
import org.starnub.cache.objects.TimeCache;
import org.starnub.cache.wrappers.PlayerCtxCacheWrapper;
import org.starnub.connectedentities.player.session.Player;
import org.starnub.plugins.Command;

public class Nick extends Command {

    private PlayerCtxCacheWrapper lastChange;

    public Nick() {
        lastChange = new PlayerCtxCacheWrapper("StarboundCommands", "StarboundCommands Nick Name Frequency Tracker", true);
    }

    @Override
    public void onCommand(Object sender, String command, String[] args) {
        Player playerSession = StarNub.getServer().getConnections().getOnlinePlayerByAnyIdentifier(sender);
        if (!StarNub.getServer().getConnections().hasPermission(playerSession, "starbound.nick.block", false)) {
            if (!StarNub.getServer().getConnections().hasPermission(playerSession, "starbound.nick.bypass", true)) {
                int integer = StarNub.getServer().getConnections().getPermissionVariableInteger(playerSession, "starbound.nickrate");
                if (integer < -100000) {
                    integer = 30000;
                } else if (integer == -100000) {
                    integer = 0;
                }
                if (integer != 0) {
                    ChannelHandlerContext ctx = ((Player) sender).getClientCtx();
                    TimeCache cache = (TimeCache) lastChange.getCache(ctx);
                    if (cache != null) {
                        if (!cache.isPastDesignatedTime(integer)) {
                            StarNub.getMessageSender().playerMessage("StarNub", sender, "You cannot change your nick name more then once per " + (integer / 1000) +
                                    " seconds.");
                            return;
                        }
                    } else {
                        lastChange.replaceCache(ctx, new TimeCache());
                    }
                }
            }
            StarNub.getServer().getConnections().nickNameChanger(
                    "StarNub",
                    sender,
                    StarNub.getServer().getServerChat().getChatFilter().cleanNameAccordingToPermissionsAnyIdentifier(sender, args[0]),
                    null);
        } else {
            StarNub.getMessageSender().playerMessage("StarNub", playerSession, "You have been blocked from changing your nick name.");
        }
    }
}

package essentials.commands;

import essentials.Essentials;
import io.netty.channel.ChannelHandlerContext;
import starbounddata.types.chat.Mode;
import starbounddata.types.color.Colors;
import starnubserver.cache.wrappers.PlayerCtxCacheWrapper;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.plugins.Command;
import starnubserver.plugins.resources.PluginConfiguration;
import utilities.cache.objects.TimeCache;

import java.util.HashMap;
import java.util.HashSet;

public class Unsubscribe extends Command {

    public Unsubscribe(String PLUGIN_NAME, HashSet<String> COMMANDS, HashSet<String> MAIN_ARGS, HashMap<String, Integer> CUSTOM_SPLIT, String COMMAND_CLASS, String COMMAND_NAME, int CAN_USE, String DESCRIPTION) {
        super(PLUGIN_NAME, COMMANDS, MAIN_ARGS, CUSTOM_SPLIT, COMMAND_CLASS, COMMAND_NAME, CAN_USE, DESCRIPTION);
    }

    @Override
    public void onCommand(PlayerSession playerSession, String command, String[] args) {
        ChannelHandlerContext clientCtx = playerSession.getCONNECTION().getCLIENT_CTX();
        Essentials essentials = (Essentials) getPLUGIN();
        PluginConfiguration CONFIG = essentials.getCONFIGURATION();
        PlayerCtxCacheWrapper joinLeave = essentials.getPlayerMessages().getUNSUBSCRIBED_JOIN_LEAVE();
        String colorUnvalidated = (String) CONFIG.getNestedValue("player_messages", "connect_disconnect", "unsubscribe", "color");
        String chatColor = Colors.validate(colorUnvalidated);
        TimeCache cache = joinLeave.getCache(clientCtx);
        if (cache == null) {
            joinLeave.addCache(clientCtx, new TimeCache());
            String unsubMessage = chatColor + CONFIG.getNestedValue("player_messages", "connect_disconnect", "unsubscribe", "unsubscribe");
            sendMessage(playerSession, unsubMessage);
        } else {
            joinLeave.removeCache(clientCtx);
            String subMessage = chatColor + CONFIG.getNestedValue("player_messages", "connect_disconnect", "unsubscribe", "subscribe");
            sendMessage(playerSession, subMessage);
        }
    }

    private void sendMessage(PlayerSession playerSession, String message) {
        playerSession.sendChatMessage(getPLUGIN_NAME(), Mode.BROADCAST, message);
    }
}

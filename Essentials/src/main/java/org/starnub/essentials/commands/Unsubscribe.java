package org.starnub.essentials.commands;

import io.netty.channel.ChannelHandlerContext;
import org.starnub.essentials.Essentials;
import org.starnub.starnubserver.cache.objects.PlayerSessionCache;
import org.starnub.starnubserver.cache.wrappers.PlayerCtxCacheWrapper;
import org.starnub.starnubserver.connections.player.session.PlayerSession;
import org.starnub.starnubserver.plugins.Command;
import org.starnub.starnubserver.plugins.resources.PluginConfiguration;
import starbounddata.types.color.Colors;
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
            joinLeave.addCache(clientCtx, new PlayerSessionCache(playerSession));
            String unsubMessage = chatColor + CONFIG.getNestedValue("player_messages", "connect_disconnect", "unsubscribe", "unsubscribe");
            sendMessage(playerSession, unsubMessage);
        } else {
            joinLeave.removeCache(clientCtx);
            String subMessage = chatColor + CONFIG.getNestedValue("player_messages", "connect_disconnect", "unsubscribe", "subscribe");
            sendMessage(playerSession, subMessage);
        }
    }

    private void sendMessage(PlayerSession playerSession, String message) {
        playerSession.sendBroadcastMessageToClient(getPLUGIN_NAME(), message);
    }
}

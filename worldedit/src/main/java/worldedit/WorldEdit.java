/*
* Copyright (C) 2014 www.StarNub.org - Underbalanced
*
* This file is part of org.starnub a Java Wrapper for Starbound.
*
* This above mentioned StarNub software is free software:
* you can redistribute it and/or modify it under the terms
* of the GNU General Public License as published by the Free
* Software Foundation, either version  3 of the License, or
* any later version. This above mentioned CodeHome software
* is distributed in the hope that it will be useful, but
* WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See
* the GNU General Public License for more details. You should
* have received a copy of the GNU General Public License in
* this StarNub Software.  If not, see <http://www.gnu.org/licenses/>.
*/

package worldedit;


import io.netty.channel.ChannelHandlerContext;
import starbounddata.chat.ChatReceiveChannel;
import starbounddata.packets.Packet;
import starbounddata.packets.tile.DamageTileGroupPacket;
import starbounddata.tiles.TileDamage;
import starbounddata.tiles.TileDamageType;
import starbounddata.tiles.TileLayer;
import starbounddata.vectors.Vec2F;
import starbounddata.vectors.Vec2I;
import starbounddata.vectors.Vec2IArray;
import starnubserver.connections.player.StarNubProxyConnection;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.events.packet.PacketEventHandler;
import starnubserver.events.packet.PacketEventSubscription;
import starnubserver.plugins.JavaPlugin;
import starnubserver.plugins.generic.CommandInfo;
import starnubserver.plugins.generic.PluginDetails;
import starnubserver.plugins.resources.PluginRunnables;
import starnubserver.plugins.resources.YAMLFiles;
import starnubserver.resources.files.PluginConfiguration;
import utilities.events.Priority;

import java.io.File;
import java.util.HashSet;

/**
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public final class WorldEdit extends JavaPlugin {

    volatile Vec2I point1 = null;
    volatile Vec2I point2 = null;
    public WorldEdit(String NAME, File FILE, String MAIN_CLASS, PluginDetails PLUGIN_DETAILS, PluginConfiguration CONFIGURATION, YAMLFiles FILES, CommandInfo COMMAND_INFO, PluginRunnables PLUGIN_RUNNABLES) {
        super(NAME, FILE, MAIN_CLASS, PLUGIN_DETAILS, CONFIGURATION, FILES, COMMAND_INFO, PLUGIN_RUNNABLES);
    }

    @Override
    public void onPluginEnable() {
        int size = (int) getCONFIGURATION().getNestedValue("tiles", "group_size");

        new PacketEventSubscription("StarNub", Priority.CRITICAL, DamageTileGroupPacket.class, new PacketEventHandler() {
            volatile TileLayer tileLayer = null;

            @Override
            public void onEvent(Packet packet) {
                PlayerSession playerSession = PlayerSession.getPlayerSession(packet);
                if (point1 == null){
                    Vec2F point = ((DamageTileGroupPacket) packet).getSourcePosition();
                    point1 = new Vec2I(point.getX(), point.getY());
                    System.out.println(point + " " + point1);
                    playerSession.sendChatMessage("WorldEdit", ChatReceiveChannel.UNIVERSE, "Point 1 Selected: " + point1.toString());
                    packet.recycle();
                    return;
                }
                if (point2 == null){
                    Vec2F point1 = ((DamageTileGroupPacket) packet).getSourcePosition();
                    point2 = new Vec2I(point1.getX(), point1.getY());
                    System.out.println(point1 + " " + point2);
                    playerSession.sendChatMessage("WorldEdit", ChatReceiveChannel.UNIVERSE, "Point 2 Selected: " + point2.toString());
                    packet.recycle();
                    return;
                }
                if (tileLayer == null){
                    tileLayer = ((DamageTileGroupPacket) packet).getLayer();
                    playerSession.sendChatMessage("WorldEdit", ChatReceiveChannel.UNIVERSE, "Layer Selected: " + tileLayer.toString());
                    packet.recycle();
                    return;
                }
                StarNubProxyConnection starNubProxyConnection = (StarNubProxyConnection) playerSession.getCONNECTION();
                ChannelHandlerContext server_ctx = starNubProxyConnection.getSERVER_CTX();
                System.out.println(point1.toString() + point2.toString());
                Vec2IArray vec2IArray = new Vec2IArray(point1, point2);
                HashSet<Vec2IArray> vec2IGroups = vec2IArray.getVec2IGroups(size);
                System.out.println(vec2IGroups.toString());
                for (Vec2IArray vec2IArrayG : vec2IGroups){
                    TileDamage tileDamage = new TileDamage(TileDamageType.BEAMISH, 100f, 1);
                    new DamageTileGroupPacket(server_ctx, vec2IArrayG, tileLayer, ((DamageTileGroupPacket) packet).getSourcePosition(), tileDamage).routeToDestinationNoFlush();
                }
                point1 = null;
                point2 = null;
                tileLayer = null;
                packet.recycle();
            }
        });
    }

    @Override
    public void onPluginDisable() {

    }

  
}

package nocheats.AntiGreif;

import org.starnub.StarNub;
import org.starnub.cache.objects.TimeCache;
import org.starnub.cache.wrappers.PlayerCtxCacheWrapper;
import org.starnub.eventsrouter.handlers.PacketEventHandler;
import org.starnub.server.datatypes.tiles.TileDamageType;
import org.starnub.server.packets.Packet;
import org.starnub.server.packets.tile.DamageTileGroupPacket;

import java.util.Map;

public class Tiles {

    private int miscSpeed;
    private int miscRadius;
    private float miscMinDmg;
    private float miscMaxDmg;

    private int maniSpeed;
    private int maniRadius;
    private float maniMinDmg;
    private float maniMaxDmg;

    private PlayerCtxCacheWrapper tileCacheManipulators;
    private PlayerCtxCacheWrapper tileCacheMisc;

    public void registerTileGroupDamage() {
        Map tileConfig = (Map) StarNub.getPluginManager().getConfiguration("nocheats").get("tiles");
        Map miscStats = (Map) tileConfig.get("misc");
        Map maniStats = (Map) tileConfig.get("manipulators");

        miscSpeed = (int) miscStats.get("frequency");
        miscRadius = (int) miscStats.get("radius");
        miscRadius = miscRadius * miscRadius;

        double tempMiscMinDmg = (double) miscStats.get("damage_min");
        double tempMiscMaxdmg = (double) miscStats.get("damage_max");
        miscMinDmg = (float) tempMiscMinDmg;
        miscMaxDmg = (float) tempMiscMaxdmg;

        maniSpeed = (int) maniStats.get("frequency");
        maniRadius = (int) maniStats.get("radius");
        maniRadius = maniRadius * maniRadius;
//        maniMinDmg = (float) maniStats.get("damage_min");
//        maniMaxDmg = (float) maniStats.get("damage_max");

        tileCacheManipulators = new PlayerCtxCacheWrapper("NoCheats", "NoCheats - Tile Damage - Manipulators", true);
        tileCacheMisc = new PlayerCtxCacheWrapper("NoCheats", "NoCheats - Tile Damage - Misc", true);
        startTileDamageListenerEvent();
    }

    public void startTileDamageListenerEvent() {
        StarNub.getPacketEventRouter().registerEventSubscription("NoCheats", DamageTileGroupPacket.class, new PacketEventHandler() {
            @Override
            public Packet onEvent(Packet packet) {
                DamageTileGroupPacket dg = (DamageTileGroupPacket) packet;
                float tileDamage = dg.getTileDamage().getAmount();
                boolean inRange = ((tileDamage < .6) && (tileDamage > .4));
                if (inRange || dg.getTileDamage().getTileDamageType().equals(TileDamageType.BEAMISH)) {
                    TimeCache time = (TimeCache) tileCacheManipulators.getCache(dg.getSenderCTX());
                    if (time != null) {
                        if (!time.isPastDesignatedTimeRefreshTimeNowIfPast(maniSpeed)) {
                            packet.setRecycle(true);
                            return packet;
                        } else {
                            time.setCacheTimeNow();
                        }
                    } else {
                        tileCacheManipulators.addCache(dg.getSenderCTX(), new TimeCache());
                    }
                    if (!inRange) {
                        dg.getTileDamage().setAmount(0.5f);
                    }
                    if (dg.getTilePositions().size() > maniRadius) {
                        dg.setTilePositions(dg.getTilePositions().subList(0, maniRadius));
                    }
                } else {
                    TimeCache time = (TimeCache) tileCacheMisc.getCache(dg.getSenderCTX());
                    if (time != null) {
                        if (!time.isPastDesignatedTimeRefreshTimeNowIfPast(miscSpeed)) {
                            packet.setRecycle(true);
                            return packet;
                        } else {
                            time.setCacheTimeNow();
                        }
                    } else {
                        tileCacheMisc.addCache(dg.getSenderCTX(), new TimeCache());
                    }
                    if (dg.getTileDamage().getAmount() > miscMaxDmg) {
                        dg.getTileDamage().setAmount(miscMaxDmg);
                    } else if (dg.getTileDamage().getAmount() < miscMinDmg) {
                        dg.getTileDamage().setAmount(miscMinDmg);
                    }
                    if (dg.getTilePositions().size() > (miscRadius)) {
                        dg.setTilePositions(dg.getTilePositions().subList(0, miscRadius));
                    }
                }
                return packet;
            }
        });
    }
}








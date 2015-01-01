package essentials.classes;

import starbounddata.types.chat.Mode;
import starbounddata.types.color.Colors;
import starnubserver.StarNubTask;
import starnubserver.cache.wrappers.PlayerUUIDCacheWrapper;
import starnubserver.connections.player.session.PlayerSession;
import starnubserver.events.events.StarNubEvent;
import starnubserver.events.starnub.StarNubEventHandler;
import starnubserver.resources.files.PluginConfiguration;
import utilities.cache.objects.BooleanCache;
import utilities.cache.objects.TimeCache;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CrashedHandler extends StarNubEventHandler {

    private final PluginConfiguration CONFIG;
    private final PlayerUUIDCacheWrapper PLAYER_UUID_CACHE = new PlayerUUIDCacheWrapper("Essentials", "Essentials - Crash Notification", true);

    public CrashedHandler(PluginConfiguration CONFIG) {
        this.CONFIG = CONFIG;
    }

    public PlayerUUIDCacheWrapper getPLAYER_UUID_CACHE() {
        return PLAYER_UUID_CACHE;
    }

    @Override
    public void onEvent(StarNubEvent starNubEvent) {
        PlayerSession playerSession = (PlayerSession) starNubEvent.getEVENT_DATA();
        UUID uuid = playerSession.getPlayerCharacter().getUuid();
        TimeCache cache = PLAYER_UUID_CACHE.removeCache(uuid);
        if (cache != null) {
            BooleanCache booleanCache = (BooleanCache) cache;
            if (booleanCache.isBool()) {
                String color = Colors.validate((String) CONFIG.getNestedValue("monitor", "crashed_notification", "color"));
                String message = (String) CONFIG.getNestedValue("monitor", "crashed_notification", "message");
                new StarNubTask("Essentials", "Essentials - Server Crash - Notification - " + uuid.toString(), 3, TimeUnit.SECONDS, () -> playerSession.sendChatMessage("StarNub", Mode.BROADCAST, color + message));
            }
        }
        PLAYER_UUID_CACHE.addCache(uuid, new BooleanCache(false));
    }
}

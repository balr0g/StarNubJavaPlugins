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
package org.starnub.ejninsync;

import org.starnub.StarNub;
import org.starnub.cache.wrappers.PlayerCtxCacheWrapper;
import org.starnub.connectedentities.player.account.Account;
import org.starnub.connectedentities.player.session.Player;
import org.starnub.ejninsync.connectors.DatabaseConnector;
import org.starnub.ejninsync.connectors.EnjinConnector;
import org.starnub.ejninsync.ejinusermanagment.AccountLink;
import org.starnub.ejninsync.ejinusermanagment.datatypes.Tag;
import org.starnub.plugins.Plugin;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public final class EnjinSync extends Plugin {

    private DatabaseConnector databaseConnector;
    private EnjinConnector enjinConnector;
    private ConcurrentHashMap<String, Tag> enjinGroupTags;
    private ConcurrentHashMap<String, Tag> enjinAwardTags;
    private PlayerCtxCacheWrapper playerCtxCacheWrapper;

    @Override
    @SuppressWarnings("unchecked")
    public void onPluginEnable() {
        Map<String, Object> config = (Map<String, Object>) StarNub.getPluginManager().getConfiguration("EnjinSync");
        Map<String, Object> databaseConfig = (Map<String, Object>) config.get("database");
        Map<String, Object> otherConfig = (Map<String, Object>) StarNub.getPluginManager().getConfiguration("EnjinSync").get("other");
        databaseConnector = new DatabaseConnector(
                "EnjinSync",
                (String) databaseConfig.get("type"),
                (String) databaseConfig.get("mysql_url"),
                (String) databaseConfig.get("mysql_user"),
                (String) databaseConfig.get("mysql_pass"));
        enjinConnector = new EnjinConnector();
        playerCtxCacheWrapper = new PlayerCtxCacheWrapper("EnjinSync", "EnjinSync - Account Link - Cache", true, null, 0, 0);
        /* Sync the website tags and awards */
        ArrayList<ConcurrentHashMap<String, Tag>> tagMaps = enjinConnector.getEnjinTagsJSONObjectFiltered((Map<String, Object>) config.get("groups_map"), (Map<String, Object>) config.get("awards_map"));
        enjinGroupTags = tagMaps.get(0);
        enjinAwardTags =  tagMaps.get(1);
        taskScheduler((int) otherConfig.get("refresh_rate"));

       //task refresh online list
        //Refresh
    }

    private void taskScheduler(int refresh_rate) {


    }

    @Override
    public void onPluginDisable() {

    }

    private void onJoinEventListener(){
//        StarNub.getEventSystem().getStarnubReactor().on($("Player_Connection_Success"), ev -> {
//            scheduleEnjinAccountNotification(((PlayerEvent) ev.getData()).getPLAYER_SESSION());
//        });
//        StarNub.getEventSystem().getStarnubReactor().on($("Player_Created_StarNub_Account"), ev -> {
//            scheduleEnjinAccountNotification((Player) ev.getData());
//        });
    }

    private void scheduleEnjinAccountNotification(Player player){
        Account account = player.getCharacter().getAccount();
        AccountLink accountLink;
        if (account != null) {
            accountLink = databaseConnector.getAccountLinkByStarNubId(account.getStarnubId());
            if (accountLink == null){
                StarNub.getTask().getTaskScheduler().scheduleOneTimeTask("EnjinSync", "EnjinSync - Send - Player - " + player.getCleanNickName() + " - Account Link Reminder", new Runnable() {
                    @Override
                    public void run() {
                        StarNub.getMessageSender().playerMessage("EnjinSync", player,
                                "EnjinSync has detected that you currently do not have a website account linked to your StarNub account. " +
                                        "please use \"/enjin help\" for more details."
                        );
                    }
                }, 5, TimeUnit.MINUTES);
            } else {
                //TODO build cache and retrieve data



            }
        }
    }


    

}

// TODO FOR CHARACTERS
//http://www.free-universe.com



/*
 * TODO
 * Get JSON Data
 * Create Map with player data that will contain everything about the player
 *
 *
 */
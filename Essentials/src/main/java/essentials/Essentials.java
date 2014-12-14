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

package essentials;

import org.starnub.StarNub;
import org.starnub.cache.objects.TimeCache;
import org.starnub.cache.wrappers.PlayerCtxCacheWrapper;
import org.starnub.connectedentities.player.session.Player;
import org.starnub.eventsrouter.events.IntegerEvent;
import org.starnub.eventsrouter.events.PlayerEvent;
import org.starnub.eventsrouter.events.StarNubEvent;
import org.starnub.eventsrouter.events.TimeEvent;
import org.starnub.eventsrouter.handlers.StarNubEventHandler;
import starnubserver.plugins.JavaPlugin;
import starnubserver.plugins.generic.CommandInfo;
import starnubserver.plugins.generic.PluginDetails;
import starnubserver.plugins.resources.PluginRunnables;
import starnubserver.plugins.resources.YAMLFiles;
import starnubserver.resources.files.PluginConfiguration;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Represents the StarNub Internal Event Plugins to handle events
 * <p>
 * OPTIONAL - THESE CAN BE TURNED OFF IN THE CONFIGURATION
 * <p>
 * This .jar will be within StarNub but extracted on deletion.
 * <p>
 * Credit goes to Reactor.io and its examples
 * <p>
 *
 * @author Daniel (Underbalanced) (www.StarNub.org)
 * @since 1.0
 */
public final class Essentials extends JavaPlugin {

    private PlayerCtxCacheWrapper joinLeaveCache;



    @Override
    public void onPluginEnable() {
        joinLeaveCache = new PlayerCtxCacheWrapper("Essentials", "Player Join and Leave Cache", true, 40, true);
        /* If you do not have a server monitor plugin, your server will not start with this off */
        if ((boolean) StarNub.getPluginManager().getConfiguration("essentials").get("server_monitor")){
            serverMonitor();
        }
        if ((boolean) StarNub.getPluginManager().getConfiguration("essentials").get("server_broadcast")) {
            serverBroadcast();
        }
        if ((boolean) StarNub.getPluginManager().getConfiguration("essentials").get("auto_restart")) {
            autoRestart();
        }
        if ((boolean) StarNub.getPluginManager().getConfiguration("essentials").get("motd")) {
            motd();
        }
        if ((boolean) StarNub.getPluginManager().getConfiguration("essentials").get("player_join_leave_message")) {
            playerJoinDisconnectMessages();
        }

    }

    @Override
    public void onPluginDisable() {

    }

    /**
     * This will listen for Starbound Server Events and act
     * accordingly to keep the server online
     */
    protected void serverMonitor () {
        StarNub.getStarNubEventRouter().registerEventSubscription("Essentials", "StarNub_Startup_Complete", new StarNubEventHandler() {
                    @Override
                    public void onEvent(StarNubEvent<String> starNubEvent) {
                        StarNub.getServer().getStarboundManager().startUp();
                    }
                }
        );

        StarNub.getStarNubEventRouter().registerEventSubscription("Essentials", "Starbound_Server_Status_Crashed", new StarNubEventHandler() {
                    @Override
                    public void onEvent(StarNubEvent<String> starNubEvent) {
                        StarNub.getLogger().cErrPrint("Essentials", "Starbound Server has crashed.");
                        StarNub.getServer().getStarboundManager().startUp();
                    }
                }
        );

        StarNub.getStarNubEventRouter().registerEventSubscription("Essentials", "Starbound_Server_Status_Unresponsive", new StarNubEventHandler() {
                    @Override
                    public void onEvent(StarNubEvent<String> starNubEvent) {
                        StarNub.getLogger().cErrPrint("Essentials", "12 attempts to check the Starbound Server responsiveness have" +
                                "failed. Restarting the Starbound Server.");
                        StarNub.getServer().getStarboundManager().restart();
                    }
                }
        );

        StarNub.getStarNubEventRouter().registerEventSubscription("Essentials", "Starbound_Server_Status_TCP_Query_Failed", new StarNubEventHandler() {
                    @Override
                    public void onEvent(StarNubEvent<String> starNubEvent) {
                        StarNub.getLogger().cErrPrint("Essentials", "An attempt to check the Starbound Server responsiveness" +
                                "has failed. Connection Attempt (" + ((IntegerEvent) starNubEvent).getCOUNT() + "/12).");
                    }
                }
        );

        StarNub.getStarNubEventRouter().registerEventSubscription("Essentials", "Starbound_Server_Status_Starting_Up", new StarNubEventHandler() {
                    @Override
                    public void onEvent(StarNubEvent<String> starNubEvent) {
                        StarNub.getLogger().cInfoPrint("Essentials", "If you have mods please be patient," +
                                " it may take a little longer. StarNub will notify you when the server is ready for Players.");
                    }
                }
        );

//            EventSystem.getStarnubReactor().on($("Starbound_Server_Status_Starting_Up_Still"), ev -> {
//                StarNub.getMessageSender().cInfoPrint("Starbound Server is still starting up...");
//            });

        StarNub.getStarNubEventRouter().registerEventSubscription("Essentials", "Starbound_Server_Status_Online", new StarNubEventHandler() {
                    @Override
                    public void onEvent(StarNubEvent<String> starNubEvent) {
                        StarNub.getLogger().cInfoPrint("Essentials", "Starbound Server Version: " +
                                StarNub.getServer().getProtocolVersionPacket().getProtocolVersion() + ".");
                        StarNub.getLogger().cInfoPrint("Essentials", "Starbound is now online, player can now connect to port: "
                                + ((Map) StarNub.getConfiguration().getConfiguration().get("starnub settings")).get("starnub_port") + ".");
                    }
                }
        );

        StarNub.getStarNubEventRouter().registerEventSubscription("Essentials", "Starbound_Server_Status_Restarting", new StarNubEventHandler() {
                    @Override
                    public void onEvent(StarNubEvent<String> starNubEvent) {
                        StarNub.getLogger().cInfoPrint("Essentials", "Starbound is restarting.");
                    }
                }
        );

        StarNub.getStarNubEventRouter().registerEventSubscription("Essentials", "Starbound_Server_Status_Shutting_Down", new StarNubEventHandler() {
                    @Override
                    public void onEvent(StarNubEvent<String> starNubEvent) {
                        StarNub.getLogger().cInfoPrint("Essentials", "Starbound is shutting down.");
                    }
                }
        );

        StarNub.getStarNubEventRouter().registerEventSubscription("Essentials", "Starbound_Server_Status_Shutdown", new StarNubEventHandler() {
                    @Override
                    public void onEvent(StarNubEvent<String> starNubEvent) {
                        StarNub.getLogger().cInfoPrint("Essentials", "Starbound is now shut down.");
                    }
                }
        );
    }


    /**
     * This will execute a auto restart at the designated time, using
     * the configuration variable
     */
    protected void autoRestart() {
        StarNub.getStarNubEventRouter().registerEventSubscription("Essentials", "Starbound_Server_Status_Online", new StarNubEventHandler() {
            @Override
            public void onEvent(StarNubEvent<String> starNubEvent) {
                StarNub.getTask().getTaskScheduler().purgeBasedOnTaskNameContains("Essentials", "Auto-Restart");

                int restartTimer = (int) StarNub.getPluginManager().getConfiguration("essentials").get("auto_restart_timer");
                int messageRestart = restartTimer - 1;

                StarNub.getTask().getTaskScheduler().scheduleOneTimeTask("Essentials", "Essentials - Starbound - Auto-Restart", new Runnable() {
                    @Override
                    public void run() {

                        String color = StarNub.getMessageSender().getGameColors().validateColor("crimson");
                        StarNub.getMessageSender().serverBroadcast("StarNub", color + "STARBOUND WILL RESTART IN 60 MINUTES.");
                        StarNub.getStarNubEventRouter().notify(new TimeEvent("Essentials_Auto_Restart", System.currentTimeMillis() + (((restartTimer * 60)) * 60) * 1000));

                        StarNub.getTask().getTaskScheduler().scheduleOneTimeTask("Essentials", "Essentials - Starbound - Auto-Restart - 45 Minute Warning", new Runnable() {
                            @Override
                            public void run() {
                                StarNub.getMessageSender().serverBroadcast("StarNub", color + "STARBOUND WILL RESTART IN 45 MINUTES.");
                                StarNub.getStarNubEventRouter().notify(new TimeEvent("Essentials_Auto_Restart_Message", System.currentTimeMillis() + (((45 * 60)) * 60) * 1000));
                            }
                        }, 15, TimeUnit.MINUTES);

                        StarNub.getTask().getTaskScheduler().scheduleOneTimeTask("Essentials", "Essentials - Starbound - Auto-Restart - 30 Minute Warning", new Runnable() {
                            @Override
                            public void run() {
                                StarNub.getMessageSender().serverBroadcast("StarNub", color + "STARBOUND WILL RESTART IN 30 MINUTES.");
                                StarNub.getStarNubEventRouter().notify(new TimeEvent("Essentials_Auto_Restart_Message", System.currentTimeMillis() + (((30 * 60)) * 60) * 1000));
                            }
                        }, 30, TimeUnit.MINUTES);

                        StarNub.getTask().getTaskScheduler().scheduleOneTimeTask("Essentials", "Essentials - Starbound - Auto-Restart - 15 Minute Warning", new Runnable() {
                            @Override
                            public void run() {
                                StarNub.getMessageSender().serverBroadcast("StarNub", color + "STARBOUND WILL RESTART IN 15 MINUTES.");
                                StarNub.getStarNubEventRouter().notify(new TimeEvent("Essentials_Auto_Restart_Message", System.currentTimeMillis() + (((15 * 60)) * 60) * 1000));
                            }
                        }, 45, TimeUnit.MINUTES);

                        StarNub.getTask().getTaskScheduler().scheduleOneTimeTask("Essentials", "Essentials - Starbound - Auto-Restart - 10 Minute Warning", new Runnable() {
                            @Override
                            public void run() {
                                StarNub.getMessageSender().serverBroadcast("StarNub", color + "STARBOUND WILL RESTART IN 10 MINUTES.");
                                StarNub.getStarNubEventRouter().notify(new TimeEvent("Essentials_Auto_Restart_Message", System.currentTimeMillis() + (((10 * 60)) * 60) * 1000));
                            }
                        }, 50, TimeUnit.MINUTES);

                        StarNub.getTask().getTaskScheduler().scheduleOneTimeTask("Essentials", "Essentials - Starbound - Auto-Restart - 5 Minute Warning", new Runnable() {
                            @Override
                            public void run() {
                                StarNub.getMessageSender().serverBroadcast("StarNub", color + "STARBOUND WILL RESTART IN 5 MINUTES.");
                                StarNub.getStarNubEventRouter().notify(new TimeEvent("Essentials_Auto_Restart_Message", System.currentTimeMillis() + (((5 * 60)) * 60) * 1000));
                            }
                        }, 55, TimeUnit.MINUTES);

                        StarNub.getTask().getTaskScheduler().scheduleOneTimeTask("Essentials", "Essentials - Starbound - Auto-Restart - 1 Minute Warning", new Runnable() {
                            @Override
                            public void run() {
                                StarNub.getMessageSender().serverBroadcast("StarNub", color + "STARBOUND WILL RESTART IN 1 MINUTES, PLEASE COME BACK IN 4-6 MINUTES.\"");
                                StarNub.getStarNubEventRouter().notify(new TimeEvent("Essentials_Auto_Restart_Message", System.currentTimeMillis() + ((60) * 60) * 1000));
                            }
                        }, 59, TimeUnit.MINUTES);

                        StarNub.getTask().getTaskScheduler().scheduleOneTimeTask("Essentials", "Essentials - Starbound - Auto-Restart - 59 Minute 30 Second Warning", new Runnable() {
                            @Override
                            public void run() {
                                StarNub.getMessageSender().serverBroadcast("StarNub", color + "STARBOUND WILL RESTART IN 30 SECONDS, PLEASE COME BACK IN 3-5 MINUTES.");
                                StarNub.getStarNubEventRouter().notify(new TimeEvent("Essentials_Auto_Restart_Message", System.currentTimeMillis() + (((30)) * 60) * 1000));
                            }
                        }, 3570, TimeUnit.SECONDS);

                        StarNub.getTask().getTaskScheduler().scheduleOneTimeTask("Essentials", "Essentials - Starbound - Auto-Restart - Restart", new Runnable() {
                            @Override
                            public void run() {
                                StarNub.getMessageSender().serverBroadcast("StarNub", color + "STARNUB IS NOW RESTARTING STARBOUND...");
                                StarNub.getStarNubEventRouter().notify(new TimeEvent("Essentials_Auto_Restart_Message", System.currentTimeMillis()));
                                StarNub.getServer().restartStarbound(true);
                            }
                        }, 60, TimeUnit.MINUTES);
                    }
                }, messageRestart, TimeUnit.HOURS);
            }
        });
    }

    /**
     * Sets a simple MOTD using the value found in the configuration
     */
    protected void motd () {
        StarNub.getStarNubEventRouter().registerEventSubscription("Essentials", "Player_Connection_Success", new StarNubEventHandler() {
            @Override
            public void onEvent(StarNubEvent<String> starNubEvent) {
                StarNub.getTask().getTaskScheduler().scheduleOneTimeTask("Essentials", "Essentials - Send - Player - " + ((PlayerEvent) starNubEvent).getPLAYER_SESSION().getCleanNickName() + " - MOTD Send", new Runnable() {
                    @Override
                    public void run() {
                        StarNub.getMessageSender().playerMessage("Free Universe", ((PlayerEvent) starNubEvent).getPLAYER_SESSION(),
                                "Welcome to Free Universe powered by StarNub (Updated: 10 October), this server is Modded, please visit www.free-universe.com for more details. There are currently "
                                        + StarNub.getServer().getConnections().getConnectedPlayers().size() + " player(s) online. This server tool is a work in progress, some commands are not available yet."
                        );

                    }
                }, 5, TimeUnit.SECONDS);
            }
        });
    }

    /**
     * Broadcast a server message at a designated interval
     */
    protected void serverBroadcast () {
        StarNub.getTask().getTaskScheduler().scheduleWithFixedDelayRepeatingTask("Essentials", "Essentials - Starbound - Auto-Restart", new Runnable() {
            @Override
            public void run() {
                StarNub.getMessageSender().serverBroadcast("StarNub", "Don't forget this is a modded server! Download the modpack at http://www.free-universe.com/sbm " +
                        "And don't forget to step by the teamspeak to chat once in a while: ts3.free-universe.com");
            }
        }, 15, 15, TimeUnit.MINUTES);
        StarNub.getTask().getTaskScheduler().scheduleWithFixedDelayRepeatingTask("Essentials", "Essentials - Starbound - Auto-Restart", new Runnable() {
            @Override
            public void run() {
                StarNub.getMessageSender().serverBroadcast("StarNub", "We made some recent changes, if you experience frequent connection loss, please post on the forums and search out \"Frequent Connection Lost\". http://www.free-universe.com/");
            }
        }, 14, 14, TimeUnit.MINUTES);
    }

    /**
     * Broadcast play joins and disconnects
     */
    protected void playerJoinDisconnectMessages() {
        StarNub.getStarNubEventRouter().registerEventSubscription("Essentials", "Player_Connection_Success", new StarNubEventHandler() {
            @Override
            public void onEvent(StarNubEvent<String> starNubEvent) {
                Player player = ((PlayerEvent) starNubEvent).getPLAYER_SESSION();
                joinLeaveCache.addCache(player.getClientCtx(), new TimeCache());
                StarNub.getTask().getTaskScheduler().scheduleOneTimeTask("Essentials", "Essentials - Send - Player - " + ((PlayerEvent) starNubEvent).getPLAYER_SESSION().getCleanNickName() + " - Has Connected Message", new Runnable() {
                    @Override
                    public void run() {
                        String color = StarNub.getMessageSender().getGameColors().getDefaultServerChatColor();
                        StarNub.getMessageSender().serverBroadcast("StarNub", player,
                                player.getNickName() + color + " has connected.");
                        StarNub.getLogger().cInfoPrint("Essentials", player.getCleanNickName() + " has connected. (IP: " +
                                player.getSessionIp().toString().substring(1) + ")");
                    }
                }, 3, TimeUnit.SECONDS);

                StarNub.getTask().getTaskScheduler().scheduleOneTimeTask("Essentials", "Essentials - Send - Player - " + ((PlayerEvent) starNubEvent).getPLAYER_SESSION().getCleanNickName() + " - Has Connected Cache Removal", new Runnable() {
                    @Override
                    public void run() {
                        joinLeaveCache.removeCache(player.getClientCtx());
                    }
                }, 3500, TimeUnit.MILLISECONDS);
            }
        });

        StarNub.getStarNubEventRouter().registerEventSubscription("Essentials", "Player_Disconnected", new StarNubEventHandler() {
            @Override
            public void onEvent(StarNubEvent<String> starNubEvent) {
                Player player = ((PlayerEvent) starNubEvent).getPLAYER_SESSION();
                String color = StarNub.getMessageSender().getGameColors().getDefaultServerChatColor();
                StarNub.getTask().getTaskScheduler().purgeBasedOnTaskNameSpecific("Essentials", "Essentials - Send - Player - " + ((PlayerEvent) starNubEvent).getPLAYER_SESSION().getCleanNickName() + " - Has Connected Message - 0");
                TimeCache cache = (TimeCache) joinLeaveCache.getCache(player.getClientCtx());
                if (cache != null) {
                    joinLeaveCache.removeCache(player.getClientCtx());
                } else {
                    StarNub.getMessageSender().serverBroadcast("StarNub", player,
                            player.getNickName() + color + " has disconnected.");
                    StarNub.getLogger().cInfoPrint("Essentials", player.getCleanNickName() + " has disconnected. (IP: " +
                            player.getSessionIp().toString().substring(1) + ")");
                }
            }

        });
    }
}

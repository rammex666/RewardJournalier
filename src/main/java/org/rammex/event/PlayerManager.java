package org.rammex.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.rammex.Main;
import org.rammex.commands.RewardCommand;

public class PlayerManager implements Listener {
    Main plugin;
    public PlayerManager(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();
        if(event.getPlayer().hasPlayedBefore()){
            try {
                if (plugin.getPlayerData() != null && plugin.getPlayerData().contains("players." + playerName)) {
                    Integer lasttime = plugin.getPlayerData().getInt("players." + playerName + ".lasttime");
                    RewardCommand.loadPlayTime(event.getPlayer());
                } else {
                    try{
                        if (plugin.getPlayerData() != null) {
                            plugin.getPlayerData().createSection("players." + playerName);
                            plugin.savePlayerData();
                            plugin.reloadPlayerData();
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    if (plugin.getPlayerData() != null) {
                        plugin.getPlayerData().set("players." + playerName + ".day", 1);
                        plugin.getPlayerData().set("players." + playerName + ".hasreward", false);
                        plugin.getPlayerData().set("players." + playerName + ".haspremiumreward", false);
                        plugin.getPlayerData().set("players." + playerName + ".lasttime", 0);
                        plugin.savePlayerData(); // Save the data after modifying it
                        plugin.reloadPlayerData();
                    }
                    RewardCommand.playTime.put(event.getPlayer(), System.currentTimeMillis());
                    if(event.getPlayer().hasPermission(this.plugin.getConfig().getString("premiumperm"))){
                        RewardCommand.playTimePremium.put(event.getPlayer(), System.currentTimeMillis());
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }

        } else {
            // Create a new entry for the new player
            if (plugin.getPlayerData() != null) {
                plugin.getPlayerData().set("players." + playerName + ".day", 1);
                plugin.getPlayerData().set("players." + playerName + ".hasreward", false);
                plugin.getPlayerData().set("players." + playerName + ".haspremiumreward", false);
                plugin.getPlayerData().set("players." + playerName + ".lasttime", 0);
                plugin.savePlayerData(); // Save the data after modifying it
                plugin.reloadPlayerData();
            }
            RewardCommand.playTime.put(event.getPlayer(), System.currentTimeMillis());
        }

        plugin.savePlayerData();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        RewardCommand.savePlayTime(event.getPlayer());
       RewardCommand.removePlayTime(event.getPlayer());
    }

}

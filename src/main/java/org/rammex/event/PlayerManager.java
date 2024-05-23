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
        ((RewardCommand) this.plugin.getCommand("rd").getExecutor()).loadPlayTime(event.getPlayer());
        String playerName = event.getPlayer().getName();
        if (!plugin.getPlayerData().contains("players." + playerName)) {
            plugin.getPlayerData().set("players." + playerName + ".day", 1);
            plugin.savePlayerData();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        ((RewardCommand) this.plugin.getCommand("rd").getExecutor()).savePlayTime(event.getPlayer());

    }

}

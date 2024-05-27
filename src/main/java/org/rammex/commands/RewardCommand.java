package org.rammex.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.rammex.Main;
import org.rammex.gui.RdGui;


import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardCommand implements CommandExecutor {

    private static Main plugin = null;
    public static Map<Player, Long> playTime = Collections.emptyMap();
    public static Map<Player, Long> playTimePremium = Collections.emptyMap();


    public RewardCommand(Main plugin) {
        this.plugin = plugin;
        this.playTime = new HashMap<>();
        this.playTimePremium = new HashMap<>();

    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§8[§a❌§8] Cette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0 && args[0].equalsIgnoreCase("start")) {
            if(player.hasPermission("rd.start")){
                startTracking(player);
                player.sendMessage("§8[§a✔§8] §7Vous avez commencé le suivi des récompenses.");
            } else {
                player.sendMessage("Vous n'avez pas la permission de faire cela.");
            }
        } else {
            RdGui gui = new RdGui(plugin);
            gui.openInventory(player);

        }
        return true;
    }

    private void startTracking(Player player) {
        LocalDate today = LocalDate.now();
        this.plugin.getConfig().set("DateStart",today.toString());
        this.plugin.saveConfig();

        new BukkitRunnable() {
            @Override
            public void run() {
                LocalDate today = LocalDate.now();
                LocalDate startDate = LocalDate.parse(plugin.getConfig().getString("DateStart"));
                Integer day = plugin.getConfig().getInt("JourActuel");
                long daysBetween = ChronoUnit.DAYS.between(startDate, today);
                if (daysBetween >= day+1){
                    plugin.getConfig().set("JourActuel",plugin.getConfig().getInt("JourActuel")+1);
                    resetRewards();
                    plugin.saveConfig();
                }
            }
        }.runTaskTimer(this.plugin, 0L, 24*60*60*20L);


    }

    public void checkRewardPremium(Player player, Integer day) {
        if (plugin.getPlayerData().getBoolean("players." + player.getName() + ".hasrewardpremium")) {
            player.sendMessage("§8[§a⚫§8] Vous avez déjà réclamé votre récompense pour aujourd'hui.");
            return;
        }

        if (!hasPlayed15MinutesPremium(player)) {
            return;
        }

        this.plugin.getPlayerData().set("players." + player.getName() + ".day", day + 1);
        playTimePremium.remove(player);
        playTimePremium.put(player, System.currentTimeMillis());
        if(day == this.plugin.getConfig().getInt("JourActuel")){
            this.plugin.getPlayerData().set("players." + player.getName() + ".hasrewardpremium", true);
            this.plugin.savePlayerData();
        }
        List<String> commands = this.plugin.getConfig().getStringList("rewardspremium." + day + ".commands");
        for (String command : commands) {
            command = command.replace("%player%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
        player.sendMessage("§8[§a⚫§8] Vous avez réclamé votre récompense premium du "+day+" jour.");
    }

    public void checkReward(Player player, Integer day) {
        if (plugin.getPlayerData().getBoolean("players." + player.getName() + ".hasreward")) {
            player.sendMessage("§8[§a⚫§8] Vous avez déjà réclamé votre récompense pour aujourd'hui.");
            return;
        }

        if (!hasPlayed15Minutes(player)) {
            player.sendMessage("§8[§a⚫§8] il vous reste " + (15 * day - (System.currentTimeMillis() - playTime.get(player)) / 1000 / 60) + " minutes pour réclamer votre récompense.");
            return;
        }

        this.plugin.getPlayerData().set("players." + player.getName() + ".day", day + 1);
        playTime.remove(player);
        playTime.put(player, System.currentTimeMillis());
        if(day == this.plugin.getConfig().getInt("JourActuel")){
            this.plugin.getPlayerData().set("players." + player.getName() + ".hasreward", true);
            this.plugin.savePlayerData();
        }
        List<String> commands = this.plugin.getConfig().getStringList("rewards." + day + ".commands");
        for (String command : commands) {
            command = command.replace("%player%", player.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
        player.sendMessage("§8[§a✔§8] §7Vous avez récupéré les récompense du jour "+day+".");
    }

    public static void savePlayTime(Player player) {
        long playTimeInMinutes = (System.currentTimeMillis() - playTime.get(player)) / 1000 / 60;
        plugin.getPlayerData().set("players." + player.getName() + ".lasttime", playTimeInMinutes);
    }

    public static void loadPlayTime(Player player) {
        long playTimeInMinutes = plugin.getPlayerData().getInt("players." + player.getName() + ".lasttime");
        playTime.put(player, System.currentTimeMillis() - playTimeInMinutes * 60 * 1000);
        String permissionPremium = plugin.getConfig().getString("premiumperm");
        if(permissionPremium != null && player.hasPermission(permissionPremium)){
            playTimePremium.put(player, System.currentTimeMillis() - playTimeInMinutes * 60 * 1000);
        }

    }

    public static void removePlayTime(Player player) {
        playTime.remove(player);
        if(player.hasPermission(plugin.getConfig().getString("premiumperm"))){
            playTimePremium.remove(player);
        }
    }

    public boolean hasPlayed15Minutes(Player player) {
        try{
            long currentTime = System.currentTimeMillis();
            long startTime = RewardCommand.playTime.get(player);
            long playTimeInMinutes = (currentTime - startTime) / 1000 / 60;
            return playTimeInMinutes >= 15 * this.plugin.getPlayerData().getInt("players." + player.getName() + ".day");
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public boolean hasPlayed15MinutesPremium(Player player) {
        long currentTime = System.currentTimeMillis();
        long startTime = RewardCommand.playTimePremium.get(player);
        long playTimeInMinutes = (currentTime - startTime) / 1000 / 60;
        return playTimeInMinutes >= 15 * this.plugin.getPlayerData().getInt("players." + player.getName() + ".day");
    }

    public void resetRewards() {
        ConfigurationSection playersSection = plugin.getPlayerData().getConfigurationSection("players");
        if (playersSection != null) {
            for (String playerName : playersSection.getKeys(false)) {
                plugin.getPlayerData().set("players." + playerName + ".hasreward", false);
                plugin.getPlayerData().set("players." + playerName + ".hasrewardpremium", false);
            }
            plugin.savePlayerData();
        }
    }
}
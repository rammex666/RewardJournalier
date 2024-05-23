package org.rammex.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.rammex.Main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class RewardCommand implements CommandExecutor {

    private final Main plugin;
    private final Map<Player, Long> playTime;

    public RewardCommand(Main plugin) {
        this.plugin = plugin;
        this.playTime = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Cette commande ne peut être utilisée que par un joueur.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0 && args[0].equalsIgnoreCase("start")) {
            startTracking(player);
            return true;
        }
        Integer day = plugin.getPlayerData().getInt("players." + player.getName() + ".day");
        checkReward(player, day);

        return true;
    }

    private void startTracking(Player player) {
        playTime.put(player, System.currentTimeMillis());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    return;
                }

                long playTimeInMinutes = (System.currentTimeMillis() - playTime.get(player)) / 1000 / 60;
                Path path = Paths.get(plugin.getDataFolder().getAbsolutePath(), player.getUniqueId().toString() + ".yml");

                try {
                    LocalDate lastPlayed = LocalDate.parse(new String(Files.readAllBytes(path)));
                    long daysBetween = ChronoUnit.DAYS.between(lastPlayed, LocalDate.now());

                    if (playTimeInMinutes >= (daysBetween + 1) * 15) {
                        Files.write(path, LocalDate.now().toString().getBytes());
                        player.sendMessage("Vous avez joué assez longtemps pour recevoir votre récompense aujourd'hui !");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.runTaskTimer(plugin, 0L, 20L * 60);
    }

    private void checkReward(Player player, Integer day) {
        Path path = Paths.get(plugin.getDataFolder().getAbsolutePath(), player.getUniqueId().toString() + ".yml");

        if (!Files.exists(path)) {
            player.sendMessage("Vous n'avez pas encore commencé à suivre votre temps de jeu. Utilisez /rd start pour commencer.");
            return;
        }

        try {
            LocalDate lastPlayed = LocalDate.parse(new String(Files.readAllBytes(path)));
            long daysBetween = ChronoUnit.DAYS.between(lastPlayed, LocalDate.now());

            if (daysBetween == 0) {
                player.sendMessage("Vous avez déjà reçu votre récompense aujourd'hui.");
            } else if (daysBetween > 1) {
                player.sendMessage("Vous avez manqué votre récompense hier. Vous devez jouer tous les jours pour recevoir une récompense.");
            } else {
                player.sendMessage("Vous pouvez recevoir votre récompense aujourd'hui !");
                removePlayTime(player);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePlayTime(Player player) {
        long playTimeInMinutes = (System.currentTimeMillis() - playTime.get(player)) / 1000 / 60;
        plugin.getDatabaseManager().savePlayTime(player.getUniqueId(), playTimeInMinutes);
    }

    public void loadPlayTime(Player player) {
        long playTimeInMinutes = plugin.getDatabaseManager().getPlayTime(player.getUniqueId());
        playTime.put(player, System.currentTimeMillis() - playTimeInMinutes * 60 * 1000);
    }

    public void removePlayTime(Player player) {
        plugin.getDatabaseManager().removePlayTime(player.getUniqueId());
        playTime.remove(player);
    }
}
package org.rammex;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.rammex.commands.RewardCommand;
import org.rammex.event.PlayerManager;
import org.rammex.gui.RdGui;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

public final class Main extends JavaPlugin {

    private static Main instance;
    private FileConfiguration playerData;
    private File playerDataFile;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        loadPlayerData();
        reloadPlayerData(); // Add this line
        saveDefaultConfig();

        this.getServer().getPluginManager().registerEvents(new PlayerManager(this), this);
        this.getServer().getPluginManager().registerEvents(new RdGui(this), this);
        this.getCommand("rd").setExecutor(new RewardCommand(this));
        LocalDate today = LocalDate.now();
        String startDateString = getConfig().getString("DateStart");
        LocalDate startDate = null;
        if (startDateString != null) {
            startDate = LocalDate.parse(startDateString);
        } else {
            startDate = LocalDate.now();
        }
    }

    @Override
    public void onDisable() {
    }


    private void loadPlayerData() {
        playerDataFile = new File(getDataFolder(), "playerdata.yml");
        if (!playerDataFile.exists()) {
            saveResource("playerdata.yml", false);
        }
    }

    public FileConfiguration getPlayerData() {
        return this.playerData;
    }

    public void savePlayerData() {
        if (playerData != null && getDataFolder() != null) {
            try {
                playerData.save(new File(getDataFolder(), "playerdata.yml"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Log an error message or throw an exception
            System.out.println("Error: playerData or getDataFolder() is null");
        }
    }

    public void reloadPlayerData() {
        playerData = YamlConfiguration.loadConfiguration(playerDataFile);

        Reader defConfigStream = new InputStreamReader(getResource("playerdata.yml"), StandardCharsets.UTF_8);
        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
        playerData.setDefaults(defConfig);
    }




}
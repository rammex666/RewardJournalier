package org.rammex;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.rammex.commands.RewardCommand;
import org.rammex.event.PlayerManager;
import org.rammex.utils.DatabaseManager;

import java.io.File;

public final class Main extends JavaPlugin {

    private static Main instance;
    private DatabaseManager databaseManager;
    private FileConfiguration playerData;

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        databaseManager = new DatabaseManager();
        databaseManager.connect();

        loadPlayerData();
        saveDefaultConfig();

        this.getServer().getPluginManager().registerEvents(new PlayerManager(this), this);
        this.getCommand("rd").setExecutor(new RewardCommand(this));
    }

    @Override
    public void onDisable() {
        databaseManager.disconnect();
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    private void loadPlayerData() {
        File playerDataFile = new File(getDataFolder()+"/data", "playerdata.yml");
        if (!playerDataFile.exists()) {
            saveResource("data/playerdata.yml", false);
        }
        YamlConfiguration playerdata = YamlConfiguration.loadConfiguration(playerDataFile);
    }

    public FileConfiguration getPlayerData() {
        return this.playerData;
    }

    public void savePlayerData() {
        try {
            playerData.save(new File(getDataFolder()+"/data", "playerdata.yml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
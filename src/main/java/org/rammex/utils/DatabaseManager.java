package org.rammex.utils;

import org.rammex.Main;

import java.sql.*;
import java.util.UUID;

public class DatabaseManager {
    private Connection connection;

    public void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + Main.getInstance().getDataFolder() + "/data/data.db";
            connection = DriverManager.getConnection(url);
            createTables();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void savePlayTime(UUID playerId, long playTimeInMinutes) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO play_time (player_id, play_time) VALUES (?, ?) ON CONFLICT (player_id) DO UPDATE SET play_time = ?")) {
            statement.setString(1, playerId.toString());
            statement.setLong(2, playTimeInMinutes);
            statement.setLong(3, playTimeInMinutes);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public long getPlayTime(UUID playerId) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT play_time FROM play_time WHERE player_id = ?")) {
            statement.setString(1, playerId.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getLong("play_time");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void removePlayTime(UUID playerId) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM play_time WHERE player_id = ?")) {
            statement.setString(1, playerId.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTables() {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS play_time (player_id TEXT PRIMARY KEY, play_time INTEGER)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
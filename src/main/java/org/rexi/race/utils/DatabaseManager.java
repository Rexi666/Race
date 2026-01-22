package org.rexi.race.utils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.rexi.race.Race;

import java.io.File;
import java.sql.*;

public class DatabaseManager implements Listener {

    private final JavaPlugin plugin;
    private Connection connection;

    public DatabaseManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void connect() throws SQLException {
        String type = plugin.getConfig().getString("database.type", "sqlite").toLowerCase();
        if (type.equals("mysql")) {
            String host = plugin.getConfig().getString("database.mysql.host");
            int port = plugin.getConfig().getInt("database.mysql.port");
            String db = plugin.getConfig().getString("database.mysql.database");
            String user = plugin.getConfig().getString("database.mysql.username");
            String pass = plugin.getConfig().getString("database.mysql.password");

            connection = DriverManager.getConnection(
                    "jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=false",
                    user,
                    pass
            );
        } else {
            File dbFile = new File(plugin.getDataFolder(), "data.db");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
        }

        plugin.getLogger().info("Connected to " + type.toUpperCase() + " database!");
        createTable();
    }

    private void createTable() throws SQLException {
        String sql1 = "CREATE TABLE IF NOT EXISTS player_race (" +
                "uuid VARCHAR(36) PRIMARY KEY," +
                "chosen_race TEXT" +
                ")";
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql1);
        }
    }

    public void setPlayerRace(String uuid, String chosen_race) throws SQLException {
        String sql = "REPLACE INTO player_race (uuid, chosen_race) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ps.setString(2, chosen_race);
            ps.executeUpdate();
        }
    }

    public String getPlayerRace(String uuid) throws SQLException {
        String sql = "SELECT chosen_race FROM player_race WHERE uuid = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, uuid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("chosen_race");
            }
        }
        return null;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error closing database connection: " + e.getMessage());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Race.getInstance().getPlayerRaceFromDB(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Race.getInstance().playerRace.remove(event.getPlayer().getUniqueId());
    }
}

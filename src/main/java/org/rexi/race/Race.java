package org.rexi.race;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.rexi.race.utils.DatabaseManager;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Race extends JavaPlugin {

    private DatabaseManager databaseManager;
    private static Race instance;

    public final Map<UUID, String> playerRace= new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        databaseManager = new DatabaseManager(this);
        try {
            databaseManager.connect();
        } catch (SQLException e) {
            getLogger().severe("Could not connect to database: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Bukkit.getPluginManager().registerEvents(databaseManager, this);

        Bukkit.getConsoleSender().sendMessage(Component.text("Race se ha activado correctamente").color(NamedTextColor.GREEN));
        Bukkit.getConsoleSender().sendMessage(Component.text("Gracias por usar plugins de Rexi666 :D").color(NamedTextColor.BLUE));
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }

        Bukkit.getConsoleSender().sendMessage(Component.text("Race se ha desactivado correctamente").color(NamedTextColor.RED));
        Bukkit.getConsoleSender().sendMessage(Component.text("Gracias por usar plugins de Rexi666 :D").color(NamedTextColor.BLUE));
    }

    public void setPlayerRace(Player player, String chosenRace) {
        playerRace.put(player.getUniqueId(), chosenRace);
        try {
            databaseManager.setPlayerRace(player.getUniqueId().toString(), chosenRace);
        } catch (SQLException e) {
            getLogger().severe("Failed to save player race on the database: " + e.getMessage());
        }
    }

    public String getPlayerRace(Player player) {
        return playerRace.getOrDefault(player.getUniqueId(), null);
    }
    public void getPlayerRaceFromDB(Player player) {
        try {
            String chosenRace = databaseManager.getPlayerRace(player.getUniqueId().toString());
            playerRace.put(player.getUniqueId(), chosenRace);
        } catch (SQLException e) {
            getLogger().severe("Failed to load player class on the database: " + e.getMessage());
        }
    }

    public static Race getInstance() {
        return instance;
    }
}

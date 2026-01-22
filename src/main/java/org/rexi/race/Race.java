package org.rexi.race;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;
import org.rexi.race.listeners.Tasks.VampireNightTask;
import org.rexi.race.listeners.VampireListener;
import org.rexi.race.listeners.WizardListener;
import org.rexi.race.utils.DatabaseManager;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class Race extends JavaPlugin implements Listener {

    private DatabaseManager databaseManager;
    private static Race instance;

    public final Map<UUID, String> playerRace= new HashMap<>();
    public final Map<UUID, PermissionAttachment> racePermissions = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // DATABASE
        databaseManager = new DatabaseManager(this);
        try {
            databaseManager.connect();
        } catch (SQLException e) {
            getLogger().severe("Could not connect to database: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        Bukkit.getPluginManager().registerEvents(databaseManager, this);

        // LISTENERS
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new VampireListener(), this);
        Bukkit.getPluginManager().registerEvents(new WizardListener(), this);

        // TASKS
        Bukkit.getScheduler().runTaskTimer(
                this,
                new VampireNightTask(),
                0L,
                200L // cada 10 segundos
        );

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
        applyRacePermissions(player);
        try {
            databaseManager.setPlayerRace(player.getUniqueId().toString(), chosenRace);
        } catch (SQLException e) {
            getLogger().severe("Failed to save player race on the database: " + e.getMessage());
        }
    }

    public String getPlayerRace(Player player) {
        return playerRace.getOrDefault(player.getUniqueId(), "NONE");
    }

    // vampire
    // wizard

    public void loadPlayerRaceFromDB(Player player) {
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

    public void applyRacePermissions(Player player) {

        // Limpiar permisos anteriores
        PermissionAttachment old = Race.getInstance().racePermissions.remove(player.getUniqueId());
        if (old != null) {
            player.removeAttachment(old);
        }

        String race = Race.getInstance().getPlayerRace(player);
        if (race == null || !race.equalsIgnoreCase("wizard")) return;

        PermissionAttachment attachment = player.addAttachment(this);

        attachment.setPermission("race.wizard", true);
        Race.getInstance().racePermissions.put(player.getUniqueId(), attachment);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) { // JOIN EN DB
        Player player = event.getPlayer();

        PermissionAttachment attachment = Race.getInstance().racePermissions.remove(player.getUniqueId());
        if (attachment != null) {
            player.removeAttachment(attachment);
        }
    }
}

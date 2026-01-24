package org.rexi.race;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;
import org.rexi.race.commands.RaceCommand;
import org.rexi.race.listeners.AssassinListener;
import org.rexi.race.listeners.TankListener;
import org.rexi.race.listeners.Tasks.RacesTasks;
import org.rexi.race.listeners.VampireListener;
import org.rexi.race.listeners.WizardListener;
import org.rexi.race.menu.RaceMenu;
import org.rexi.race.menu.RaceMenuListener;
import org.rexi.race.utils.DatabaseManager;

import java.sql.SQLException;
import java.util.*;

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
        Bukkit.getPluginManager().registerEvents(new AssassinListener(), this);
        Bukkit.getPluginManager().registerEvents(new TankListener(), this);
        Bukkit.getPluginManager().registerEvents(new RaceMenuListener(this), this);

        // COMANDOS
        RaceCommand raceCommand = new RaceCommand(this);
        this.getCommand("race").setExecutor(raceCommand);
        this.getCommand("race").setTabCompleter(raceCommand);

        // TASKS
        Bukkit.getScheduler().runTaskTimer(
                this,
                new RacesTasks(),
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
    // assassin
    // tank

    public void loadPlayerRaceFromDB(Player player) {
        try {
            String chosenRace = databaseManager.getPlayerRace(player.getUniqueId().toString());

            if (chosenRace == null || chosenRace.isBlank()) {
                chosenRace = "NONE";
            }

            playerRace.put(player.getUniqueId(), chosenRace);
        } catch (SQLException e) {
            getLogger().severe("Failed to load player class on the database: " + e.getMessage());
            playerRace.put(player.getUniqueId(), "NONE");
        }
    }

    public static Race getInstance() {
        return instance;
    }

    public void applyRacePermissions(Player player) {

        // Limpiar permisos anteriores
        PermissionAttachment old = racePermissions.remove(player.getUniqueId());
        if (old != null) {
            player.removeAttachment(old);
        }

        String race = getPlayerRace(player);
        if (race == null || !race.equalsIgnoreCase("wizard")) return;

        PermissionAttachment attachment = player.addAttachment(this);

        attachment.setPermission("race.wizard", true);
        racePermissions.put(player.getUniqueId(), attachment);
    }

    private final Set<UUID> waitingForPack = new HashSet<>();
    public final Set<UUID> raceMenuOpen = new HashSet<>();

    public boolean hasRace(Player player) {
        String race = getPlayerRace(player);
        return race != null && !race.equalsIgnoreCase("NONE");
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        loadPlayerRaceFromDB(player);

        if (!hasRace(player)) {
            waitingForPack.add(player.getUniqueId());
        } else {
            applyRacePermissions(player);
        }
    }

    @EventHandler
    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();

        if (!waitingForPack.contains(player.getUniqueId())) return;

        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED
                || event.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED
                || event.getStatus() == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {

            waitingForPack.remove(player.getUniqueId());

            if (!hasRace(player)) {
                Bukkit.getScheduler().runTask(this, () -> {
                    if (!raceMenuOpen.contains(player.getUniqueId())) {
                        raceMenuOpen.add(player.getUniqueId());
                        RaceMenu.open(player);
                    }
                });
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        playerRace.remove(uuid);
        raceMenuOpen.remove(uuid);
        waitingForPack.remove(uuid);

        PermissionAttachment attachment = racePermissions.remove(uuid);
        if (attachment != null) {
            player.removeAttachment(attachment);
        }
    }
}

package org.rexi.race.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.rexi.race.Race;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RaceCommand implements CommandExecutor, TabCompleter {

    private final Race plugin;

    public RaceCommand(Race plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String label,
            @NotNull String[] args
    ) {

        // Permiso o consola
        if (sender instanceof Player player && !player.hasPermission("race.admin")) {
            player.sendMessage(deserialize("&cNo tienes permiso para usar este comando."));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(deserialize("&eUso: /race set <jugador> <raza>"));
            sender.sendMessage(deserialize("&eUso: /race menu <jugador>"));
            return true;
        }

    /* =====================
       /race menu <jugador>
       ===================== */
        if (args[0].equalsIgnoreCase("menu")) {

            if (args.length != 2) {
                sender.sendMessage(deserialize("&eUso: /race menu <jugador>"));
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(deserialize("&cEl jugador no está conectado."));
                return true;
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                org.rexi.race.menu.RaceMenu.open(target);
            });

            sender.sendMessage(deserialize("&aMenú de razas abierto para &e" + target.getName()));
            return true;
        }

    /* =====================
       /race set <jugador> <raza>
       ===================== */
        if (!args[0].equalsIgnoreCase("set") || args.length != 3) {
            sender.sendMessage(deserialize("&eUso: /race set <jugador> <raza>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(deserialize("&cEl jugador no está conectado."));
            return true;
        }

        String race = args[2].toLowerCase();

        List<String> validRaces = Arrays.asList("vampire", "wizard", "assassin", "tank");
        if (!validRaces.contains(race)) {
            sender.sendMessage(deserialize("&cRaza inválida. Disponibles: " + String.join(", ", validRaces)));
            return true;
        }

        plugin.setPlayerRace(target, race);

        String raceName = plugin.getConfig().getString("races."+race, race);

        sender.sendMessage(deserialize("&aHas establecido la raza de &e" + target.getName() + " &aa &b" + raceName));
        target.sendMessage(deserialize("&aTu raza ha sido cambiada a &b" + raceName));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(
            @NotNull CommandSender sender,
            @NotNull Command command,
            @NotNull String alias,
            @NotNull String[] args
    ) {

        if (!sender.hasPermission("race.admin")) return Collections.emptyList();

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("set", "menu"));
        }

        if (args.length == 2 && (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("menu"))) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                completions.add(p.getName());
            }
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            completions.addAll(Arrays.asList("vampire", "wizard", "assassin", "tank"));
        }

        return completions;
    }

    private static final LegacyComponentSerializer LEGACY_HEX_SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors() // Habilita el soporte de hex
            .useUnusualXRepeatedCharacterHexFormat() // Soporta &x&r&r&g&g&b&b
            .build();

    public static Component deserialize(String input) {
        return LEGACY_HEX_SERIALIZER.deserialize(input);
    }
}

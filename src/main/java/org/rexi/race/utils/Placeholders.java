package org.rexi.race.utils;

import org.bukkit.entity.Player;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.rexi.race.Race;

public class Placeholders extends PlaceholderExpansion {

    private final Race plugin;

    public Placeholders(Race plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getAuthor() {
        return "Rexi666";
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String getIdentifier() {
        return "race";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) return "";

        if (identifier.equalsIgnoreCase("chosen")) {
            String race = plugin.getPlayerRace(player);

            if (race == null || race.equalsIgnoreCase("NONE") || race.isEmpty()) {
                return "";
            }

            return plugin.getConfig().getString("races." + race, race);
        }

        return null;
    }
}

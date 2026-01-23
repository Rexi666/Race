package org.rexi.race.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.rexi.race.Race;

import java.util.Set;

public class WizardListener implements Listener {

    @EventHandler
    public void onPhysicalDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!Race.getInstance().getPlayerRace(player).equalsIgnoreCase("wizard")) return;

        if (!(event.getDamager() instanceof Player)) return;

        event.setDamage(event.getDamage() * 1.3);
    }
}

package org.rexi.race.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.rexi.race.Race;

public class TankListener implements Listener {

    private static final double DAMAGE_RECEIVED_MULTIPLIER = 0.7; // 30% menos daño
    private static final double DAMAGE_DEALT_MULTIPLIER = 0.7;

    @EventHandler
    public void onTankDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player damager)) return;
        if (!(event.getEntity() instanceof Player victim)) return;

        String victimRace = Race.getInstance().getPlayerRace(victim);
        String damagerRace = Race.getInstance().getPlayerRace(damager);

        if (victimRace.equalsIgnoreCase("tank") && damagerRace.equalsIgnoreCase("tank")) {
            return;
        }

        double damage = event.getDamage();

        if (victimRace.equalsIgnoreCase("tank")) {
            damage *= DAMAGE_RECEIVED_MULTIPLIER;
        } else if (damagerRace.equalsIgnoreCase("tank")) {
            damage *= DAMAGE_DEALT_MULTIPLIER;
        }

        event.setDamage(damage);
    }
}

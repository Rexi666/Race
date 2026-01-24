package org.rexi.race.listeners;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.rexi.race.Race;

public class VampireListener implements Listener {

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity().getKiller() instanceof Player player)) return;
        if (!Race.getInstance().getPlayerRace(player).equalsIgnoreCase("vampire")) return;

        double healAmount = 5.0; // Coras para curarse, en mitades

        double newHealth = Math.min(
                player.getHealth() + healAmount,
                player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue()
        );

        player.setHealth(newHealth);
    }

    @EventHandler
    public void onFireDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!Race.getInstance().getPlayerRace(player).equalsIgnoreCase("vampire")) return;

        EntityDamageEvent.DamageCause cause = event.getCause();

        if (cause == EntityDamageEvent.DamageCause.FIRE ||
                cause == EntityDamageEvent.DamageCause.FIRE_TICK ||
                cause == EntityDamageEvent.DamageCause.LAVA) {

            event.setDamage(event.getDamage() * 1.5); // +50% daño
        }
    }
}
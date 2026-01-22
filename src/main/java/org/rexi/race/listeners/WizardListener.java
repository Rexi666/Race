package org.rexi.race.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.rexi.race.Race;

import java.util.Set;

public class WizardListener implements Listener {

    private static final Set<EntityDamageEvent.DamageCause> PHYSICAL_DAMAGE = Set.of(
            EntityDamageEvent.DamageCause.ENTITY_ATTACK,
            EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK,
            EntityDamageEvent.DamageCause.PROJECTILE,
            EntityDamageEvent.DamageCause.FALL,
            EntityDamageEvent.DamageCause.ENTITY_EXPLOSION,
            EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
    );

    @EventHandler
    public void onPhysicalDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (!Race.getInstance().getPlayerRace(player).equalsIgnoreCase("wizard")) return;

        if (PHYSICAL_DAMAGE.contains(event.getCause())) {
            event.setDamage(event.getDamage() * 1.3); // +30% daño
        }
    }
}

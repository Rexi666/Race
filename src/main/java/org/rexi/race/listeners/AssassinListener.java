package org.rexi.race.listeners;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.util.Vector;
import org.rexi.race.Race;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AssassinListener implements Listener {

    private final Map<UUID, Long> lastHitTime = new HashMap<>();

    // Tiempo máximo entre golpes para contar como "rápido" (ms)
    private static final long FAST_HIT_WINDOW = 400; // 0.4s

    private static final double CRIT_MULTIPLIER = 1.5; // +50% daño
    private static final double BACKSTAB_MULTIPLIER = 1.5; // +50% daño por ataque por la espalda

    @EventHandler
    public void onAssassinHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!Race.getInstance().getPlayerRace(player).equalsIgnoreCase("assassin")) return;

        Entity victim = event.getEntity();
        if (!(victim instanceof Player victim_player)) return;

        double damage = event.getDamage();

        // Golpes rápidos
        long now = System.currentTimeMillis();
        long last = lastHitTime.getOrDefault(player.getUniqueId(), 0L);

        if (now - last <= FAST_HIT_WINDOW) {
            damage *= CRIT_MULTIPLIER;
            spawnCritEffects(victim_player);
        }

        // Backstab (ataque por la espalda)
        if (isBackstab(player, victim_player)) {
            damage *= BACKSTAB_MULTIPLIER;
            spawnBackstabEffects(victim_player);
        }

        lastHitTime.put(player.getUniqueId(), now);
        event.setDamage(damage);
    }

    private boolean isBackstab(Player attacker, Player victim) {

        Vector victimDirection = victim.getLocation().getDirection().normalize();
        Vector attackerDirection = attacker.getLocation()
                .toVector()
                .subtract(victim.getLocation().toVector())
                .normalize();

        double dot = victimDirection.dot(attackerDirection);

        return dot > 0.5; // cuanto más alto, más estricto
    }

    private void spawnCritEffects(LivingEntity entity) {
        entity.getWorld().spawnParticle(
                Particle.CRIT,
                entity.getLocation().add(0, 1, 0),
                15,
                0.3, 0.4, 0.3,
                0.1
        );
        entity.getWorld().playSound(
                entity.getLocation(),
                Sound.ENTITY_PLAYER_ATTACK_CRIT,
                1f,
                1.2f
        );
    }

    private void spawnBackstabEffects(LivingEntity entity) {
        entity.getWorld().spawnParticle(
                Particle.DAMAGE_INDICATOR,
                entity.getLocation().add(0, 1, 0),
                10,
                0.2, 0.3, 0.2,
                0.1
        );
        entity.getWorld().playSound(
                entity.getLocation(),
                Sound.ENTITY_PLAYER_ATTACK_STRONG,
                1f,
                0.8f
        );
    }

    @EventHandler
    public void onAssassinReceiveDamage(EntityDamageByEntityEvent event) {

        if (!(event.getEntity() instanceof Player player)) return;
        if (!Race.getInstance().getPlayerRace(player).equalsIgnoreCase("assassin")) return;

        // Solo daño cuerpo a cuerpo
        if (!(event.getDamager() instanceof Player)) return;

        double damage = event.getDamage();

        double WEAKNESS_MULTIPLIER = 1.3; // +30% daño recibido
        event.setDamage(damage * WEAKNESS_MULTIPLIER);
    }
}

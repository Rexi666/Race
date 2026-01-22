package org.rexi.race.listeners.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.rexi.race.Race;

public class VampireNightTask implements Runnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {

            if (!Race.getInstance().getPlayerRace(player).equalsIgnoreCase("vampire")) {
                continue;
            }

            World world = player.getWorld();
            long time = world.getTime();
            boolean isNight = time >= 13000 && time <= 23000;
            if (world.getEnvironment() != World.Environment.NORMAL) {
                applyEffects(player);
                continue;
            }

            if (isNight) {
                applyEffects(player);
            } else {
                removeEffects(player);
            }
        }
    }

    private void applyEffects(Player player) {
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.SPEED,
                250,
                0,   // Speed I
                true,
                false
        ));

        player.addPotionEffect(new PotionEffect(
                PotionEffectType.STRENGTH,
                250,
                0, // Strength I
                true,
                false
        ));
    }

    private void removeEffects(Player player) {
        player.removePotionEffect(PotionEffectType.SPEED);
        player.removePotionEffect(PotionEffectType.STRENGTH);
    }
}

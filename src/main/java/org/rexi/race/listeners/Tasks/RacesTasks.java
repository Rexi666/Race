package org.rexi.race.listeners.Tasks;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.rexi.race.Race;

public class RacesTasks implements Runnable {

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {

            String race = Race.getInstance().getPlayerRace(player);

            if (race.equalsIgnoreCase("vampire")) {
                World world = player.getWorld();
                long time = world.getTime();
                boolean isNight = time >= 13000 && time <= 23000;
                if (world.getEnvironment() != World.Environment.NORMAL) {
                    applyEffectsVampire(player);
                    continue;
                }

                if (isNight) {
                    applyEffectsVampire(player);
                }
            } else if (race.equalsIgnoreCase("assassin")) {
                applyEffectsAssassin(player);
            } else if (race.equalsIgnoreCase("tank")) {
                applyEffectsTank(player);
            }
        }
    }

    private void applyEffectsVampire(Player player) {
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.SPEED,
                250, // 11s
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

    private void applyEffectsAssassin(Player player) {
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.SPEED,
                250,
                0,   // Speed I
                true,
                false
        ));
    }

    private void applyEffectsTank(Player player) {
        player.addPotionEffect(new PotionEffect(
                PotionEffectType.SLOWNESS,
                250,
                0,   // Slowness I
                true,
                false
        ));
    }
}

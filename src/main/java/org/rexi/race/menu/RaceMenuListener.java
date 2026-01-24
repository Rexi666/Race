package org.rexi.race.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.rexi.race.Race;

import java.awt.*;

public class RaceMenuListener implements Listener {

    private final Race plugin;

    public RaceMenuListener(Race plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getClickedInventory() == null) return;
        if (event.getCurrentItem() == null) return;
        if (event.getClickedInventory() != event.getView().getTopInventory()) return;

        String title = PLAIN.serialize(event.getView().title());
        String menuTitle = PLAIN.serialize(deserialize(RaceMenu.TITLE));

        if (!title.equalsIgnoreCase(menuTitle)) return;

        event.setCancelled(true);

        ItemStack item = event.getCurrentItem();
        if (item == null) return;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        if (!pdc.has(RaceMenu.RACE_KEY, PersistentDataType.STRING)) return;

        String race = pdc.get(RaceMenu.RACE_KEY, PersistentDataType.STRING);
        if (race == null) return;

        plugin.setPlayerRace(player, race);

        player.closeInventory();
        player.sendMessage(deserialize("&aHas elegido la raza &e" + race + "&a correctamente."));
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        String title = PLAIN.serialize(event.getView().title());
        String menuTitle = PLAIN.serialize(deserialize(RaceMenu.TITLE));

        if (!title.equalsIgnoreCase(menuTitle)) return;

        String race = plugin.getPlayerRace(player);

        if (race != null && !race.equalsIgnoreCase("NONE")) return;

        player.sendMessage(deserialize("&cTienes que elegir una raza para continuar."));

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            RaceMenu.open((Player) event.getPlayer());
        }, 1L);
    }

    private static final PlainTextComponentSerializer PLAIN = PlainTextComponentSerializer.plainText();

    private static final LegacyComponentSerializer LEGACY_HEX_SERIALIZER = LegacyComponentSerializer.builder()
            .character('&')
            .hexColors() // Habilita el soporte de hex
            .useUnusualXRepeatedCharacterHexFormat() // Soporta &x&r&r&g&g&b&b
            .build();

    public static Component deserialize(String input) {
        return LEGACY_HEX_SERIALIZER.deserialize(input);
    }
}

package org.rexi.race.menu;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.rexi.race.Race;

import java.util.ArrayList;
import java.util.List;

public class RaceMenu {

    public static final String TITLE = "&8Elige tu raza";

    public static final NamespacedKey RACE_KEY =
            new NamespacedKey(Race.getInstance(), "race");

    public static void open(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, deserialize(TITLE));

        List<String> generalLore = List.of(
                "",
                "&7Selecciona una raza para adquirir",
                "&7sus habilidades y características."
        );

        inv.setItem(10, createItem(Material.REDSTONE, "&cVampiro", "vampire", generalLore));
        inv.setItem(12, createItem(Material.BOOK, "&9Mago", "wizard", generalLore));
        inv.setItem(14, createItem(Material.IRON_SWORD, "&7Asesino", "assassin", generalLore));
        inv.setItem(16, createItem(Material.SHIELD, "&aTanque", "tank", generalLore));

        player.openInventory(inv);
    }

    private static ItemStack createItem(Material material, String name, String race, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        meta.displayName(deserialize(name));

        if (lore != null && !lore.isEmpty()) {
            List<Component> loreComponents = new ArrayList<>();
            for (String line : lore) {
                loreComponents.add(deserialize(line));
            }
            meta.lore(loreComponents);
        }

        meta.getPersistentDataContainer().set(
                RACE_KEY,
                PersistentDataType.STRING,
                race.toLowerCase()
        );

        item.setItemMeta(meta);
        return item;
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

package de.kevin.knockffa.inventory;

import de.kevin.knockffa.KnockFFA;
import de.kevin.knockffa.MapHandler;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import static de.kevin.knockffa.inventory.InventoryHelper.*;

public class VotingChangeInventoryHandler implements Listener {
    static Inventory inventory;
    private static KnockFFA knockFFA;

    public VotingChangeInventoryHandler(KnockFFA knockFFA) {
        VotingChangeInventoryHandler.knockFFA = knockFFA;
    }

    static final String TITLE = "§6KnockFFA §7-> §eMapwechsel";

    public static Inventory createInventory(Player holder) {
        inventory = InventoryHelper.createInventory(TITLE, 4, true);

        inventory.setItem(0, getBack());
        inventory.setItem(4, createItem(Material.PAPER, 0, 1, "§e§lMapwechsel"));

        for (int i = 10; i < 26; i++) {
            if (i % 9 == 8) {
                i++;
                continue;
            }
            inventory.setItem(i, new ItemStack(Material.AIR));
        }
        int number = 1;
        for (MapHandler map : MapHandler.MapSetter.maps) {
            ItemStack item = createItem(Material.MAP, 0, 1, "§9" + number + ": " + map.getMapName());
            ItemMeta meta = item.getItemMeta();
            if (map.equals(MapHandler.MapSetter.activeMap))
                meta.addEnchant(Enchantment.KNOCKBACK, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
            inventory.addItem(item);
            number++;
            if (number == 15) break;
        }
        for (int i = 10; i < 26; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, createItem(Material.EMPTY_MAP, 0, 1, "§7" + number + ": -"));
                number++;
            }
        }

        return getInventory();
    }

    public static Inventory getInventory() {
        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        if (inv == null) return;
        if (!inv.getTitle().equals(TITLE)) return;
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        e.setCancelled(true);
        if (e.getCurrentItem() == null) return;
        ItemStack item = e.getCurrentItem();
        if (isFiller(item)) return;
        if (isBack(item)) p.openInventory(StartInventoryHandler.getInventory());

        if (item.getType().equals(Material.MAP)) {
            MapHandler.MapSetter.changeMap(MapHandler.MapSetter.getMapByName(item.getItemMeta().getDisplayName().split(": ")[1]));

        }

    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {

    }
}

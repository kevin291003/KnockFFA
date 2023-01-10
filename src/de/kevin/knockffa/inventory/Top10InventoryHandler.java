package de.kevin.knockffa.inventory;

import de.kevin.knockffa.KnockFFA;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

import static de.kevin.knockffa.inventory.InventoryHelper.*;

public class Top10InventoryHandler implements Listener {

    static Inventory inventory;
    private static KnockFFA knockFFA;

    public Top10InventoryHandler(KnockFFA knockFFA) {
        Top10InventoryHandler.knockFFA = knockFFA;
    }

    static final String TITLE = "§6KnockFFA §7-> §eTop 10";

    public static Inventory createInventory() {
        inventory = InventoryHelper.createInventory(TITLE, 4, true);

        inventory.setItem(0, getBack());

        inventory.setItem(4, createTexturedSkull(HEADS.TOP10, "§e§l§nTop 10", 1));

        List<Map<String, Object>> l = knockFFA.getDB().getTop10AsList();
        int i = 0;
        int position = 11;
        for (Map<String, Object> item : l) {
            inventory.setItem(position, createSkull(item.get("username").toString(), item.get("username").toString(), i + 1, "§7Punkte:", "§7" + item.get("score")));
            if ( (i + 1) % 5 == 0) {
                position += 4;
            }
            position++;
            i++;
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


    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {

    }

}

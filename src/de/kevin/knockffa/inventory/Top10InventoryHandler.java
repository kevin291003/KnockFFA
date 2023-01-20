package de.kevin.knockffa.inventory;

import de.kevin.knockffa.KnockFFA;
import de.kevin.knockffa.Message;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
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

    static final String TITLE = "§6KnockFFA §7-> " + Message.getMessage("inventories.top10.title");

    public static Inventory createInventory() {
        inventory = InventoryHelper.createInventory(TITLE, 4, true);

        inventory.setItem(0, getBack());

        inventory.setItem(4, createTexturedSkull(HEADS.TOP10, Message.getMessage("inventories.main.items.top10.item"), 1));

        List<Map<String, Object>> l = knockFFA.getDB().getTop10AsList();
        int i = 0;
        int position = 11;
        for (Map<String, Object> item : l) {
            inventory.setItem(position, createSkull(item.get("username").toString(), item.get("username").toString(), i + 1, Message.getMessage("inventories.top10.user_score1").replace("{score}", item.get("score").toString()), Message.getMessage("inventories.top10.user_score2").replace("{score}", item.get("score").toString())));
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

}

package de.kevin.knockffa.inventory;

import de.kevin.knockffa.KnockFFA;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;


import static de.kevin.knockffa.inventory.InventoryHelper.*;

public class Template implements Listener {

    static Inventory inventory;
    private static KnockFFA knockFFA;

    public Template(KnockFFA knockFFA) {
        Template.knockFFA = knockFFA;
    }

    static final String TITLE = "§6KnockFFA §7-> §e"; // TODO: Inventory Title

    public static Inventory createInventory() {
        inventory = InventoryHelper.createInventory(TITLE, 4, true);

        // TODO: Fill inventory

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
        // TODO: Handle other clicks
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {

    }

}

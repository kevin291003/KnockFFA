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

import static de.kevin.knockffa.inventory.InventoryHelper.*;

public class CommandsInventoryHandler implements Listener {
    static Inventory inventory;
    private static KnockFFA knockFFA;

    public static class CommandItems {

        public static ItemStack Top10;

    }

    public CommandsInventoryHandler(KnockFFA knockFFA) {
        CommandsInventoryHandler.knockFFA = knockFFA;
    }

    static final String TITLE = "§6KnockFFA §7-> §eBefehle";

    public static Inventory createInventory() {
        inventory = InventoryHelper.createInventory(TITLE, 4, true);

        inventory.setItem(4, createItem(Material.COMMAND, 0, 1, "§e§l§nBefehle"));

        inventory.setItem(11, CommandItems.Top10 = createItem(Material.STICK, 0, 1, "§e§l/top10"));

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

        if (item.equals(CommandItems.Top10)) {
            p.closeInventory();
            p.performCommand("top10");
        }

        // TODO: Handle other clicks
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {

    }
}

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
        public static ItemStack Voting;

    }

    public CommandsInventoryHandler(KnockFFA knockFFA) {
        CommandsInventoryHandler.knockFFA = knockFFA;
    }

    static final String TITLE = "§6KnockFFA §7-> §eBefehle";

    public static Inventory createInventory(Player holder) {
        inventory = InventoryHelper.createInventory(holder, TITLE, 4, true);

        inventory.setItem(4, createItem(Material.COMMAND, 0, 1, "§e§l§nBefehle"));

        inventory.setItem(11, CommandItems.Top10 = createItem(Material.STICK, 0, 1, "§e§l/top10"));

        if (holder.hasPermission("knockffa.map.voting.see")) {
            inventory.setItem(inventory.getSize() - 1, CommandItems.Voting = createItem(Material.PAPER, 0, 1, "§e§lVoting"));
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

        if (item.equals(CommandItems.Top10)) {
            p.closeInventory();
            p.performCommand("top10");
        }
        if (item.equals(CommandItems.Voting)) {
            p.closeInventory();
            p.openInventory(VotingInventoryHandler.createInventory(p));
        }

        // TODO: Handle other clicks
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {

    }
}

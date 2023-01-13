package de.kevin.knockffa.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static de.kevin.knockffa.inventory.InventoryHelper.*;

public class StartInventoryHandler implements Listener {

    static Inventory inventory;
    static final String TITLE = "§6KnockFFA §7-> §eStartseite";
    private static ItemStack CHOICE_TOP10;
    private static ItemStack CHOICE_COMMANDS;
    private static ItemStack CHOICE_KITS;

    public static Inventory createInventory() {
        inventory = InventoryHelper.createInventory(TITLE, 1, true);

        inventory.setItem(2, CHOICE_COMMANDS = createItem(Material.COMMAND, 0 ,1, "§e§l§nBefehle", "§7Liste alle Befehle auf."));
        inventory.setItem(4, CHOICE_TOP10 = createTexturedSkull(HEADS.TOP10, "§e§l§nTop 10", 1, "§7Zeige dir die Top 10", "§7Spieler aus KnockFFA an."));
        inventory.setItem(6, CHOICE_KITS = createItem(Material.CHEST, 0, 1, "§e§l§nKlassen", "§7Wähle eine Klasse", "§7zum Kämpfen aus."));

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

        if (item.equals(CHOICE_TOP10)) {
            p.openInventory(Top10InventoryHandler.createInventory());
            return;
        }
        if (item.equals(CHOICE_COMMANDS)) {
            p.openInventory(CommandsInventoryHandler.createInventory(p));
            return;
        }
        if (item.equals(CHOICE_KITS)) {
            p.openInventory(KitInventoryHandler.createInventory(p));
            return;
        }
        // TODO: Handle other clicks
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {

    }

}

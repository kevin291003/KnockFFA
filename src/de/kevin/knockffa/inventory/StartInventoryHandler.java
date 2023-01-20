package de.kevin.knockffa.inventory;

import de.kevin.knockffa.Message;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static de.kevin.knockffa.inventory.InventoryHelper.*;

public class StartInventoryHandler implements Listener {

    static Inventory inventory;
    static final String TITLE = "§6KnockFFA §7-> " + Message.getMessage("inventories.main.title");
    private static ItemStack CHOICE_TOP10;
    private static ItemStack CHOICE_COMMANDS;
    private static ItemStack CHOICE_KITS;

    public static Inventory createInventory() {
        inventory = InventoryHelper.createInventory(TITLE, 1, true);

        inventory.setItem(2, CHOICE_COMMANDS = createItem(Material.COMMAND, 0 ,1, Message.getMessage("inventories.main.items.commands.item"), Message.getMessage("inventories.main.items.commands.info")));
        inventory.setItem(4, CHOICE_TOP10 = createTexturedSkull(HEADS.TOP10, Message.getMessage("inventories.main.items.top10.item"), 1, Message.getMessage("inventories.main.items.top10.info1"), Message.getMessage("inventories.main.items.top10.info2")));
        inventory.setItem(6, CHOICE_KITS = createItem(Material.CHEST, 0, 1, Message.getMessage("inventories.main.items.kits.item"), Message.getMessage("inventories.main.items.kits.info1"), Message.getMessage("inventories.main.items.kits.info2")));

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

}

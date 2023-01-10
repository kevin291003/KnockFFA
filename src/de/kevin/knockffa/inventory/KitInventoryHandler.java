package de.kevin.knockffa.inventory;

import de.kevin.knockffa.KnockFFA;
import de.kevin.knockffa.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static de.kevin.knockffa.inventory.InventoryHelper.*;

public class KitInventoryHandler implements Listener {
    static Inventory inventory;
    private static KnockFFA knockFFA;

    private static ItemStack KIT_STANDARD;

    public KitInventoryHandler(KnockFFA knockFFA) {
        KitInventoryHandler.knockFFA = knockFFA;
    }

    static final String TITLE = "§6KnockFFA §7-> §eKlassen"; // TODO: Inventory Title

    public static Inventory createInventory() {
        inventory = InventoryHelper.createInventory(TITLE, 5, true);

        inventory.setItem(0, getBack());

        inventory.setItem(8*3, KIT_STANDARD = createItem(Material.STICK, 0, 1, "Standard"));

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

        if (sameKit(item, KIT_STANDARD)) {
            Utils.sendTitle(p, item.getItemMeta().getDisplayName(), "§eKlasse gewählt", 20, 20*2, 20);
            p.closeInventory();
            // TODO: Last seen
        }

        // TODO: Handle other clicks
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {

    }

    public boolean sameKit(ItemStack itemStack1, ItemStack itemStack2) {
        return itemStack1.getItemMeta().getDisplayName().equals(itemStack2.getItemMeta().getDisplayName());
    }
}

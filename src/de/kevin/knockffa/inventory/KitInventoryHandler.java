package de.kevin.knockffa.inventory;

import de.kevin.knockffa.KnockFFA;
import de.kevin.knockffa.Utils;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import static de.kevin.knockffa.inventory.InventoryHelper.*;

public class KitInventoryHandler implements Listener {
    static Inventory inventory;
    private static KnockFFA knockFFA;

    private static ItemStack KIT_STANDARD;
    private static ItemStack KIT_TEST;

    public KitInventoryHandler(KnockFFA knockFFA) {
        KitInventoryHandler.knockFFA = knockFFA;
    }

    static final String TITLE = "§6KnockFFA §7-> §eKlassen";

    public static Inventory createInventory(Player holder) {
        inventory = InventoryHelper.createInventory(holder, TITLE, 5, true);
        inventory.setItem(0, getBack());

        inventory.setItem(9*2 + 1, KIT_STANDARD =
                createItem(Material.STICK, 0, 1, "§8Standard", getLore(holder, "Standard", 0)));
        inventory.setItem(9*2 + 2, KIT_TEST =
                createItem(Material.STICK, 0, 2, "§8Test", getLore(holder, "Test", 10)));

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
            KIT_STANDARD(p);
            // TODO: Give player kit Standard
        }
        if (sameKit(item, KIT_TEST)) {
            Utils.sendTitle(p, item.getItemMeta().getDisplayName(), "§eKlasse gewählt", 20, 20*2, 20);
            p.closeInventory();
            // TODO: Give player kit Test
        }

        // TODO: Handle other clicks
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {

    }

    public boolean sameKit(ItemStack itemStack1, ItemStack itemStack2) {
        return itemStack1.getItemMeta().getDisplayName().equals(itemStack2.getItemMeta().getDisplayName());
    }

    public ItemStack[] getKit() {
        return null;
    }

    public void KIT_STANDARD(Player p) {
        Inventory inv = resetInventory(p);

        ItemStack itemStack = createItem(Material.STICK, 0, 1, "§7Standard");
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addEnchant(Enchantment.KNOCKBACK, 1, false);
        itemStack.setItemMeta(itemMeta);

        inv.addItem(itemStack);

        setKit(p, "Standard");
    }

    public Inventory resetInventory(Player p) {
        p.getInventory().clear();
        return p.getInventory();
    }

    public static String[] getLore(OfflinePlayer p, String kit, int coins) {
        if (!knockFFA.getDB().hasKit(p, kit))
            return new String[]{"§c", "§cDu besitzt diese Klasse nicht.", "§cPreis: " + coins + " Coins", "§cKlicke zum kaufen."};
        return new String[]{"§a", "§aWähle diese Klasse."};
    }

    public static void setKit(Player p, String kit) {
        p.setMetadata("active_kit", new FixedMetadataValue(knockFFA, kit));
    }

    public String getKit(Player p) {
        return p.getMetadata("active_kit").get(0).asString();
    }

    @EventHandler
    public void onSpawn(PlayerRespawnEvent e) {
        getKit(e.getPlayer());
        KIT_STANDARD(e.getPlayer());
    }

}

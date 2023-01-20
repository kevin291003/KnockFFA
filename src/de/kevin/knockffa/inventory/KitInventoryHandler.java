package de.kevin.knockffa.inventory;

import de.kevin.knockffa.KnockFFA;
import de.kevin.knockffa.Message;
import de.kevin.knockffa.Utils;
import de.kevin.knockffa.database.Database;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import static de.kevin.knockffa.inventory.InventoryHelper.*;

public class KitInventoryHandler implements Listener {
    static Inventory inventory;
    private static KnockFFA knockFFA;
    private Database db;

    private static ItemStack KIT_STANDARD;
    private static ItemStack KIT_TEST;

    public KitInventoryHandler(KnockFFA knockFFA) {
        KitInventoryHandler.knockFFA = knockFFA;
        db = knockFFA.getDB();
    }

    static final String TITLE = "§6KnockFFA §7-> " + Message.getMessage("inventories.kits.title");

    public static Inventory createInventory(Player holder) {
        inventory = InventoryHelper.createInventory(holder, TITLE, 5, true);
        inventory.setItem(0, getBack());

        inventory.setItem(9*2 + 1, KIT_STANDARD =
                createItem(Material.STICK, 0, 1, Message.getMessage("inventories.kits.kit1.name"), getLore(holder, "Standard", 0)));
        inventory.setItem(9*2 + 2, KIT_TEST =
                createItem(Material.STICK, 0, 2, Message.getMessage("inventories.kits.kit2.name"), getLore(holder, "Test", 10)));

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
            Utils.sendTitle(p, item.getItemMeta().getDisplayName(), Message.getMessage("inventories.kits.kit_chosen"), 20, 20*2, 20);
            p.closeInventory();
            setKit(p, "Standard");
        }
        if (sameKit(item, KIT_TEST)) {
            if (db.hasKit(p, "Test")) {
                Utils.sendTitle(p, item.getItemMeta().getDisplayName(), Message.getMessage("inventories.kits.kit_chosen"), 20, 20*2, 20);
                setKit(p, "Test");
            }
            p.closeInventory();
        }

        // TODO: Handle other clicks
    }

    public boolean sameKit(ItemStack itemStack1, ItemStack itemStack2) {
        return itemStack1.getItemMeta().getDisplayName().equals(itemStack2.getItemMeta().getDisplayName());
    }

    private static void KIT_STANDARD(Player p) {
        Inventory inv = resetInventory(p);

        ItemStack itemStack = createItem(Material.STICK, 0, 1, Message.getMessage("inventories.kits.kit1.name"));
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addEnchant(Enchantment.KNOCKBACK, 1, false);
        itemStack.setItemMeta(itemMeta);

        inv.addItem(itemStack);
    }

    private static void KIT_TEST(Player p) {
        Inventory inv = resetInventory(p);

        ItemStack itemStack = createItem(Material.STICK, 0, 1, Message.getMessage("inventories.kits.kit2.name"));
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.addEnchant(Enchantment.KNOCKBACK, 1, false);
        itemStack.setItemMeta(itemMeta);
        itemStack.addUnsafeEnchantment(Enchantment.KNOCKBACK, 3);

        inv.addItem(itemStack);
    }

    public static Inventory resetInventory(Player p) {
        p.getInventory().clear();
        return p.getInventory();
    }

    public static String[] getLore(OfflinePlayer p, String kit, int coins) {
        if (!knockFFA.getDB().hasKit(p, kit))
            return new String[]{"§c", Message.getMessage("inventories.kits.kit_not_buyed.line1"), Message.getMessage("inventories.kits.kit_not_buyed.line2").replace("{coins}", Integer.toString(coins)), Message.getMessage("inventories.kits.kit_not_buyed.line3")};
        return new String[]{"§a", Message.getMessage("inventories.kits.kit_choose")};
    }

    public static void setKit(Player p, String kit) {
        p.setMetadata("active_kit", new FixedMetadataValue(knockFFA, kit));
        switch (kit) {
            case "Standard":
                KIT_STANDARD(p);
                break;
            case "Test":
                KIT_TEST(p);
                break;
            default:
                break;
        }
    }

    public static void setLastKit(Player p, String kit) {
        p.setMetadata("last_kit", new FixedMetadataValue(knockFFA, kit));
    }

    public static String getLastKit(Player p) {
        return p.getMetadata("last_kit").get(0).asString();
    }

    public static String getKit(Player p) {
        return p.getMetadata("active_kit").get(0).asString();
    }

    @EventHandler
    public void onSpawn(PlayerRespawnEvent e) {
        getKit(e.getPlayer());
        KIT_STANDARD(e.getPlayer());
    }

}

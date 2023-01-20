package de.kevin.knockffa.inventory;

import de.kevin.knockffa.KnockFFA;
import de.kevin.knockffa.MapHandler;
import de.kevin.knockffa.Message;
import de.kevin.knockffa.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;

import static de.kevin.knockffa.inventory.InventoryHelper.*;

public class VotingInventoryHandler implements Listener {
    static Inventory inventory;
    private static KnockFFA knockFFA;

    public VotingInventoryHandler(KnockFFA knockFFA) {
        VotingInventoryHandler.knockFFA = knockFFA;
    }

    static final String TITLE = "§6KnockFFA §7-> " + Message.getMessage("inventories.voting.title");

    public static class CommandItems {

        public static ItemStack Voting;
        public static ItemStack ForceVote;
        public static ItemStack ForceChange;

    }

    public static Inventory createInventory(Player holder) {
        inventory = InventoryHelper.createInventory(TITLE, 2, true);

        inventory.setItem(0, getBack());
        inventory.setItem(4, createItem(Material.PAPER, 0, 1, Message.getMessage("inventories.voting.item")));

        if (holder.hasPermission("knockffa.map.voting.pause")) {
            CommandItems.Voting = createItem(Material.WOOL, MapHandler.MapSetter.isPaused() ? 14 : 5, 1, Message.getMessage("inventories.voting.items.pause_voting"));
            ItemStack item = CommandItems.Voting.clone();
            ItemMeta meta = item.getItemMeta();
            if (item.getDurability() == 5)
                meta.setLore(Collections.singletonList(Message.getMessage("inventories.voting.items.pause_voting_pause")));
            else
                meta.setLore(Collections.singletonList(Message.getMessage("inventories.voting.items.pause_voting_start")));
            item.setItemMeta(meta);
            inventory.setItem(9 + 3, item);
        }
        if (holder.hasPermission("knockffa.map.voting.forcevote"))
            inventory.setItem(9 + 4, CommandItems.ForceVote = createItem(Material.PAPER, 0, 1, Message.getMessage("inventories.voting.items.force_vote")));
        if (holder.hasPermission("knockffa.map.voting.forcechange"))
            inventory.setItem(9 + 5, CommandItems.ForceChange = createItem(Material.PAPER, 0, 2, Message.getMessage("inventories.voting.items.force_map_change")));

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

        if (item.getItemMeta().getDisplayName().equals(CommandItems.Voting.getItemMeta().getDisplayName())) {
            p.closeInventory();
            if (MapHandler.MapSetter.isPaused()) {
                MapHandler.MapSetter.setPaused(false);
                Utils.sendMessage(p, true, Message.getMessage("inventories.voting.voting_start"));
            } else {
                MapHandler.MapSetter.setPaused(true);
                Utils.sendMessage(p, true, Message.getMessage("inventories.voting.voting_pause"));
            }
        }

        if (item.equals(CommandItems.ForceVote)) {
            p.closeInventory();
            MapHandler.MapSetter.stopVotingTimer();
            MapHandler.MapSetter.startVoting();
            MapHandler.MapSetter.startVotingTimer();
        }

        if (item.equals(CommandItems.ForceChange)) {
            p.closeInventory();
            p.openInventory(VotingChangeInventoryHandler.createInventory(p));
        }
    }

}

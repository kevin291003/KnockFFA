package de.kevin.knockffa.events;

import de.kevin.knockffa.KnockFFA;
import de.kevin.knockffa.MapHandler;
import de.kevin.knockffa.Message;
import de.kevin.knockffa.Utils;
import de.kevin.knockffa.database.Database;
import de.kevin.knockffa.inventory.KitInventoryHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class RegisterUser implements Listener {

    private final Database db;

    public RegisterUser(KnockFFA knockFFA) {
        db = knockFFA.getDB();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        e.setJoinMessage("§7[§a+§7] §a" + p.getName());
        if (!db.userExists(p)) {
            db.registerUser(p);
            Utils.sendMessage(p, true, Message.getMessage("events.database_registered"));
        }
        Utils.sendTitle(p, KnockFFA.getPrefix().substring(0, KnockFFA.getPrefix().length() - 3).trim(), Message.getMessage("join_subtitle"), 30, 40, 30);
        KitInventoryHandler.setKit(p, "Standard");

        MapHandler.MapSetter.tryVoting();
    }

}

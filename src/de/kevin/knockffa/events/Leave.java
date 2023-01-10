package de.kevin.knockffa.events;

import de.kevin.knockffa.KnockFFA;
import de.kevin.knockffa.database.Database;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class Leave implements Listener {

    private KnockFFA knockFFA;
    private Database db;

    public Leave(KnockFFA knockFFA) {
        this.knockFFA = knockFFA;
        this.db = knockFFA.getDB();
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        e.setQuitMessage("§7[§c-§7] §c" + p.getName());
    }
}

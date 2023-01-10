package de.kevin.knockffa.events;

import de.kevin.knockffa.KnockFFA;
import de.kevin.knockffa.Utils;
import de.kevin.knockffa.database.Database;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class RegisterUser implements Listener {

    private final KnockFFA knockFFA;
    private final Database db;

    public RegisterUser(KnockFFA knockFFA) {
        this.knockFFA = knockFFA;
        db = knockFFA.getDB();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        e.setJoinMessage("§7[§a+§7] §a" + p.getName());
        if (!db.userExists(p)) {
            db.registerUser(p);
            Utils.sendMessage(p, true, "§6Du wurdest in der Datenbank registriert.");
        }
        Utils.sendTitle(p, KnockFFA.getPrefix().substring(0, KnockFFA.getPrefix().length() - 3).trim(), "Herzlich Willkommen", 30, 40, 30);
    }

    @EventHandler
    public void doSmth(AsyncPlayerPreLoginEvent e) {
        db.update("ggfg;");
    }

}

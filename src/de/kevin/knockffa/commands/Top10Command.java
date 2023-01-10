package de.kevin.knockffa.commands;

import de.kevin.knockffa.KnockFFA;
import de.kevin.knockffa.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class Top10Command implements CommandExecutor {


    private KnockFFA knockFFA;

    public Top10Command(KnockFFA knockFFA) {
        this.knockFFA = knockFFA;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(KnockFFA.getPrefix());
        sender.sendMessage("§e§nTop 10 Punkteliste:");
        sender.sendMessage("");

        List<Map<String, Object>> l = knockFFA.getDB().getTop10AsList();
        if (l.isEmpty()) {
            sender.sendMessage("§cEs sind noch keine Daten von Spielern vorhanden.");
            return true;
        }
        int i = 0;
        for (Map<String, Object> item : l) {
            sender.sendMessage("§e" + (i + 1) + ". " + item.get("username") + ": " + item.get("score"));
            i++;
        }

        return true;
    }
}

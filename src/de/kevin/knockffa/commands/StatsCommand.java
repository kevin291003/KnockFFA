package de.kevin.knockffa.commands;

import de.kevin.knockffa.KnockFFA;
import de.kevin.knockffa.Message;
import de.kevin.knockffa.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Class to handle /stats command
 */
public class StatsCommand implements CommandExecutor {

    /**
     * Instance of {@link KnockFFA} class
     */
    private final KnockFFA knockFFA;

    /**
     * Instantiate class and set main class
     * @param knockFFA instance of {@link KnockFFA} class
     */
    public StatsCommand(KnockFFA knockFFA) {
        this.knockFFA = knockFFA;
    }

    /**
     * Method to handle command /top10
     * @param commandSender who executes command
     * @param command executed command
     * @param s command string
     * @param strings command arguments
     * @return syntax okay
     */
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (strings.length == 0) {
            if (commandSender instanceof Player) {
                Map<String, Object> m = knockFFA.getDB().getStats((Player) commandSender);
                Utils.sendMessage(commandSender,true, "");
                Utils.sendMessage(commandSender, false, Message.getMessage("commands.statscommand.stats").replace("{player}", m.get("username").toString()));
                Utils.sendMessage(commandSender, false, "");
                Utils.sendMessage(commandSender, false, Message.getMessage("commands.statscommand.kills").replace("{kills}", m.get("kills").toString()));
                Utils.sendMessage(commandSender, false, Message.getMessage("commands.statscommand.deaths").replace("{deaths}", m.get("deaths").toString()));
                Utils.sendMessage(commandSender, false, Message.getMessage("commands.statscommand.coins").replace("{coins}", m.get("coins").toString()));
                Utils.sendMessage(commandSender, false, Message.getMessage("commands.statscommand.score").replace("{score}", m.get("score").toString()));
                Utils.sendMessage(commandSender, false, Message.getMessage("commands.statscommand.rank").replace("{rank}", m.get("rank").toString()));

            } else
                Utils.sendMessage(commandSender, true, "§c/stats <player>");
            return true;
        } else if (strings.length == 1) {
            OfflinePlayer op = Bukkit.getOfflinePlayer(knockFFA.getDB().getUUID(strings[0]));
            Map<String, Object> m = knockFFA.getDB().getStats(op);
            if (m.isEmpty()) {
                Utils.sendMessage(commandSender, true, Message.getMessage("commands.statscommand.no_data").replace("{player}", strings[0]));
                return true;
            }
            Utils.sendMessage(commandSender,true, "");
            Utils.sendMessage(commandSender, false, Message.getMessage("commands.statscommand.stats").replace("{player}", m.get("username").toString()));
            Utils.sendMessage(commandSender, false, "");
            Utils.sendMessage(commandSender, false, Message.getMessage("commands.statscommand.kills").replace("{kills}", m.get("kills").toString()));
            Utils.sendMessage(commandSender, false, Message.getMessage("commands.statscommand.deaths").replace("{deaths}", m.get("deaths").toString()));
            Utils.sendMessage(commandSender, false, Message.getMessage("commands.statscommand.coins").replace("{coins}", m.get("coins").toString()));
            Utils.sendMessage(commandSender, false, Message.getMessage("commands.statscommand.score").replace("{score}", m.get("score").toString()));
            Utils.sendMessage(commandSender, false, Message.getMessage("commands.statscommand.rank").replace("{rank}", m.get("rank").toString()));
        }
        return true;
    }
}

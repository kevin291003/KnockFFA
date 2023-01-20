package de.kevin.knockffa.commands;

import de.kevin.knockffa.KnockFFA;
import de.kevin.knockffa.Message;
import de.kevin.knockffa.events.GamePlayEvents;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

/**
 * Class to handle /top10 command
 */
public class Top10Command implements CommandExecutor {

    /**
     * Instance of {@link KnockFFA} class
     */
    private final KnockFFA knockFFA;

    /**
     * Instantiate class and set main class
     * @param knockFFA instance of {@link KnockFFA} class
     */
    public Top10Command(KnockFFA knockFFA) {
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
        commandSender.sendMessage(KnockFFA.getPrefix());
        commandSender.sendMessage(Message.getMessage("commands.top10command.first_line"));
        commandSender.sendMessage("");

        Player p = (Player) commandSender;
        int x;
        for (x = 0; x < 3 * 12; x += 3) {
            Bukkit.getScheduler().runTaskLater(knockFFA, () ->  {
                p.getLocation().getWorld().spigot().playEffect(p.getLocation(), Effect.TILE_DUST, 1, 0, 1.5F, 0.5F, 1.5F, 0.05F, 50, 128);
                p.getLocation().getWorld().playSound(p.getLocation(), Sound.FIREWORK_LAUNCH, 1.0F, 0.0F);
            }, x);
        }
        System.out.println(x);
        Bukkit.getScheduler().runTaskLater(knockFFA, () -> {
            GamePlayEvents.lightning(p);
        }, x);




        List<Map<String, Object>> l = knockFFA.getDB().getTop10AsList();
        if (l.isEmpty()) {
            commandSender.sendMessage(Message.getMessage("commands.top10command.no_data"));
            return true;
        }
        int i = 0;
        for (Map<String, Object> item : l) {
            commandSender.sendMessage("§e" + (i + 1) + ". " + item.get("username") + ": " + item.get("score"));
            i++;
        }

        return true;
    }
}

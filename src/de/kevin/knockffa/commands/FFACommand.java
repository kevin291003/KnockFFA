package de.kevin.knockffa.commands;

import de.kevin.knockffa.inventory.StartInventoryHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Class to handle /knockffa command
 */
public class FFACommand implements CommandExecutor {

    /**
     * Method to handle command /knockffa
     * @param commandSender who executes command
     * @param command executed command
     * @param s command string
     * @param strings command arguments
     * @return syntax okay
     */
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return true;
        ((Player) commandSender).openInventory(StartInventoryHandler.createInventory());

        return true;
    }
}

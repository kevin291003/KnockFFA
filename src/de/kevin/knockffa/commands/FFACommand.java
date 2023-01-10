package de.kevin.knockffa.commands;

import de.kevin.knockffa.inventory.StartInventoryHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FFACommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        ((Player) commandSender).openInventory(StartInventoryHandler.createInventory());

        return true;
    }
}

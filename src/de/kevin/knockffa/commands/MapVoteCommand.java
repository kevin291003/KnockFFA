package de.kevin.knockffa.commands;

import de.kevin.knockffa.MapHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.StringJoiner;

public class MapVoteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (MapHandler.MapSetter.isVoting) {
            StringJoiner joiner = new StringJoiner(" ");
            for (int i = 0; i < args.length; i++) {
                joiner.add(args[i]);
            }
            if (!MapHandler.MapSetter.hasVoted((Player) sender)) {
                MapHandler.MapSetter.vote(((Player) sender), MapHandler.MapSetter.getMapByName(joiner.toString()));
            }
        }
        return true;
    }
}

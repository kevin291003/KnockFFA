package de.kevin.knockffa.commands;

import de.kevin.knockffa.MapHandler;
import de.kevin.knockffa.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.StringJoiner;

public class MapVoteCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (MapHandler.MapSetter.isVoting) {
            StringJoiner joiner = new StringJoiner(" ");
            Arrays.stream(args).forEach(joiner::add);
            if (!MapHandler.MapSetter.hasVoted((Player) sender)) {
                MapHandler.MapSetter.vote(((Player) sender), MapHandler.MapSetter.getMapByName(joiner.toString()));
            } else {
                Utils.sendMessage(((Player) sender), true, "§cDu hast bereits abgestimmt!");
            }
        } else
            Utils.sendMessage(((Player) sender), true, "§cEs läuft gerade kein Voting!");
        return true;
    }
}

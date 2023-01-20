package de.kevin.knockffa.commands;

import de.kevin.knockffa.MapHandler;
import de.kevin.knockffa.Message;
import de.kevin.knockffa.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.StringJoiner;

/**
 * Class to handle /mapvote command
 */
public class MapVoteCommand implements CommandExecutor {

    /**
     * Method to handle command /mapvote
     * @param commandSender who executes command
     * @param command executed command
     * @param s command string
     * @param strings command arguments
     * @return syntax okay
     */
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return true;
        if (strings.length != 1) return true;
        if (MapHandler.MapSetter.isVoting) {
            StringJoiner joiner = new StringJoiner(" ");
            Arrays.stream(strings).forEach(joiner::add);
            if (!MapHandler.MapSetter.hasVoted((Player) commandSender)) {
                MapHandler.MapSetter.vote(((Player) commandSender), MapHandler.MapSetter.getMapByName(joiner.toString()));
            } else {
                Utils.sendMessage(((Player) commandSender), true, Message.getMessage("commands.mapvotecommand.already_voted"));
            }
        } else
            Utils.sendMessage(((Player) commandSender), true, Message.getMessage("commands.mapvotecommand.no_voting"));
        return true;
    }
}

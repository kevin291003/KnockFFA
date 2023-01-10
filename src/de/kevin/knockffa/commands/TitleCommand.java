package de.kevin.knockffa.commands;

import de.kevin.knockffa.Logging;
import de.kevin.knockffa.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class TitleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (args.length < 2) return false;

        List<String> possible = Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
        possible.addAll(Arrays.asList("all", "@a", "*"));

        StringJoiner stringJoiner = new StringJoiner(" ");
        Arrays.stream(args).forEach(stringJoiner::add);

        String[] titles = stringJoiner.toString().split("%%", 2);

        Utils.sendTitle((Player) sender, titles[0], titles.length == 2 ? titles[1] : null, 20, 20 * 3, 10);


        return true;
    }
}

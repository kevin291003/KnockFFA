package de.kevin.knockffa;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Utils {

    public static void healPlayer(Player p) {
        p.setHealth(p.getMaxHealth());
    }

    public static boolean isFlying(Player p) {
        return p.isFlying();
    }

    public static void setFlying(Player p, boolean flying) {
        p.setAllowFlight(flying);
        p.setFlying(flying);
    }

    public static void sendMessage(CommandSender commandSender, boolean prefix, String message) {
        if (prefix)
            message = KnockFFA.getPrefix() + message;
        commandSender.sendMessage(message);
    }

    public static void broadcast(boolean prefix, String message) {
        broadcast(prefix, null, message);
    }

    public static void broadcast(boolean prefix, String permission, String message) {
        if (permission != null)
            Bukkit.getOnlinePlayers().stream().filter(player -> player.hasPermission(permission)).forEach(player -> Utils.sendMessage(player, prefix, message));
        else
            Bukkit.getOnlinePlayers().forEach(player -> Utils.sendMessage(player, prefix, message));
    }

    public static void sendTitle(Player p, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        Reflection.sendTitle(p, title, subtitle, fadeIn, stay, fadeOut);
    }

    public static void sendTitle(Player p, String title, int fadeIn, int stay, int fadeOut) {
        sendTitle(p, title, "", fadeIn, stay, fadeOut);
    }

    public static void sendTitle(Player p, String title) {
        sendTitle(p, title, -1, -1, -1);
    }

    public static boolean checkPermission(CommandSender sender, String permission) {
        if (!sender.hasPermission(permission)) {
            sendMessage(sender, true, Message.getMessage("commands.no_permission"));
            return false;
        } else
            return true;
    }

    public static void sendVoteCommand(MapHandler mapHandler) {
        TextComponent component = new TextComponent(TextComponent.fromLegacyText("§9- " + mapHandler.getMapName()));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/mapvote " + mapHandler.getMapName()));
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(Message.getMessage("commands.mapvotecommand.hover"))));
        Bukkit.getOnlinePlayers().forEach(player -> player.spigot().sendMessage(component));
    }
}

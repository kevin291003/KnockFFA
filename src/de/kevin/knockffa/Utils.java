package de.kevin.knockffa;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
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

    public static void sendMessage(Player p, boolean prefix, String message) {
        if (prefix)
            message = KnockFFA.getPrefix() + message;
        p.sendMessage(message);
    }

    public static void broadcast(boolean prefix, String message) {
        broadcast(prefix, null, message);
    }

    public static void broadcast(boolean prefix, String permission, String message) {
        message = (prefix ? KnockFFA.getPrefix() : "") + message;
        if (permission != null)
            Bukkit.broadcast(message, permission);
        else
            Bukkit.broadcastMessage(message);
    }

    public static void sendTitle(Player p, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        IChatBaseComponent chatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + title + "\"}");
        IChatBaseComponent chatSubTitle = subtitle == null ? null : IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + subtitle + "\"}");

        PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, chatTitle);
        PacketPlayOutTitle subtitlePacket = chatSubTitle == null ? null : new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, chatSubTitle);
        PacketPlayOutTitle lengthPacket = new PacketPlayOutTitle(fadeIn >= 0 ? fadeIn : 5, stay >= 0 ? stay : 20, fadeOut >= 0 ? fadeOut : 5);

        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(titlePacket);
        if (subtitlePacket != null)
            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(subtitlePacket);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(lengthPacket);
    }

    public static void sendTitle(Player p, String title, int fadeIn, int stay, int fadeOut) {
        sendTitle(p, title, null, fadeIn, stay, fadeOut);
    }

    public static void sendTitle(Player p, String title) {
        sendTitle(p, title, -1, -1, -1);
    }


    public static boolean checkPermission(CommandSender sender, String permission) {
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(KnockFFA.getPrefix() + "§cDu hast keine Berechtigung!");
            return false;
        } else
            return true;
    }
}

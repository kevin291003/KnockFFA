package de.kevin.knockffa;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * This class is used for reflection features
 */
public class Reflection {

    public static boolean versionLowerOrEqual(String version) {
        System.out.println(version);
        System.out.println(Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3]);
        version = version.replace("v", "");
        String versionNow = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].replace("v", "");
        for(int i = 0; i <= 2; i++)
            if (Integer.parseInt(versionNow.split("_")[i].replace("R", "")) > Integer.parseInt(version.split("_")[i].replace("R", "")))
                return false;
        return true;
    }

    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server."
                    + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception ignored) {
        }
    }

    public static void sendTitle(Player p, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        try {
            fadeIn = fadeIn >= 0 ? fadeIn : 5;
            stay = stay >= 0 ? stay : 20;
            fadeOut = fadeOut >= 0 ? fadeOut : 5;

            Class<?> IChatBaseComponentClass = Objects.requireNonNull(getNMSClass("IChatBaseComponent"));
            Class<?> PacketPlayOutTitleClass = Objects.requireNonNull(getNMSClass("PacketPlayOutTitle"));
            Class<?> ChatSerializerClass = Objects.requireNonNull(versionLowerOrEqual("v1_8_R1") ? getNMSClass("ChatSerializer") : getNMSClass("IChatBaseComponent$ChatSerializer"));
            Class<?> EnumTitleActionClass = Objects.requireNonNull(versionLowerOrEqual("v1_8_R1") ? getNMSClass("EnumTitleAction") : getNMSClass("PacketPlayOutTitle$EnumTitleAction"));

            Object chatTitle = ChatSerializerClass.getMethod("a", String.class).invoke(null, "{\"text\": \"" + title + "\"}");
            Object chatSubTitle = ChatSerializerClass.getMethod("a", String.class).invoke(null, "{\"text\": \"" + subtitle + "\"}");

            Object lengthPacket = PacketPlayOutTitleClass.getConstructor(int.class, int.class, int.class).newInstance(fadeIn, stay, fadeOut);
            Object titlePacket = PacketPlayOutTitleClass.getConstructor(EnumTitleActionClass, IChatBaseComponentClass).newInstance(EnumTitleActionClass.getField("TITLE").get(null), chatTitle);
            Object subTitlePacket = PacketPlayOutTitleClass.getConstructor(EnumTitleActionClass, IChatBaseComponentClass).newInstance(EnumTitleActionClass.getField("SUBTITLE").get(null), chatSubTitle);

            sendPacket(p, lengthPacket);
            sendPacket(p, titlePacket);
            sendPacket(p, subTitlePacket);

        } catch (Exception ignored) {
        }
    }

}

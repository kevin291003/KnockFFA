package de.kevin.knockffa;

import org.bukkit.ChatColor;

import java.util.Objects;

public class Message {

    private static KnockFFA knockFFA;
    private static ConfigHandler configHandler;

    @SuppressWarnings("MethodNameSameAsClassName")
    public static void Message(KnockFFA knockFFA, String language) {
        Message.knockFFA = knockFFA;

        knockFFA.saveResource("messages/english.yml", false);
        //knockFFA.saveResource("messages/german.yml", false);

        configHandler = new ConfigHandler(knockFFA, "messages/" + language + ".yml");
    }

    public static String getMessage(String key) {
        try {
            return Objects.requireNonNull(ChatColor.translateAlternateColorCodes('&', configHandler.getConfiguration().getString(key)));
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "Translation NOT FOUND: " + key;
        }
    }

}

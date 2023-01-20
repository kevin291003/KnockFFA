package de.kevin.knockffa;

import de.kevin.knockffa.inventory.KitInventoryHandler;
import org.bukkit.Bukkit;

import java.util.Random;

/**
 * Handles special gameplay events
 */
public class GamePlay {

    /**
     * Instance of main class
     */
    private static KnockFFA knockFFA;

    /**
     * Sets the instance of main class
     * @param knockFFA instance of main class
     */
    public static void setKnockFFA(KnockFFA knockFFA) {
        GamePlay.knockFFA = knockFFA;
    }

    /**
     * Have players access to a special kit
     */
    public static boolean kitEvent = false;
    /**
     * The kit which all players temporally have access to
     */
    public static String kitEventKit = null;

    public static String[] kits = new String[] {"Standard", "Test"};

    public static boolean didKitEvent = false;

    public static void EventHandling() {
        if (!didKitEvent) {
            Utils.broadcast(true, "!didKitEvent");
            Bukkit.getScheduler().runTaskLater(knockFFA, GamePlay::doEventHandlingTask, 20 * 60 * 2);
        } else {
            Utils.broadcast(true, "didKitEvent");
            Bukkit.getScheduler().runTaskLater(knockFFA, GamePlay::doEventHandlingTask, 20 * 60 * 10);
        }
    }

    private static void doEventHandlingTask() {
        Random rnd = new Random();
        float chance = rnd.nextFloat();
        Utils.broadcast(true, chance + "");
        if (chance <= 0.5F) {
            startKitEvent(kits[rnd.nextInt(2)]);
            didKitEvent = true;
        } else
            didKitEvent = false;
        EventHandling();
    }

    /**
     * Starts an event - all players have access to a specific kit for a few minutes
     * @param kit the kit which should used
     */
    public static void startKitEvent(String kit) {
        kitEvent = true;
        kitEventKit = kit;
        Bukkit.getOnlinePlayers().forEach(player -> {
            KitInventoryHandler.setLastKit(player, KitInventoryHandler.getKit(player));
            KitInventoryHandler.setKit(player, kit);
            Utils.sendTitle(player, Message.getMessage("gameevents.kitevent.start_title"), Message.getMessage("gameevents.kitevent.start_subtitle").replace("{kit}", kit), 20, 20 * 2, 20);
        });
        Bukkit.getScheduler().runTaskLater(knockFFA, () -> {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    KitInventoryHandler.setKit(player, KitInventoryHandler.getLastKit(player));
                    Utils.sendTitle(player, Message.getMessage("gameevents.kitevent.end_title"), Message.getMessage("gameevents.kitevent.end_subtitle"), 20, 20 * 2, 20);
                    Utils.sendMessage(player, true, Message.getMessage("gameevents.kitevent.end").replace("{kit}", kit));
                });
                kitEvent = false;
                kitEventKit = null;
        }, 20 * 60 * 5);
    }


    // TODO: 18.01.2023 Add Special events
}

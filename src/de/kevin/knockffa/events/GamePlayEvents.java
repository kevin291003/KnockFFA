package de.kevin.knockffa.events;

import de.kevin.knockffa.KnockFFA;
import de.kevin.knockffa.MapHandler;
import de.kevin.knockffa.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.HashMap;

public class GamePlayEvents implements Listener {

    private final KnockFFA knockFFA;

    public KnockFFA getKnockFFA() {
        return knockFFA;
    }

    public GamePlayEvents(KnockFFA knockFFA) {
        this.knockFFA = knockFFA;
    }

    public Player lightningSpawner = null;

    /**
     * OfflinePlayer damaged - OfflinePlayer damager
     */
    private HashMap<OfflinePlayer, OfflinePlayer> lastDamaged = new HashMap<>();

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        if (!(e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM) || e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)))
            e.setCancelled(true);
    }

    @EventHandler
    public void onEntity(CreeperPowerEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntity(EntityBreakDoorEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntity(EntityTameEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntity(EntityTargetEvent e) {
        if (!e.getReason().equals(EntityTargetEvent.TargetReason.CUSTOM))
            e.setCancelled(true);
    }

    @EventHandler
    public void onEntity(EntityTargetLivingEntityEvent e) {
        if (!e.getReason().equals(EntityTargetEvent.TargetReason.CUSTOM))
            e.setCancelled(true);
    }

    @EventHandler
    public void onEntity(FoodLevelChangeEvent e) {
        e.setCancelled(true);
        e.setFoodLevel(20);
        ((Player) e.getEntity()).setSaturation(20F);
    }

    @EventHandler
    public void onEntity(SlimeSplitEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntity(SpawnerSpawnEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onWeatherRainChange(WeatherChangeEvent e) {
        if (e.toWeatherState()) {
            e.getWorld().setStorm(false);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onWeatherThunderChange(ThunderChangeEvent e) {
        if (e.toThunderState()) {
            e.getWorld().setThundering(false);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        e.setKeepInventory(true);
        System.out.println(e.getEntity().getKiller());
        dead(e.getEntity());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        switch (e.getCause()) {
            case FALL:
                e.setCancelled(true);
                lightning((Player) e.getEntity());
                break;
            case VOID:
            case LAVA:
            case DROWNING:
                e.setDamage(((Player) e.getEntity()).getMaxHealth());
                break;
            case LIGHTNING:
                // TODO Special attack handling
                break;
            default:
                break;
        }
    }

    // TODO: Damage Event verfeinern
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) { // Last Seen
        if (!(e.getEntity() instanceof Player)) return;
        Player damaged = ((Player) e.getEntity());
        if (((e.getDamage() > damaged.getMaxHealth() / 2) && e.getDamage() != damaged.getMaxHealth()) || e.getDamage() == 1) {
            e.setDamage(0);
        }
        // Damage durch Projectile
        if (e.getCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
            Projectile projectile = (Projectile) e.getDamager();
            Bukkit.broadcastMessage("Projectile from " + projectile.getShooter());
            putLastDamaged((OfflinePlayer) damaged, (OfflinePlayer) projectile.getShooter());
        } else
            putLastDamaged((OfflinePlayer) damaged, (OfflinePlayer) e.getDamager());
        Bukkit.broadcastMessage(damaged.getName() + " damaged by " + e.getDamager());
    }

    public void dead(Player damaged) {
        if (lastDamaged.containsKey((OfflinePlayer) damaged)) {
            OfflinePlayer damager = lastDamaged.get(damaged);
            if (damager.isOnline()) {
                Player player = (Player) damager;
                player.giveExpLevels(1);
                Utils.sendMessage(player, true, "§9Du hast " + damaged + " getötet.");
            }
            Utils.sendMessage(damaged, true, "§9Du wurdest von " + damager.getName() + " getötet.");
        }
        lastDamaged.remove((OfflinePlayer) damaged);
    }

    public void lightning(Player p) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (p == player) break;
            if (MapHandler.MapSetter.activeMap.getSafezone() >= player.getLocation().getY()) {
                player.getWorld().strikeLightningEffect(player.getLocation());
                player.damage(player.getMaxHealth(), p);
            }
        }
    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent e) {
        lightning(e.getPlayer());
    }

    public void putLastDamaged(OfflinePlayer damaged, OfflinePlayer damager) {
        // Lösche letzten Angreifer
        lastDamaged.remove(damaged);

        // Setze neuen Angreifer
        lastDamaged.put(damaged, damager);

        // Entferne nach 5 Sekunden Angreifer
        Bukkit.getScheduler().runTaskLater(knockFFA, () -> lastDamaged.remove(damaged, damager), 20 * 5);
    }

}

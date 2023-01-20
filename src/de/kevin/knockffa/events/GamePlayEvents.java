package de.kevin.knockffa.events;

import de.kevin.knockffa.KnockFFA;
import de.kevin.knockffa.MapHandler;
import de.kevin.knockffa.Message;
import de.kevin.knockffa.Utils;
import de.kevin.knockffa.database.Database;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class GamePlayEvents implements Listener {

    private static KnockFFA knockFFA;
    private static Database db;

    public KnockFFA getKnockFFA() {
        return knockFFA;
    }

    public GamePlayEvents(KnockFFA knockFFA) {
        this.knockFFA = knockFFA;
        db = knockFFA.getDB();
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent e) {
        if (!(e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM) || e.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)))
            e.setCancelled(true);
    }

    @EventHandler
    public void onEntity(CreeperPowerEvent e) {
        if (!KnockFFA.Settings.GamePlay) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntity(EntityBreakDoorEvent e) {
        if (!KnockFFA.Settings.GamePlay) return;
        e.setCancelled(true);
        if (e.getEntity() instanceof Player)
            if (((Player)e.getEntity()).getGameMode().equals(GameMode.CREATIVE))
                e.setCancelled(false);
    }

    @EventHandler
    public void onEntity(EntityTameEvent e) {
        if (!KnockFFA.Settings.GamePlay) return;
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
        if (!KnockFFA.Settings.GamePlay) return;
        if (e.toWeatherState()) {
            e.getWorld().setStorm(false);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onWeatherThunderChange(ThunderChangeEvent e) {
        if (!KnockFFA.Settings.GamePlay) return;
        if (e.toThunderState()) {
            e.getWorld().setThundering(false);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if (!KnockFFA.Settings.GamePlay) return;
        e.setKeepInventory(true);
        e.setDeathMessage(null);
        e.setDroppedExp(0);
        e.setNewLevel(0);
        e.setNewTotalExp(0);
        if (e.getEntity().getKiller() != null) {
            dead(e.getEntity(), e.getEntity().getKiller());
            db.addStat(e.getEntity().getKiller(), Database.Stats.KILLS, 1);
            if (e.getEntity().getLastDamage() == 21D) {
                db.addStat(e.getEntity(), Database.Reason.DEATH_BY_LIGHTNING);
                db.addStat(e.getEntity().getKiller(), Database.Reason.KILL_BY_LIGHTNING);
            } else {
                db.addStat(e.getEntity(), Database.Reason.DEATH_BY_PLAYER);
                db.addStat(e.getEntity().getKiller(), Database.Reason.KILL_BY_PLAYER);
            }
        } else {
            dead(e.getEntity(), null);
            db.addStat(e.getEntity(), Database.Reason.DEATH_BY_VOID);
        }
        e.getEntity().spigot().respawn();
    }

    @EventHandler
    public void onDeath(PlayerRespawnEvent e) {
        if (!KnockFFA.Settings.GamePlay) return;
        e.setRespawnLocation(MapHandler.MapSetter.activeMap.getSpawn());
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!KnockFFA.Settings.GamePlay) return;
        if (!(e.getEntity() instanceof Player)) {
            e.setCancelled(true);
            return;
        }
        Player p = (Player) e.getEntity();
        if (MapHandler.MapSetter.activeMap.getSafezone() < p.getLocation().getY())
            e.setCancelled(true);

        switch (e.getCause()) {
            case FALL:
                e.setCancelled(true);
                break;
            case VOID:
            case LAVA:
            case DROWNING:
                e.setDamage(p.getMaxHealth());
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!KnockFFA.Settings.GamePlay) return;
        if (!(e.getEntity() instanceof Player)) {
            e.setCancelled(true);
            if (e.getDamager() instanceof Player)
                if (((Player)e.getDamager()).getGameMode().equals(GameMode.CREATIVE))
                    e.setCancelled(false);
            return;
        }
        Player damaged = ((Player) e.getEntity());
        if (e.getDamage() == 21D) {

        }
        if (    ((e.getDamage() > damaged.getMaxHealth() / 2) && e.getDamage() != 21)
                || e.getDamage() <= 2) {
            e.setDamage(0);
        }
    }

    @EventHandler
    public void onFish(PlayerFishEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onItem(PlayerItemDamageEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onItem(PlayerDropItemEvent e) {
        if (!KnockFFA.Settings.GamePlay) return;
        if (!e.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            e.setCancelled(true);
    }

    @EventHandler
    public void onAchievement(PlayerAchievementAwardedEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onArmorStand(PlayerArmorStandManipulateEvent e) {
        if (!KnockFFA.Settings.GamePlay) return;
        if (!e.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            e.setCancelled(true);
    }

    @EventHandler
    public void onBed(PlayerBedEnterEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onItem(PlayerPickupItemEvent e) {
        if (!KnockFFA.Settings.GamePlay) return;
        if (!e.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlock(BlockBreakEvent e) {
        if (!KnockFFA.Settings.GamePlay) return;
        if (!e.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlock(BlockDamageEvent e) {
        if (!KnockFFA.Settings.GamePlay) return;
        if (!e.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlock(BlockPlaceEvent e) {
        if (!KnockFFA.Settings.GamePlay) return;
        if (e.getPlayer().getGameMode().equals(GameMode.CREATIVE) == e.getPlayer().getGameMode().equals(GameMode.SURVIVAL))
            e.setCancelled(true);
        if (e.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
            Bukkit.getScheduler().runTaskLater(knockFFA, () -> {
                e.getBlockPlaced().setType(Material.RED_SANDSTONE);
                Bukkit.getScheduler().runTaskLater(knockFFA, () -> {
                    e.getBlockPlaced().setType(Material.AIR);
                }, 20 * 2);
            }, 20 * 4);
        }
    }

    @EventHandler
    public void onBlock(LeavesDecayEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onHanging(HangingBreakEvent e) {
        if (!KnockFFA.Settings.GamePlay) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onHanging(HangingBreakByEntityEvent e) {
        if (!KnockFFA.Settings.GamePlay) return;
        if (e.getRemover() instanceof Player)
            if (((Player)e.getRemover()).getGameMode().equals(GameMode.CREATIVE))
                e.setCancelled(false);
    }

    @EventHandler
    public void onFire(BlockIgniteEvent e) {
        if (!KnockFFA.Settings.GamePlay) return;
        e.setCancelled(true);
        if (e.getCause().equals(BlockIgniteEvent.IgniteCause.FLINT_AND_STEEL))
            if (e.getPlayer().getGameMode().equals(GameMode.CREATIVE))
                e.setCancelled(false);
    }

    @EventHandler
    public void onFire(BlockBurnEvent e) {
        if (!KnockFFA.Settings.GamePlay) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (!KnockFFA.Settings.GamePlay) return;
        if (MapHandler.MapSetter.activeMap == null) return;
        if (e.getTo().getY() <= MapHandler.MapSetter.activeMap.getDeathzone()) {
            if (!e.getPlayer().isDead()) {
                e.getPlayer().setHealth(0D);
                e.setCancelled(true);
            }
        }
    }

    public void dead(Player damaged, Player killer) {
        if (killer != null && killer.isOnline()) {
            Utils.sendMessage(killer, true, Message.getMessage("events.death.killer").replace("{killed}", damaged.getName()));
            Utils.sendMessage(damaged, true, Message.getMessage("events.death.killed_by_player").replace("{killer}", killer.getName()));
        } else {
            Utils.sendMessage(damaged, true, Message.getMessage("events.death.killed_other"));
        }
        db.addStat(damaged, Database.Stats.DEATHS, 1);
    }

    public static void lightning(Player p) {
        long l = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1.0F, 0.0F);
            if (p == player) continue;
            if (player.isDead()) continue;
            if (MapHandler.MapSetter.activeMap.getSafezone() > player.getLocation().getBlockY()) {
                Bukkit.getScheduler().runTaskLater(knockFFA, () -> {
                    if (!player.isOnline()) return;
                    player.getWorld().strikeLightningEffect(player.getLocation());
                    player.damage(21D, p);
                }, 20L + l);
                l += 10;
            }
        }
    }


    @EventHandler
    public void chat(AsyncPlayerChatEvent e) {
        if (!KnockFFA.Settings.CustomChat) return;
        if (e.getPlayer().hasPermission("knockffa.chat.color"))
            e.setMessage(ChatColor.translateAlternateColorCodes('&', e.getMessage()));
        e.setFormat("§9" + e.getPlayer().getDisplayName() + " §8§l> §7" + e.getMessage());
        e.getPlayer().spigot().respawn();
    }

}

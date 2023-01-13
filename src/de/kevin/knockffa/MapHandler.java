package de.kevin.knockffa;

import org.bukkit.*;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MapHandler {

    private final KnockFFA knockFFA;
    private final File mapFile;
    private FileConfiguration configuration;

    public String getMap() {
        return map;
    }

    public double getSafezone() {
        return safezone;
    }

    public double getDeathzone() {
        return deathzone;
    }

    public Location getSpawn() {
        return spawn;
    }

    public String getMapName() {
        return mapName;
    }

    public String getWorldString() {
        return (String) configuration.get("spawn.world");
    }

    private final String map;
    private double safezone = 255;
    private double deathzone = -10;
    private Location spawn = new Location(Bukkit.getWorlds().get(0), 0, 0, 100);
    private String mapName = "";

    private boolean loaded = false;

    private boolean finished = false;

    private KnockFFA getKnockFFA() {
        return knockFFA;
    }

    public MapHandler(KnockFFA knockFFA, String map) {
        this.knockFFA = knockFFA;
        this.map = map;
        this.mapFile = new File(getKnockFFA().getDataFolder() + "/maps/", map + ".mapconfig");
        createMapFile();
    }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        public void createMapFile() {
        if (!mapFile.exists()) {
            mapFile.getParentFile().mkdirs();
            try {
                mapFile.createNewFile();
            } catch (IOException ignored) {
            }
        }

        configuration = new YamlConfiguration();
        load();
        if (!isFinished())
            configuration.set("finished", false);
    }

    public FileConfiguration getConfiguration() {
        return configuration;
    }

    public MapHandler save() {
        if (isLoaded()) {
            try {
                configuration.save(mapFile);
                // loaded = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this;
    }

    public MapHandler load() {
        try {
            configuration.load(mapFile);
            loaded = true;
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
            loaded = false;
        }
        return this;
    }

    public MapHandler loadAll() {
        load();
        if (configuration.contains("spawn")) {
            WorldCreator creator = new WorldCreator((String) configuration.get("spawn.world"));
            World w = Bukkit.createWorld(creator);
            w.setAutoSave(false);
            w.setDifficulty(Difficulty.EASY);
            spawn = new Location(
                    Bukkit.getWorld((String) configuration.get("spawn.world")),
                    (double) configuration.get("spawn.x"),
                    (double) configuration.get("spawn.y"),
                    (double) configuration.get("spawn.z"),
                    Float.parseFloat(configuration.get("spawn.yaw").toString()),
                    Float.parseFloat(configuration.get("spawn.pitch").toString()));
        }
        if (configuration.contains("safezone"))
            safezone = (double) configuration.get("safezone");
        if (configuration.contains("deathzone"))
            deathzone = (double) configuration.get("deathzone");
        if (configuration.contains("mapname"))
            mapName = (String) configuration.get("mapname");
        return this;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public boolean isFinished() {
        return configuration.contains("finished") && configuration.getBoolean("finished");
    }

    public MapHandler setMapName(String name) {
        if (isLoaded()) {
            mapName = name;
            configuration.set("mapname", name);
        }
        return this;
    }

    public MapHandler setSpawnLocation(Location loc) {
        if (isLoaded()) {
            spawn = new Location(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
            configuration.set("spawn.x", loc.getX());
            configuration.set("spawn.y", loc.getY());
            configuration.set("spawn.z", loc.getZ());
            configuration.set("spawn.yaw", loc.getYaw());
            configuration.set("spawn.pitch", loc.getPitch());
            configuration.set("spawn.world", loc.getWorld().getName());
            loc.getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        }
        return this;
    }

    public MapHandler setDeathZone(Location loc) {
        if (isLoaded()) {
            deathzone = loc.getY();
            configuration.set("deathzone", loc.getY());
        }
        return this;
    }

    public MapHandler setSafeZone(Location loc) {
        if (isLoaded()) {
            safezone = loc.getY();
            configuration.set("safezone", loc.getY());
        }
        return this;
    }

    public MapHandler getMapHandler() {
        return this;
    }

    public MapHandler setFinished(boolean finished) {
        if (isLoaded()) {
            this.finished = finished;
            configuration.set("finished", finished);
        }
        return this;
    }

    public static class MapSetter implements Listener {
        public static MapHandler activeMap;
        public static List<MapHandler> maps = new ArrayList<>();
        private static KnockFFA knockFFA;
        public static boolean isVoting = false;
        public static boolean isPaused = false;
        private static BukkitTask mapChangeTask;
        private static final List<BukkitTask> tasks = new ArrayList<>();
        private static final HashMap<Player, MapHandler> votes = new HashMap<>();

        public static void initialize(KnockFFA knockFFA) {
            MapSetter.knockFFA = knockFFA;
            loadMaps(knockFFA);
            activeMap = maps.stream().findFirst().orElse(null);

            if (activeMap == null) {
                knockFFA.gameplay = false;
            } else {
                knockFFA.gameplay = true;
                startVotingTimer();
            }
        }

        public static void startVotingTimer() {
            mapChangeTask = Bukkit.getScheduler().runTaskTimer(knockFFA, new Runnable() {
                @Override
                public void run() {
                    if (!isPaused())
                        startVoting();
                }
            },20 * 60 * 5, 20 * 60 * 5);
        }

        public static void stopVotingTimer() {
            tasks.forEach(BukkitTask::cancel);
            mapChangeTask.cancel();
        }

        public static void startVoting() {
            votes.clear();
            tasks.clear();
            isVoting = true;
            Utils.broadcast(true, "§eVoting beginnt (30 Sekunden). Klicke zum abstimmen auf die Mapnamen (1 Vote möglich).");
            for (MapHandler mapHandler : shuffle(5)) {
                Utils.sendVoteCommand(mapHandler);
            }

            tasks.add(Bukkit.getScheduler().runTaskLater(knockFFA, () -> Utils.broadcast(true, "§eVoting endet in 20 Sekunden..."), 20 * 10));
            tasks.add(Bukkit.getScheduler().runTaskLater(knockFFA, () -> Utils.broadcast(true, "§eVoting endet in 10 Sekunden..."), 20 * 20));
            tasks.add(Bukkit.getScheduler().runTaskLater(knockFFA, () -> Utils.broadcast(true, "§eVoting endet in 5 Sekunden..."), 20 * 25));
            tasks.add(Bukkit.getScheduler().runTaskLater(knockFFA, MapSetter::endVoting, 20 * 30));
        }

        public static boolean isPaused() {
            return isPaused;
        }

        public static void setPaused(boolean paused) {
            isPaused = paused;
            if (isPaused())
                stopVotingTimer();
            else
                startVotingTimer();
        }

        public static void endVoting() {
            isVoting = false;
            HashMap<MapHandler, Integer> votings = new HashMap<>();
            for (Player p : votes.keySet()) {
                if (!votings.containsKey(votes.get(p))) {
                    votings.put(votes.get(p), 1);
                } else {
                    votings.put(votes.get(p), votings.get(votes.get(p)) + 1);
                }
            }
            if (!votings.isEmpty()) {
                int highestVote = votings.values().stream().mapToInt(i -> i).filter(i -> i >= 0).max().orElse(0);

                //noinspection OptionalGetWithoutIsPresent
                changeMap(votings.entrySet().stream().filter((entry) -> entry.getValue() == highestVote).findAny().get().getKey());
            }
            Utils.broadcast(true, "§eDas Voting ist beendet.");
        }

        public static boolean hasVoted(Player p) {
            return votes.containsKey(p);
        }

        public static void vote(Player p, MapHandler mapHandler) {
            votes.put(p, mapHandler);
            Utils.sendMessage(p, true, "§aDu hast für die Map '" + mapHandler.getMapName() + "' abgestimmt.");
        }

        public static void changeMap(MapHandler newMap) {
            if (activeMap == newMap) return;
            activeMap = newMap;

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.teleport(activeMap.getSpawn());
                Utils.sendTitle(p, "§7§kww§r§6§l " + activeMap.getMapName() + " §7§kww", "§7- §9Mapwechsel §7-", 20, 20*3, 20*2);
            }
        }

        public static List<MapHandler> loadMaps(KnockFFA knockFFA) {
            List<MapHandler> l = new ArrayList<>();
            File mapsFolder = new File(knockFFA.getDataFolder() + "/maps/");
            if (!mapsFolder.exists())
                //noinspection ResultOfMethodCallIgnored
                mapsFolder.mkdirs();

            for (File mapFile : Objects.requireNonNull(mapsFolder.listFiles((dir, name) -> name.endsWith(".mapconfig")))) {
                MapHandler map = new MapHandler(knockFFA, mapFile.getName().replace(".mapconfig", ""));
                map.loadAll();
                l.add(map);
            }

            return maps = l;
        }

        public static void addMap(String map) {
            MapHandler mapHandler = new MapHandler(knockFFA, map);
            mapHandler.createMapFile();
            mapHandler.save();
            maps.add(mapHandler);
        }

        public static boolean mapExists(String map) {
            return maps.stream().anyMatch(mapHandler -> mapHandler.getMap().equalsIgnoreCase(map));
        }

        public static MapHandler getMap(String map) {
            //noinspection OptionalGetWithoutIsPresent
            return maps.stream().filter(mapHandler -> mapHandler.getMap().equalsIgnoreCase(map)).findFirst().get();
        }

        public static MapHandler getMapByName(String map) {
            //noinspection OptionalGetWithoutIsPresent
            return maps.stream().filter(mapHandler -> mapHandler.getMapName().equalsIgnoreCase(map)).findFirst().get();
        }

        public static List<MapHandler> shuffle(int number) {
            List<MapHandler> list = new ArrayList<>();
            List<MapHandler> temp = new ArrayList<>(maps);
            Collections.shuffle(temp);
            for (int i = 0; i < number; i++) {
                if (temp.size() == i) break;
                list.add(temp.get(i));
            }
            return list;
        }

        @EventHandler
        public void onJoin(PlayerSpawnLocationEvent e) {
            if (activeMap == null) return;
            try {
                e.setSpawnLocation(activeMap.getSpawn());
            } catch (NullPointerException ignored) {
            }
        }
    }

    @Override
    public String toString() {
        String str = "";
        str = "Map: " + this.hashCode() + ", File: " + mapFile.getName() + ", Spawn: " + spawn.toString() + ", Safezone: " + safezone + ", Deathzone: " + deathzone;
        return str;
    }
}

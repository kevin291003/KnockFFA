package de.kevin.knockffa;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.io.File;
import java.io.IOException;

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
            Bukkit.createWorld(creator);
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
            configuration.set("deathzone:", loc.getY());
        }
        return this;
    }

    public MapHandler setSafeZone(Location loc) {
        if (isLoaded()) {
            safezone = loc.getY();
            configuration.set("safezone:", loc.getY());
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

        @EventHandler
        public void onJoin(PlayerSpawnLocationEvent e) {
            try {
                e.setSpawnLocation(activeMap.getSpawn());
            } catch (NullPointerException ignored) {
            }
        }
    }

    @Override
    public String toString() {
        String str = "";
        str = "Map: " + this.hashCode() + ", File: " + mapFile.getName() + ", Spawn: " + spawn.toString();
        return str;
    }
}

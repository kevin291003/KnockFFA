package de.kevin.knockffa.commands;

import de.kevin.knockffa.KnockFFA;
import de.kevin.knockffa.MapHandler;
import de.kevin.knockffa.Message;
import de.kevin.knockffa.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Class to handle /map command
 */
public class MapCommand implements CommandExecutor {

    /**
     * Instance of {@link KnockFFA}
     */
    private final KnockFFA knockFFA;

    /**
     * List of all loaded maps
     */
    public static List<MapHandler> maps;

    /**
     * Map of all editing players and the maps
     */
    public static final HashMap<MapHandler, Player> editingPlayers = new HashMap<>();

    /**
     * Instantiate {@link MapCommand} and set main class
     * @param knockFFA {@link KnockFFA} instance
     */
    public MapCommand(KnockFFA knockFFA) {
        this.knockFFA = knockFFA;
        maps = MapHandler.MapSetter.maps;
    }

    /**
     * Method to handle command /map
     * @param commandSender who executes command
     * @param command executed command
     * @param s command string
     * @param strings command arguments
     * @return syntax okay
     */
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return true;
        Player p = (Player) commandSender;
        if (strings.length == 0) {
            Utils.sendMessage(p, false, "§e/map list");
            Utils.sendMessage(p, false, "§e/map load");
            Utils.sendMessage(p, false, "§e/map teleport <map>");
            Utils.sendMessage(p, false, "§e/map create <name>");
            Utils.sendMessage(p, false, "§e/map setup <map>");
            Utils.sendMessage(p, false, "§e/map set <death|safe|spawn|mapname|finished> [mapname]");

            return true;
        }

        /* /map ARGS[0] */
        if (strings.length == 1) {
            // /map load
            if (strings[0].equalsIgnoreCase("load")) {
                MapHandler.MapSetter.loadMaps(knockFFA);
                Utils.sendMessage(p, true, Message.getMessage("commands.mapcommand.maps_loaded"));
                p.performCommand("map list");
                return true;
            }

            // /map list
            if (strings[0].equalsIgnoreCase("list")) {
                Utils.sendMessage(p, true, Message.getMessage("commands.mapcommand.maps"));
                for (MapHandler map : maps) {
                    Utils.sendMessage(p, false, "§e" + map.getMap() + " ('" + map.getMapName() + "')");
                }
                return true;
            }

            // /map create
            if (strings[0].equalsIgnoreCase("create")) {
                Utils.sendMessage(p, false, "§c/map create <name>");
                return true;
            }

            // /map setup
            if (strings[0].equalsIgnoreCase("setup")) {
                if (isEditing(p)) {
                    getEditingMap(p).save();
                    setEditing(p, getEditingMap(p), false);
                    Utils.sendMessage(p, true, Message.getMessage("commands.mapcommand.map_setup_finished"));
                } else
                    Utils.sendMessage(p, true, "§c/map setup <map>");
                // TODO: Finish syntax
                return true;
            }

            // map set
            if (strings[0].equalsIgnoreCase("set")) {
                Utils.sendMessage(p, true, "§c/map set <mapname|spawn|safe|death|finished> [mapname title]");
            }
        }

        if (strings.length == 2) {
            if (strings[0].equalsIgnoreCase("delete")) {
                if (MapHandler.MapSetter.mapExists(strings[1])) {
                    MapHandler.MapSetter.deleteMap(strings[1]);
                    Utils.sendMessage(p, true, Message.getMessage("commands.mapcommand.map_deleted"));
                } else
                    Utils.sendMessage(p, true, Message.getMessage("commands.mapcommand.map_not_existing"));
            }

            if (strings[0].equalsIgnoreCase("teleport")) {
                try {
                    Utils.sendTitle(p, Message.getMessage("commands.mapcommand.map_teleport"), 20, 20*3, 20*3);
                    WorldCreator creator = new WorldCreator(strings[1]);
                    World w = Bukkit.createWorld(creator);
                    p.teleport(w.getSpawnLocation());
                } catch (NullPointerException ignored) {
                }
            }

            if (strings[0].equalsIgnoreCase("change")) {
                if (MapHandler.MapSetter.mapExists(strings[1])) {
                    MapHandler.MapSetter.changeMap(MapHandler.MapSetter.getMap(strings[1]));
                }
            }

            if (strings[0].equalsIgnoreCase("create")) {
                if (!MapHandler.MapSetter.mapExists(strings[1])) {
                    MapHandler.MapSetter.addMap(strings[1]);
                    Utils.sendMessage(p, true, Message.getMessage("commands.mapcommand.map_created"));
                } else
                    Utils.sendMessage(p, true, Message.getMessage("commands.mapcommand.map_already_exists"));
                return true;
            }

            if (strings[0].equalsIgnoreCase("info")) {
                if (MapHandler.MapSetter.mapExists(strings[1])) {
                    MapHandler tmp = MapHandler.MapSetter.getMap(strings[1]);
                    Utils.sendMessage(p, true, Message.getMessage("commands.mapcommand.maps"));
                    tmp.getConfiguration().getValues(true).forEach((s1, o) -> p.sendMessage("§e" + s1 + " - " + o.toString()));
                } else
                    Utils.sendMessage(p, true, Message.getMessage("commands.mapcommand.map_not_existing"));
                return true;
            }

            if (strings[0].equalsIgnoreCase("setup")) {
                if (MapHandler.MapSetter.mapExists(strings[1])) {
                    if (!isEditing(p)) {
                        maps.stream().filter(mapHandler1 -> mapHandler1.getMap().equalsIgnoreCase(strings[1])).findFirst().ifPresent(mapHandler -> {
                            setEditing(p, mapHandler, true);
                            mapHandler.setFinished(false);
                            Utils.sendMessage(p, true, Message.getMessage("commands.mapcommand.map_now_editing").replace("{map}", strings[1]));
                        });

                    } else
                        Utils.sendMessage(p, true, Message.getMessage("commands.mapcommand.map_already_editing").replace("{map}", getEditingMap(p).getMap()));
                } else {
                    Utils.sendMessage(p, true, Message.getMessage("commands.mapcommand.map_not_existing"));
                    p.performCommand("map list");
                }
                return true;
            }

            if (strings[0].equalsIgnoreCase("set")) {
                if (isEditing(p)) {
                    if (strings[1].equalsIgnoreCase("finished")) {
                        getEditingMap(p).getSpawn().getWorld().save();
                        getEditingMap(p).getSpawn().getWorld().setAutoSave(false);
                        getEditingMap(p).setFinished(true);
                        Utils.sendMessage(p, true, Message.getMessage("commands.mapcommand.map_mark_finished"));
                        p.performCommand("map setup");
                        return true;
                    }
                    if (strings[1].equalsIgnoreCase("spawn")) {
                        getEditingMap(p).setSpawnLocation(p.getLocation());
                        Utils.sendMessage(p, true, Message.getMessage("commands.mapcommand.map_set_spawn"));
                        return true;
                    }
                    if (strings[1].equalsIgnoreCase("safe")) {
                        getEditingMap(p).setSafeZone(p.getLocation());
                        Utils.sendMessage(p, true, Message.getMessage("commands.mapcommand.map_set_safezone"));
                        return true;
                    }
                    if (strings[1].equalsIgnoreCase("death")) {
                        getEditingMap(p).setDeathZone(p.getLocation());
                        Utils.sendMessage(p, true, Message.getMessage("commands.mapcommand.map_set_deathzone"));
                        return true;
                    }
                    p.performCommand("map set");
                } else
                    Utils.sendMessage(p, true, Message.getMessage("commands.mapcommand.map_not_editing"));
            }
        }

        if (strings.length >= 3) {
            if (strings[0].equalsIgnoreCase("set")) {
                if (isEditing(p)) {
                    if (strings[1].equalsIgnoreCase("mapname")) {
                        StringJoiner joiner = new StringJoiner(" ");
                        for (int i = 2; i < strings.length; i++) {
                            joiner.add(strings[i]);
                        }
                        getEditingMap(p).setMapName(joiner.toString());
                        Utils.sendMessage(p, true, Message.getMessage("commands.mapcommand.map_set_name").replace("{mapname}", joiner.toString()));
                    }
                } else
                    Utils.sendMessage(p, true, Message.getMessage("commands.mapcommand.map_not_editing"));
            }
        }

        return true;
    }

    /**
     * Get map of all editing players
     * @return editingPlayers map
     */
    @SuppressWarnings("SameReturnValue")
    public static HashMap<MapHandler, Player> getEditingPlayers() {
        return editingPlayers;
    }

    /**
     * Check if map is edited by a player
     * @param mapHandler the {@link MapHandler} to check
     * @return true if map is being edited
     */
    public boolean isEdited(MapHandler mapHandler) {
        return getEditingPlayers().containsKey(mapHandler);
    }

    /**
     * Check if player is editing a map
     * @param p Player to check
     * @return true if player is editing a map
     */
    public boolean isEditing(Player p) {
        return getEditingPlayers().containsValue(p) && isEdited(getEditingMap(p));
    }

    /**
     * Get the {@link MapHandler} of the editing player
     * @param p the player who edits a map
     * @return {@link MapHandler} of player if he is editing else null
     */
    public MapHandler getEditingMap(Player p) {
        return getEditingPlayers().entrySet().stream().filter(map -> map.getValue() == p).findFirst().map(Map.Entry::getKey).orElse(null);
    }

    /**
     * Set editing state of player and {@link MapHandler}
     * @param p the player to set editing
     * @param mapHandler the {@link MapHandler} to set being edited
     * @param editing editing state
     */
    public void setEditing(Player p, MapHandler mapHandler, boolean editing) {
        if (editing)
            getEditingPlayers().put(mapHandler, p);
        else
            getEditingPlayers().remove(mapHandler, p);
    }
}

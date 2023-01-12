package de.kevin.knockffa.commands;

import de.kevin.knockffa.KnockFFA;
import de.kevin.knockffa.MapHandler;
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

public class MapCommand implements CommandExecutor {

    private final KnockFFA knockFFA;
    public static List<MapHandler> maps;
    public static HashMap<MapHandler, Player> editingPlayers = new HashMap<>();


    public MapCommand(KnockFFA knockFFA) {
        this.knockFFA = knockFFA;
        maps = MapHandler.MapSetter.maps;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        if (args.length == 0) {
            Utils.sendMessage(p, false, "§eAuflisten: /map list");
            Utils.sendMessage(p, false, "§eMaps laden: /map load");
            Utils.sendMessage(p, false, "§eZu Map teleportieren: /map teleport <map>");
            Utils.sendMessage(p, false, "§eErstellen: /map create <name>");
            Utils.sendMessage(p, false, "§eMap Setup: /map setup <map>");
            Utils.sendMessage(p, false, "§eMap Einstellungen: /map set <death|safe|spawn|mapname|finished> [mapname]");

            return true;
        }

        /* /map ARGS[0] */
        if (args.length == 1) {
            // /map load
            if (args[0].equalsIgnoreCase("load")) {
                MapHandler.MapSetter.loadMaps(knockFFA);
                Utils.sendMessage(p, true, "§aMaps geladen.");
                p.performCommand("map list");
                return true;
            }

            // /map list
            if (args[0].equalsIgnoreCase("list")) {
                Utils.sendMessage(p, true, "§e§nMaps:");
                for (MapHandler map : maps) {
                    Utils.sendMessage(p, false, "§e" + map.getMap() + " ('" + map.getMapName() + "')");
                }
                return true;
            }

            // /map create
            if (args[0].equalsIgnoreCase("create")) {
                Utils.sendMessage(p, false, "§c/map create <name>");
                return true;
            }

            // /map setup
            if (args[0].equalsIgnoreCase("setup")) {
                if (isEditing(p)) {
                    getEditingMap(p).save();
                    setEditing(p, getEditingMap(p), false);
                    Utils.sendMessage(p, true, "§aD§lu hast das Setup beendet und die Map wurde gespeichert!");
                } else
                    Utils.sendMessage(p, true, "§c/map setup <map>");
                // TODO: Finish syntax
                return true;
            }

            // map set
            if (args[0].equalsIgnoreCase("set")) {
                Utils.sendMessage(p, true, "§c/map set <mapname|spawn|safe|death|finished> [mapname title]");
            }
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("teleport")) {
                try {
                    Utils.sendTitle(p, "§eTeleportiere...", 20, 20*3, 20*3);
                    WorldCreator creator = new WorldCreator(args[1]);
                    World w = Bukkit.createWorld(creator);
                    p.teleport(w.getSpawnLocation());
                } catch (NullPointerException ignored) {
                }
            }

            if (args[0].equalsIgnoreCase("change")) {
                if (MapHandler.MapSetter.mapExists(args[1])) {
                    MapHandler.MapSetter.changeMap(MapHandler.MapSetter.getMap(args[1]));
                }
            }

            if (args[0].equalsIgnoreCase("create")) {
                if (!MapHandler.MapSetter.mapExists(args[1])) {
                    MapHandler.MapSetter.addMap(args[1]);
                    Utils.sendMessage(p, true, "§aMap wurde erstellt.");
                } else
                    Utils.sendMessage(p, true, "§cMap existiert bereits!");
                return true;
            }

            if (args[0].equalsIgnoreCase("info")) {
                if (MapHandler.MapSetter.mapExists(args[1])) {
                    MapHandler tmp = MapHandler.MapSetter.getMap(args[1]);
                    Utils.sendMessage(p, true, "§eMap Informationen:");
                    tmp.getConfiguration().getValues(true).forEach((s1, o) -> p.sendMessage("§e" + s1 + " - " + o.toString()));
                } else
                    Utils.sendMessage(p, true, "§cMap existiert nicht!");
                return true;
            }

            if (args[0].equalsIgnoreCase("setup")) {
                if (MapHandler.MapSetter.mapExists(args[1])) {
                    if (!isEditing(p)) {
                        maps.stream().filter(mapHandler1 -> mapHandler1.getMap().equalsIgnoreCase(args[1])).findFirst().ifPresent(mapHandler -> {
                            setEditing(p, mapHandler, true);
                            mapHandler.setFinished(false);
                            Utils.sendMessage(p, true, "§eDu editierst nun die Map: " + args[1]);
                        });

                    } else
                        Utils.sendMessage(p, true, "§cDu editierst bereits " + getEditingMap(p).getMap());
                } else {
                    Utils.sendMessage(p, true, "§cMap existiert nicht!");
                    p.performCommand("map list");
                }
                return true;
            }

            if (args[0].equalsIgnoreCase("set")) {
                if (isEditing(p)) {
                    if (args[1].equalsIgnoreCase("finished")) {
                        getEditingMap(p).setFinished(true);
                        Utils.sendMessage(p, true, "§aDu hast die Map als fertig markiert.");
                        p.performCommand("map setup");
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("spawn")) {
                        getEditingMap(p).setSpawnLocation(p.getLocation());
                        Utils.sendMessage(p, true, "§eDu hast den Spawn gesetzt.");
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("safe")) {
                        getEditingMap(p).setSafeZone(p.getLocation());
                        Utils.sendMessage(p, true, "§aDu hast die Safe-Höhe festgelegt.");
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("death")) {
                        getEditingMap(p).setDeathZone(p.getLocation());
                        Utils.sendMessage(p, true, "§aDu hast die Todes-Höhe festgelegt.");
                        return true;
                    }
                    p.performCommand("map set");
                } else
                    Utils.sendMessage(p, true, "§cDu bearbeitest keine Map!");
            }
        }

        if (args.length >= 3) {
            if (args[0].equalsIgnoreCase("set")) {
                if (isEditing(p)) {
                    if (args[1].equalsIgnoreCase("mapname")) {
                        StringJoiner joiner = new StringJoiner(" ");
                        for (int i = 2; i < args.length; i++) {
                            joiner.add(args[i]);
                        }
                        getEditingMap(p).setMapName(joiner.toString());
                        Utils.sendMessage(p, true, "§aDer Mapname wurde in '" + joiner + "' geändert.");
                    }
                } else
                    Utils.sendMessage(p, true, "§cDu bearbeitest keine Map!");
            }
        }

        return true;
    }

    public static HashMap<MapHandler, Player> getEditingPlayers() {
        return editingPlayers;
    }

    public boolean isEdited(MapHandler mapHandler) {
        return getEditingPlayers().containsKey(mapHandler);
    }

    public boolean isEditing(Player p) {
        return getEditingPlayers().containsValue(p) && isEdited(getEditingMap(p));
    }

    public MapHandler getEditingMap(Player p) {
        return getEditingPlayers().entrySet().stream().filter(map -> map.getValue() == p).findFirst().map(Map.Entry::getKey).orElse(null);
    }

    public void setEditing(Player p, MapHandler mapHandler, boolean editing) {
        if (editing)
            getEditingPlayers().put(mapHandler, p);
        else
            getEditingPlayers().remove(mapHandler, p);
    }
}

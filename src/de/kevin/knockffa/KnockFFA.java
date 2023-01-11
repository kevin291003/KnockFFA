package de.kevin.knockffa;

import de.kevin.knockffa.commands.FFACommand;
import de.kevin.knockffa.commands.MapCommand;
import de.kevin.knockffa.commands.TitleCommand;
import de.kevin.knockffa.commands.Top10Command;
import de.kevin.knockffa.database.Database;
import de.kevin.knockffa.events.GamePlayEvents;
import de.kevin.knockffa.inventory.CommandsInventoryHandler;
import de.kevin.knockffa.inventory.KitInventoryHandler;
import de.kevin.knockffa.inventory.StartInventoryHandler;
import de.kevin.knockffa.events.Leave;
import de.kevin.knockffa.events.RegisterUser;
import de.kevin.knockffa.inventory.Top10InventoryHandler;
import de.kevin.knockffa.webserver.KnockFFAWebserver;
import de.kevin.websocket.ServerSocketThread;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.sql.SQLException;
import java.util.List;

/**
 * The type Knock ffa.
 */
public final class KnockFFA extends JavaPlugin {

    /**
     * Use this to get the main class
     *
     * @return The plugin main class
     */
    public KnockFFA getKnockFFA() {
        return knockFFA;
    }

    private void setKnockFFA(KnockFFA knockFFA) {
        this.knockFFA = knockFFA;
    }

    private KnockFFA knockFFA;
    private Database database;

    private ConfigHandler configHandler;

    private ServerSocketThread serverSocketThread;
    public Database getDB() {
        return database;
    }

    public static String getPrefix() {
        return "§7§l[§6§lKnockFFA§7§l] §r";
    }

    public static KnockFFAWebserver webserver;

    @Override
    public void onEnable() {
        setKnockFFA(this);
        Logging.logger = getLogger();
        Logging.knockFFA = getKnockFFA();

        configHandler = new ConfigHandler(this,"config.yml");

        //noinspection ResultOfMethodCallIgnored
        getDataFolder().mkdirs();
        //noinspection ResultOfMethodCallIgnored
        new File(getDataFolder() + "/logs/").mkdirs();
        database = new Database(this);
        database.connect("database.db");

        createWebserverFiles();

        getServer().getPluginManager().registerEvents(new RegisterUser(this), this);
        getServer().getPluginManager().registerEvents(new Leave(this), this);
        getServer().getPluginManager().registerEvents(new GamePlayEvents(this), this);
        // Inventare
        getServer().getPluginManager().registerEvents(new StartInventoryHandler(), this);
        getServer().getPluginManager().registerEvents(new CommandsInventoryHandler(this), this);
        getServer().getPluginManager().registerEvents(new Top10InventoryHandler(this), this);
        getServer().getPluginManager().registerEvents(new KitInventoryHandler(this), this);

        getServer().getPluginManager().registerEvents(new MapHandler.MapSetter(), this);
        command("top10", "", new Top10Command(this));
        command("title", "<text...>", new TitleCommand());
        command("knockffa", "", new FFACommand());
        command("map", "", new MapCommand(knockFFA));


        Logging.info("Starte Webserver...");
        Bukkit.getScheduler().runTaskLater(this, () -> {
            int port = 0;
            try {
                port = configHandler.getConfiguration().getInt("webserver-port");
                webserver = new KnockFFAWebserver(knockFFA);
                webserver.start(port);
            } catch (IOException e) {
                Logging.warning("Der gewünschte Port ist nicht verfügbar. Suche nach freien Port...");
            }
        }, 5);

        MapCommand.loadMaps(this);
        MapHandler.MapSetter.activeMap = MapCommand.maps.stream().findFirst().orElse(null);

        for (MapHandler map : MapCommand.maps) {
            System.out.println(map.toString());
        }

        Logging.info("Das Plugin wurde aktiviert.");
    }

    private void createWebserverFiles() {
        String[] fileNames = {"error.html", "index.html", "main.html", "top10.html", "user.html", "users.html"};
        for (String fileName : fileNames) {
            File file = new File(getKnockFFA().getDataFolder() + "/webserver", fileName);
            if (!file.exists()) {
                //noinspection ResultOfMethodCallIgnored
                file.getParentFile().mkdirs();
            }
            getKnockFFA().saveResource("webserver/" + fileName, true);
        }
    }

    @Override
    public void onDisable() {
        webserver.stop();

        try {
            getDB().getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Logging.info("Plugin disabled.");
    }

    private void command(String command, String args, CommandExecutor cmdClass) {
        getCommand(command).setPermission("knockffa.command." + command);
        getCommand(command).setPermissionMessage(getPrefix() + "§cDu hast keine Berechtigung!");
        getCommand(command).setUsage(getPrefix() + "§c/<command> " + args);
        getCommand(command).setExecutor(cmdClass);
    }
}

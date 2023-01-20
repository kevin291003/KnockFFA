package de.kevin.knockffa;

import de.kevin.knockffa.commands.*;
import de.kevin.knockffa.database.Database;
import de.kevin.knockffa.events.GamePlayEvents;
import de.kevin.knockffa.events.Leave;
import de.kevin.knockffa.events.RegisterUser;
import de.kevin.knockffa.inventory.*;
import de.kevin.knockffa.webserver.KnockFFAWebserver;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.Configuration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Main class of plugin KnockFFA
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

    /**
     * @param knockFFA Instance of main class
     */
    private void setKnockFFA(KnockFFA knockFFA) {
        this.knockFFA = knockFFA;
    }

    private KnockFFA knockFFA;
    private Database database;

    private ConfigHandler configHandler;

    public Database getDB() {
        return database;
    }

    @SuppressWarnings("SameReturnValue")
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
        prepareSettings();


        Message.Message(this, configHandler.getConfiguration().getString("language"));

        //noinspection ResultOfMethodCallIgnored
        getDataFolder().mkdirs();
        //noinspection ResultOfMethodCallIgnored
        new File(getDataFolder() + "/logs/").mkdirs();
        database = new Database(this);
        database.connect("database.db");

        createWebserverFiles();

        // Events
        getServer().getPluginManager().registerEvents(new RegisterUser(this), this);
        getServer().getPluginManager().registerEvents(new Leave(), this);
        getServer().getPluginManager().registerEvents(new GamePlayEvents(this), this);

        // Inventare
        getServer().getPluginManager().registerEvents(new StartInventoryHandler(), this);
        getServer().getPluginManager().registerEvents(new CommandsInventoryHandler(this), this);
        getServer().getPluginManager().registerEvents(new Top10InventoryHandler(this), this);
        getServer().getPluginManager().registerEvents(new KitInventoryHandler(this), this);
        getServer().getPluginManager().registerEvents(new VotingInventoryHandler(this), this);
        getServer().getPluginManager().registerEvents(new VotingChangeInventoryHandler(this), this);

        // GamePlay/MapSetter
        getServer().getPluginManager().registerEvents(new MapHandler.MapSetter(), this);


        command("top10", "", new Top10Command(this));
        command("stats", "", new StatsCommand(this));
        command("knockffa", "", new FFACommand());
        command("map", "", new MapCommand(this));
        command("mapvote", "<map name>", new MapVoteCommand());


        Logging.info("Starting Webserver...");
        Bukkit.getScheduler().runTaskLater(this, () -> {
            int port;
            try {
                port = configHandler.getConfiguration().getInt("webserver-port");
                webserver = new KnockFFAWebserver(knockFFA);
                webserver.start(port);
            } catch (IOException e) {
                Logging.warning("The selected port is not available.");
            }
        }, 5);

        MapHandler.MapSetter.initialize(this);

        Bukkit.getOnlinePlayers().forEach(player -> {
            KitInventoryHandler.setKit(player, "Standard");
        });

        GamePlay.setKnockFFA(this);
        GamePlay.EventHandling();


        Logging.info(getDescription().getName() + " v" + getDescription().getVersion() + " activated.");
    }

    private void prepareSettings() {
        Configuration c = configHandler.getConfiguration();
        Settings.MinPlayerForVoting = c.getInt("players_for_voting");
        Settings.CustomChat = c.getBoolean("custom_chatformat");
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

        for (MapHandler mapHandler : MapCommand.maps) {
            if (mapHandler.isFinished())
                Bukkit.unloadWorld(mapHandler.getWorldString(), false);
        }

        Logging.info(getDescription().getName() + " disabled.");
    }

    private void command(String command, String args, CommandExecutor cmdClass) {
        getCommand(command).setPermission("knockffa.command." + command);
        getCommand(command).setPermissionMessage(getPrefix() + Message.getMessage("commands.no_permission"));
        getCommand(command).setUsage(getPrefix() + "§c/<command> " + args);
        getCommand(command).setExecutor(cmdClass);
    }

    public static class Settings {


        /**
         * Option of custom chat formatting and coloring
         */
        public static boolean CustomChat = false;

        public static boolean GamePlay = false;

        public static int MinPlayerForVoting = 3;

        public void doc() {

        }

    }

}

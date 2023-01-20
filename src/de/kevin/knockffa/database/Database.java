package de.kevin.knockffa.database;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.kevin.knockffa.ConfigHandler;
import de.kevin.knockffa.GamePlay;
import de.kevin.knockffa.KnockFFA;
import de.kevin.knockffa.Logging;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This class is used to handle all database features needed to store and get information
 */
public class Database {

    /**
     * Connection field
     */
    private Connection con;

    /**
     * Instance of {@link KnockFFA} class
     */
    public final KnockFFA knockFFA;

    /**
     * use local database file or mysql connection
     */
    private final boolean useFile;

    /**
     * Host of mysql connection
     */
    private String _HOST = "localhost";

    /**
     * Port of mysql connection
     */
    private String _PORT = "3306";

    /**
     * Database of mysql connection
     */
    private String _DATABASE = "knockffa";

    /**
     * Username of mysql connection
     */
    private String _USERNAME = "username";

    /**
     * Password of mysql connection
     */
    private String _PASSWORD = "password";

    /**
     * Instantiate class and prepare connection
     * @param knockFFA Instance of {@link KnockFFA} class
     */
    public Database(KnockFFA knockFFA) {
        this.knockFFA = knockFFA;
        ConfigHandler config = new ConfigHandler(knockFFA, "database.yml");
        Configuration conf = config.getConfiguration();
        useFile = conf.get("connection").toString().equalsIgnoreCase("file");

        if (!useFile) {
            _HOST = conf.getString("mysql.host");
            _PORT = conf.getString("mysql.port");
            _DATABASE = conf.getString("mysql.database");
            _USERNAME = conf.getString("mysql.user");
            _PASSWORD = conf.getString("mysql.password");
        }
    }

    /**
     * Get mysql connection
     * @return Connection of database
     */
    public Connection getConnection() {
        return con;
    }

    /**
     * Connect to database
     * @param fileName database file name
     */
    public void connect(String fileName) {
        if (!isConnected()) {
            String url = useFile ? "jdbc:sqlite:plugins/KnockFFA/" + fileName : "jdbc:mysql://" + _HOST + ":" + _PORT + "/" + _DATABASE;

            try {
                if (useFile) {
                    Class.forName("org.sqlite.JDBC");
                } else {
                    Class.forName("com.mysql.jdbc.Driver");
                }
            } catch (ClassNotFoundException ignored) {
            }

            try {
                con = useFile ? DriverManager.getConnection(url) : DriverManager.getConnection(url + "?user=" + _USERNAME + "&password=" + _PASSWORD);
                if (con == null)
                    Logging.error("Database: Could not connect.");
                else
                    Logging.info("Database: Connected.");
            } catch (SQLException e) {
                e.printStackTrace();
                Logging.error(e.getLocalizedMessage());
            }
            createTables();
        }
    }

    /**
     * Check if database is connected
     * @return true if database connection is there, false otherwise
     */
    public boolean isConnected() {
        try {
            return con != null && !con.isClosed();
        } catch (SQLException e) {
            Logging.error("Database: No connection!", e.getLocalizedMessage());
        }
        return false;
    }

    /**
     * Execute a query
     * @param qry Query to execute
     */
    public void update(String qry) {
        if (isConnected()) {
            try {
                con.createStatement().executeUpdate(qry);
            } catch (SQLException e) {
                Logging.error("Database: Could not execute query! '" + qry + "'");
                e.printStackTrace();
            }
        }
    }

    /**
     * Execute a statement and get the result
     * @param qry Query to execute
     * @return ResultSet of the query
     */
    public ResultSet getResult(String qry) {
        if (isConnected()) {
            try {
                return con.createStatement().executeQuery(qry);
            } catch (SQLException e) {
                Logging.error("Database: Could not execute query. '" + qry + "'");
            }
        }
        return null;
    }

    /**
     * Creates the database tables if needed
     */
    public void createTables() {
        update("CREATE TABLE IF NOT EXISTS users (uuid VARCHAR(36) NOT NULL, username VARCHAR(16) DEFAULT '', PRIMARY KEY (uuid));");
        update("CREATE TABLE IF NOT EXISTS stats (uuid VARCHAR(36) NOT NULL, coins INTEGER(11) DEFAULT 0, score INTEGER(11) DEFAULT 0, kills INTEGER(11) DEFAULT 0, deaths INTEGER(11) DEFAULT 0, kits TEXT DEFAULT 'Standard;', PRIMARY KEY(uuid));");
    }

    /**
     * Check if user exists in database
     * @param offlinePlayer player to check
     * @return true if player is registered, false otherwise
     */
    public boolean userExists(OfflinePlayer offlinePlayer) {
        try {
            return getResult("SELECT uuid FROM users WHERE uuid='" + offlinePlayer.getUniqueId().toString() + "';").next();
        } catch (SQLException e) {
            Logging.error("Database: Could not execute query.");
            return false;
        }
    }

    /**
     * Register player to database
     * @param offlinePlayer player to register
     */
    public void registerUser(OfflinePlayer offlinePlayer) {
        update("INSERT INTO users (uuid, username) VALUES ('" + offlinePlayer.getUniqueId().toString() + "', '" + offlinePlayer.getName() + "');");
        update("INSERT INTO stats (uuid) VALUES ('" + offlinePlayer.getUniqueId() + "')");
        Logging.debug("User " + offlinePlayer.getName() + " was registered in the database.");
    }

    /**
     * Get username of player
     * @param uuid UUID of player to get username from
     * @return username of existing player else Notch
     */
    @SuppressWarnings("ReturnInsideFinallyBlock")
    public String getUsername(UUID uuid) {
        //noinspection finally
        try {
            ResultSet rs = getResult("SELECT username FROM users WHERE uuid='" + uuid.toString() + "';");
            if (rs.next())
                return rs.getString("username");
        } catch (SQLException e) {
            Logging.error("Database: Could not execute query.");
        } finally {
            return Bukkit.getOfflinePlayer(uuid).getName();
        }
    }

    /**
     * Get uuid of player
     * @param username username of player to get uuid from
     * @return uuid of existing player else Notch
     */
    public UUID getUUID(String username) {
        try {
            ResultSet rs = getResult("SELECT uuid FROM users WHERE username='" + username + "';");
            if (rs.next())
                return UUID.fromString(rs.getString("uuid"));
        } catch (SQLException e) {
            Logging.error("Database: Could not execute query.");
        }
        return UUID.randomUUID();
    }

    /**
     * get top10 player list with their scores
     * .get("username") - Username
     * .get("score") - Score
     * @return List of top10 players and their scores
     */
    public List<Map<String, Object>> getTop10AsList() {
        List<Map<String, Object>> s = Lists.newArrayList();

        ResultSet rs = getResult("SELECT u.username AS username, s.score AS score FROM stats AS s INNER JOIN users AS u WHERE u.uuid=s.uuid AND s.score > 0 ORDER BY score DESC LIMIT 10;");

        try {
            while (rs != null && rs.next()) {
                Map<String, Object> daten = Maps.newHashMap();
                daten.put("username", rs.getString("username"));
                daten.put("score", rs.getString("score"));
                s.add(daten);
            }
        } catch (SQLException ignored) {
        }
        return s;
    }

    /**
     * get stats of player
     * .get("username") - Username
     * .get("kills") - Kills
     * .get("deaths") - Deaths
     * .get("coins") - Coins
     * .get("score") - Score
     * .get("rank") - Rank
     * @param player the player to get stats from
     * @return map of stats
     */
    public Map<String, Object> getStats(OfflinePlayer player) {
        if (player == null) return null;
        ResultSet rs = getResult("SELECT u.uuid AS uuid, u.username AS username, s.kills AS kills, s.deaths AS deaths, s.coins AS coins, s.score AS score FROM stats AS s INNER JOIN users AS u WHERE u.uuid=s.uuid ORDER BY score DESC;");
        Map<String, Object> daten = Maps.newHashMap();
        try {
            while (rs != null && rs.next()) {
                if (!rs.getString("uuid").equals(player.getUniqueId().toString())) continue;
                daten.put("username", rs.getString("username"));
                daten.put("kills", rs.getInt("kills"));
                daten.put("deaths", rs.getInt("deaths"));
                daten.put("coins", rs.getInt("coins"));
                daten.put("score", rs.getInt("score"));
                daten.put("rank", rs.getRow());
                return daten;
            }
        } catch (SQLException ignored) {
        }
        return daten;
    }

    /**
     * Check if player has a kit
     * @param offlinePlayer player to check if he has kit
     * @param kit name of the kit
     * @return true if he has kit access, false otherwise
     */
    public boolean hasKit(OfflinePlayer offlinePlayer, String kit) {
        if (offlinePlayer.isOnline())
            if (Bukkit.getPlayer(offlinePlayer.getUniqueId()).hasPermission("knockffa.kits." + kit.toLowerCase()) || (GamePlay.kitEvent && GamePlay.kitEventKit.equals(kit)))
                return true;
        return getKits(offlinePlayer).contains(kit + ";");
    }

    /**
     * Get all kits of a player
     * @param offlinePlayer player to get kits
     * @return kits as string
     */
    public String getKits(OfflinePlayer offlinePlayer) {
        String kits = "Standard;";
        try {
            ResultSet rs = getResult("SELECT kits FROM stats WHERE uuid='" + offlinePlayer.getUniqueId().toString() + "';");
            if (rs.next()) {
                return rs.getString("kits");
            }
        } catch (SQLException e) {
            Logging.error("Database: Could not execute query.");
        }
        return kits;
    }

    /**
     * Give the player a new kit
     * @param offlinePlayer the player who gets a kit added
     * @param kit the kit to be given access to
     */
    public void addKit(OfflinePlayer offlinePlayer, String kit) {
        String newKits = getKits(offlinePlayer) + kit + ";";
        update("UPDATE stats SET kits='" + newKits + "' WHERE uuid='" + offlinePlayer.getUniqueId().toString() + "';");
    }

    /**
     * Remove a kit of the player
     * @param offlinePlayer the player who gets a kit removed
     * @param kit the kit to be removed
     */
    public void removeKit(OfflinePlayer offlinePlayer, String kit) {
        String newKits = getKits(offlinePlayer).replace(kit + ";", "");
        update("UPDATE stats SET kits='" + newKits + "' WHERE uuid='" + offlinePlayer.getUniqueId().toString() + "';");
    }

    /**
     * The stats to update
     */
    public enum Stats {

        /**
         * Update kills
         */
        KILLS("kills"),
        /**
         * Update deaths
         */
        DEATHS("deaths"),
        /**
         * Update score
         */
        SCORE("score"),
        /**
         * Update coins
         */
        COINS("coins");

        /**
         * The column name of the stats
         */
        final String name;

        Stats(String name) {
            this.name = name;
        }

    }

    /**
     * The reason why stats are updated
     */
    public enum Reason {

        /**
         * Player died because of falling out of the world
         */
        DEATH_BY_VOID(-10, 0),
        /**
         * Player died because of a player
         */
        DEATH_BY_PLAYER(-4, 0),
        /**
         * Player died because of lightning
         */
        DEATH_BY_LIGHTNING(-2, -5),
        /**
         * Player killed a player
         */
        KILL_BY_PLAYER(15, 10),
        /**
         * Player killed a player with lightnings
         */
        KILL_BY_LIGHTNING(10, 5);

        /**
         * The score value to update
         */
        final int score;
        /**
         * The coins value to update
         */
        final int coins;

        Reason(int score, int coins) {
            this.score = score;
            this.coins = coins;
        }
    }

    /**
     * Get a players stats value
     * @param offlinePlayer the player to get stats from
     * @param stats the stats to be fetched
     * @return stats value
     */
    public int getStat(OfflinePlayer offlinePlayer, Stats stats) {
        try {
            ResultSet rs = getResult("SELECT " + stats.name + " FROM stats WHERE uuid='" + offlinePlayer.getUniqueId().toString() + "';");
            return rs.getInt(stats.name);
        } catch (SQLException e) {
            Logging.error("Database: Could not execute query.");
        }
        return 0;
    }

    /**
     * Update the stats of a player
     * @param offlinePlayer the player whose stats to update
     * @param stats the stats to be updated
     * @param value the value how the stats are modified
     */
    public void addStat(OfflinePlayer offlinePlayer, Stats stats, int value) {
        int before = getStat(offlinePlayer, stats);
        update("UPDATE stats SET " + stats.name + "=" + (Math.max(before + value, 0)) + " WHERE uuid='" + offlinePlayer.getUniqueId().toString() + "';");
    }

    /**
     * Update coins and score of a player depending on the reason
     * @param offlinePlayer the player whose stats to be updated
     * @param reason the reason why the stats are getting updated
     */
    public void addStat(OfflinePlayer offlinePlayer, Reason reason) {
        addStat(offlinePlayer, Stats.SCORE, reason.score);
        addStat(offlinePlayer, Stats.COINS, reason.coins);
    }


    // TODO: 18.01.2023 stats handling

}

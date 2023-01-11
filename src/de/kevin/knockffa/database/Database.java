package de.kevin.knockffa.database;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import de.kevin.knockffa.KnockFFA;
import de.kevin.knockffa.Logging;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bukkit.OfflinePlayer;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This class is used to handle all Database features needed to store information.
 */
public class Database {

    private Connection con;
    public final KnockFFA knockFFA;

    public Database(KnockFFA knockFFA) {
        this.knockFFA = knockFFA;
    }

    public Connection getConnection() {
        return con;
    }
    /**
     * Initialize.
     * <p>
     * Creates the database file if needed and connects to it.
     *
     * @param fileName the database file name
     */
    public void connect(String fileName) {
        if (!isConnected()) {
            String url = "jdbc:sqlite:plugins/KnockFFA/" + fileName;

            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException ignored) {
            }

            try {
                con = DriverManager.getConnection(url);
                if (con == null) {
                    Logging.error("Keine Verbindung zur Datenbank!");
                }
            } catch (SQLException e) {
                Logging.error(e.getMessage());
            }

            createTables();
        }
    }

    public boolean isConnected() {
        try {
            return con != null && !con.isClosed();
        } catch (SQLException e) {
            Logging.error("Keine Verbindung zur Datenbank!", e.getLocalizedMessage());
        }
        return false;
    }

    public void update(String qry) {
        if (isConnected()) {
            try {
                con.createStatement().executeUpdate(qry);
            } catch (SQLException e) {
                Logging.error("Konnte den SQL Befehl nicht ausführen! '" + qry + "'");
            }
        }
    }

    public ResultSet getResult(String qry) {
        if (isConnected()) {
            try {
                return con.createStatement().executeQuery(qry);
            } catch (SQLException e) {
                Logging.error("Fehler bei SQL Abfrage: '" + qry + "'");
            }
        }
        return null;
    }

    /**
     * Tabelle Nutzer:<pre>
     * ╔════════╦═══════════════════╦════════╦════════════╗
     * ║ id     ║ uuid              ║ joined ║ username   ║
     * ╠════════╬═══════════════════╬════════╬════════════╣
     * ║ INT PK ║ STRING(36) UNIQUE ║ TEXT   ║ STRING(16) ║
     * ╚════════╩═══════════════════╩════════╩════════════╝</pre>
     * Tabelle Stats:<pre>
     * ╔═══════════════════╦═══════════════╦═══════════════╦═══════════════╦═══════════════╗
     * ║ uuid              ║ coins         ║ score         ║ kills         ║ deaths        ║
     * ╠═══════════════════╬═══════════════╬═══════════════╬═══════════════╬═══════════════╣
     * ║ STRING(36) UNIQUE ║ INT DEFAULT 0 ║ INT DEFAULT 0 ║ INT DEFAULT 0 ║ INT DEFAULT 0 ║
     * ╚═══════════════════╩═══════════════╩═══════════════╩═══════════════╩═══════════════╝
     * </pre>
     */
    public void createTables() {
        update("CREATE TABLE IF NOT EXISTS users (    id       INTEGER     PRIMARY KEY ASC AUTOINCREMENT,    uuid     STRING (36) UNIQUE, joined TEXT,    username STRING (16))");
        update("CREATE TABLE IF NOT EXISTS \"scores\" (\n" +
                "\t\"uuid\"\tSTRING(36),\n" +
                "\t\"coins\"\tINTEGER DEFAULT 0,\n" +
                "\t\"score\"\tINTEGER DEFAULT 0,\n" +
                "\t\"kills\"\tINTEGER DEFAULT 0,\n" +
                "\t\"deaths\"\tINTEGER DEFAULT 0)");
    }

    public boolean userExists(OfflinePlayer offlinePlayer) {
        try {
            return getResult("SELECT id FROM users WHERE uuid='" + offlinePlayer.getUniqueId().toString() + "';").next();
        } catch (SQLException e) {
            Logging.error("Database: Could not execute query.");
            return false;
        }
    }

    public void registerUser(OfflinePlayer offlinePlayer) {
        update("INSERT INTO users VALUES (NULL, '" + offlinePlayer.getUniqueId().toString() + "', '" + System.currentTimeMillis() + "', '" + offlinePlayer.getName() + "');");
        update("INSERT INTO scores (uuid) VALUES ('" + offlinePlayer.getUniqueId() + "')");
        Logging.debug("Spieler " + offlinePlayer.getName() + " mit der UUID " + offlinePlayer.getUniqueId() + " wurde in der Datenbank registriert.");
    }

    /**
     * .get("username") - Nutzername
     * .get("score") - Punkte
    */
    public List<Map<String, Object>> getTop10AsList() {
        List<Map<String, Object>> s = Lists.newArrayList();

        ResultSet rs = getResult("SELECT u.username AS username, s.score AS score FROM scores AS s INNER JOIN users AS u WHERE u.uuid=s.uuid AND s.score > 0 ORDER BY score DESC LIMIT 10;");

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

    public boolean hasKit(OfflinePlayer offlinePlayer, String kit) {
        try {
            return getResult("SELECT kits FROM scores WHERE uuid='" + offlinePlayer.getUniqueId().toString() + "';").getString("kits").contains(kit + ";");
        } catch (SQLException e) {
            Logging.error("Database: Could not execute query.");
            return false;
        }
    }

}

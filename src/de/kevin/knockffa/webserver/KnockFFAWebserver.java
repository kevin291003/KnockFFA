package de.kevin.knockffa.webserver;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import de.kevin.knockffa.KnockFFA;
import de.kevin.knockffa.Logging;

import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class KnockFFAWebserver {

    private final KnockFFA knockFFA;

    private HttpServer httpServer;
    private HttpContext rootContext; // Main page '/'

    public KnockFFAWebserver(KnockFFA knockFFA) {
        this.knockFFA = knockFFA;
    }

    public void start(int port) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(port), 0);

        rootContext = httpServer.createContext("/");
        rootContext.setHandler(this::handleRoot);

        httpServer.start();
        Logging.debug("Webserver started.");
        Logging.info("Webserver located at: http://" + InetAddress.getLocalHost().getCanonicalHostName() + ":" + httpServer.getAddress().getPort());
    }

    public void stop() {
        if (httpServer == null) return;
        httpServer.removeContext(rootContext);
        httpServer.stop(0);
        Logging.debug("Webserver stopped.");
    }

    public void handleRoot(HttpExchange exchange) throws IOException {
        URI requestURI = exchange.getRequestURI();
        String query = requestURI.getQuery();
        String response = "";

        if (query == null || query.isEmpty())
            query = "show=start&";

        String[] arr = query.split("&");
        HashMap<String, String> map = new HashMap<>();

        for ( String s : arr) {
            String[] tmp = s.split("=");
            map.put(tmp[0], tmp.length > 1 ? tmp[1] : "NULL");
        }
        response = error("The plugin is not correctly loaded.");

        if (map.containsKey("show")) {
            // --- Sites ---
            // Home
            if (map.get("show").equals("start"))
                //response = getHTML("start.html");
                response = fetchHTML("{FILE_CONTENTS}", "start.html");

            if (map.get("show").equals("users")) {
                String answerHTML = getHTML("users.html");
                StringBuilder users = new StringBuilder();

                try {
                    Statement s = knockFFA.getDB().getConnection().createStatement();

                    s.execute("SELECT u.uuid AS uuid, u.username AS username, s.score AS score FROM users AS u, stats AS s WHERE u.uuid=s.uuid ORDER BY username ASC;");
                    ResultSet rs = s.getResultSet();
                    while (rs.next()) {
                        users.append("<tr onclick=\"window.location='/?show=user&uuid={USER_UUID}'\"><td>"
                                        .replace("{USER_UUID}", rs.getString("uuid")))
                                .append("<img class=\"mx-auto d-block\" src=\"https://mc-heads.net/avatar/{USER_UUID}/50\">"
                                        .replace("{USER_UUID}", rs.getString("username")))
                                .append("</td><td>")
                                .append(rs.getString("username"))
                                .append("</td><td>")
                                .append(rs.getInt("score"))
                                .append("</td></tr>");
                    }
                } catch (SQLException e) {
                    answerHTML = error(e);
                }
                response = answerHTML.replace("{TABLEROWS_USERS}", users.toString());
            }

            // User Statistik
            if (map.get("show").equals("user")) {
                String answerHTML = getHTML("user.html");
                String uuid = map.get("uuid") != null ? map.get("uuid") : "NULL", username = map.get("name") != null ? map.get("name") : "NULL";
                int kills = 0, deaths = 0, score = 0, coins = 0;

                try {
                    Statement s = knockFFA.getDB().getConnection().createStatement();

                    if (username == null || username.equals("NULL")) {
                        username = "NULL";
                        s.execute("SELECT u.username, s.kills, s.deaths, s.coins, s.score FROM users AS u, stats AS s WHERE u.uuid='" + uuid + "' AND s.uuid='" + uuid + "';");
                    } else
                        s.execute("SELECT u.username, s.kills, s.deaths, s.coins, s.score FROM users AS u, stats AS s WHERE u.username='" + username + "' AND u.uuid=s.uuid;");
                    ResultSet rs = s.getResultSet();
                    if (rs.next()) {
                        username = rs.getString("username");
                        kills = rs.getInt("kills");
                        deaths = rs.getInt("deaths");
                        score = rs.getInt("score");
                        coins = rs.getInt("coins");
                    }
                } catch (SQLException e) {
                    answerHTML = error(e);
                }
                response = answerHTML
                        .replace("{USER_UUID}", uuid.equals("NULL") ? username : uuid)
                        .replace("{USER_USERNAME}", username)
                        .replace("{KILLS}", kills + "")
                        .replace("{DEATHS}", deaths + "")
                        .replace("{SCORE}", score + "")
                        .replace("{COINS}", coins + "");
            }

            if (map.get("show").equals("top10")) {
                String answerHTML = getHTML("top10.html");

                StringBuilder users = new StringBuilder();

                try {
                    Statement s = knockFFA.getDB().getConnection().createStatement();

                    s.execute("SELECT u.username AS username, u.uuid AS uuid, s.score AS score FROM stats AS s INNER JOIN users AS u WHERE u.uuid=s.uuid AND s.score > 0 ORDER BY score DESC LIMIT 10;");
                    ResultSet rs = s.getResultSet();
                    int i = 1;
                    while (rs.next()) {
                        String uuid = rs.getString("uuid");
                        users.append("<tr onclick=\"window.location='/?show=user&uuid={USER_UUID}'\"><td>"
                                        .replace("{USER_UUID}", uuid))
                                .append(i)
                                .append(".")
                                .append("</td><td>")
                                .append("<img src=\"https://mc-heads.net/avatar/{USER_UUID}/25\">".replace("{USER_UUID}", rs.getString("username")))
                                .append("</td><td>")
                                .append(rs.getString("username"))
                                .append("</td><td>").append(rs.getString("score")).append("</td></tr>");
                        i++;
                    }
                } catch (SQLException e) {
                    answerHTML = error(e);
                }
                response = answerHTML.replace("{TABLEROWS_USERS}", users.toString());
            }

        } else {
            response = getHTML("index.html");
        }

        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    @SuppressWarnings("resource")
    private String fetchHTML(String html, String fileName) throws IOException {
        FileReader fileReader;
        StringBuilder htmlBuilder = new StringBuilder();
        if (knockFFA != null)
            fileReader = new FileReader(knockFFA.getDataFolder() + "/webserver/" + fileName);
        else
            fileReader = new FileReader("src/webserver/" + fileName);
        int i;

        while ( (i = fileReader.read()) != -1 ) {
            htmlBuilder.append((char) i);
        }
        return html.replace("{FILE_CONTENTS}", htmlBuilder.toString());
    }
    @SuppressWarnings("resource")
    private String getHTML(String fileName) throws IOException {
        String fileNameMain = "main.html";
        StringBuilder htmlBuilder = new StringBuilder();
        FileReader fileReader;
        if (knockFFA != null)
            fileReader = new FileReader(knockFFA.getDataFolder() + "/webserver/" + fileNameMain);
        else
            fileReader = new FileReader("src/webserver/" + fileNameMain);
        int i;

        while ( (i = fileReader.read()) != -1 ) {
            htmlBuilder.append((char) i);
        }
        return fetchHTML(htmlBuilder.toString(), fileName);
    }

    String error(Exception e) {
        try {
            return getHTML("error.html").replace("{ERROR}", e.getLocalizedMessage());
        } catch (IOException ignored) {
        }
        return "ERROR<br>" + e.getLocalizedMessage();
    }

    String error(String message) {
        try {
            return getHTML("error.html").replace("{ERROR}", message).replace("{ERROR_MESSAGE}", "");
        } catch (IOException ignored) {
        }
        return "ERROR";
    }

}

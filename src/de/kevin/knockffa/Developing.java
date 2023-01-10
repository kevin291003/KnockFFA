package de.kevin.knockffa;

import de.kevin.knockffa.database.Database;
import de.kevin.knockffa.webserver.KnockFFAWebserver;
import de.kevin.websocket.ServerSocketThread;

import java.io.IOException;

public class Developing {

    private static Database db;

    public static Database getDB() {
        return db;
    }

    public static KnockFFAWebserver webserver;

    public static void main(String[] args) {
        db = new Database(null);
        db.connect("database.db");

        int port = 12345;

        try {
            webserver = new KnockFFAWebserver(null);
            webserver.start(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

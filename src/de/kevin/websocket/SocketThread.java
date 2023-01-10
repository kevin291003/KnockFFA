package de.kevin.websocket;

import de.kevin.knockffa.Developing;
import de.kevin.knockffa.KnockFFA;
import de.kevin.knockffa.Logging;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.Arrays;

public class SocketThread extends Thread {

    private final ServerSocket serverSocket;
    private final KnockFFA knockFFA;

    public SocketThread(KnockFFA knockFFA, ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
        this.knockFFA = knockFFA;
    }

    @Override
    public void run() {
        Socket socket = null;
        try {
            socket = serverSocket.accept();

            Logging.debug("Verbindung aufgebaut mit " + socket.getInetAddress());
            InputStream inputStream = socket.getInputStream();
            PrintWriter out = new PrintWriter(socket.getOutputStream());

            StringBuilder request = new StringBuilder();

            while (inputStream.available() != 0)
                request.append((char) inputStream.read());

            Logging.info(request.toString());
            String[] requestStrings = request.toString().split("\r");

            StringBuilder html;


            // Answering requests
            out.print("HTTP/1.1 200 OK\r\n");
            out.println("Content-Type: text/html\r\n");
            //out.println("Connection: close");
            out.print("\r\n");

            if (requestStrings[0].contains("/top10")) {
                String answerHTML = getHTML("top10.html");

                StringBuilder users = new StringBuilder();
                Statement s;
                if (knockFFA != null) {
                    s = knockFFA.getDB().getConnection().createStatement();
                }
                else {
                    s = Developing.getDB().getConnection().createStatement();
                }
                s.execute("SELECT u.username AS username, s.score AS score FROM scores AS s INNER JOIN users AS u WHERE u.uuid=s.uuid AND s.score > 0 ORDER BY score DESC LIMIT 10;");
                ResultSet rs = s.getResultSet();
                int i = 1;
                while (rs.next()) {
                    users
                            .append("<tr><td>").append(i)
                            .append("</td><td>").append(rs.getString("username"))
                            .append("</td><td>").append(rs.getString("score")).append("</td></tr>");
                    i++;
                }
                html = new StringBuilder(answerHTML.replace("{TABLEROWS_USERS}", users.toString()));
            }
            else if (requestStrings[0].contains("/users")) {
                String answerHTML = getHTML("users.html");

                StringBuilder users = new StringBuilder();
                Statement s;
                if (knockFFA != null) {
                    s = knockFFA.getDB().getConnection().createStatement();
                }
                else { // A-tmoaf!
                    s = Developing.getDB().getConnection().createStatement();
                }
                s.execute("SELECT * FROM users;");
                ResultSet rs = s.getResultSet();
                while (rs.next()) {
                    users
                            .append("<tr><td>").append(rs.getInt("id"))
                            .append("</td><td>").append(rs.getString("uuid"))
                            .append("</td><td>").append(rs.getString("username")).append("</td></tr>");
                }
                html = new StringBuilder(answerHTML.replace("{TABLEROWS_USERS}", users.toString()));
            }
            else {
                html = new StringBuilder(getHTML("index.html"));
            }
            Logging.debug(html.toString());
            out.print(html);

            // Flush answer
            out.flush();

            socket.close();

            ServerSocketThread.waiting = false;

        } catch (IOException | SQLException ignored) {
        }

    }

    @SuppressWarnings("resource")
    private String fetchHTML(String html, String fileName) throws IOException {
        FileReader fileReader;
        StringBuilder htmlBuilder = new StringBuilder();
        if (knockFFA != null)
            fileReader = new FileReader(knockFFA.getDataFolder() + "/webserver/" + fileName);
        else
            fileReader = new FileReader("src/webserver/" + fileName);
        int i = 0;

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
        int i = 0;

        while ( (i = fileReader.read()) != -1 ) {
            htmlBuilder.append((char) i);
        }
        return fetchHTML(htmlBuilder.toString(), fileName);
    }
}

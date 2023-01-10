package de.kevin.websocket;

import de.kevin.knockffa.KnockFFA;
import de.kevin.knockffa.Logging;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketThread extends Thread {

    private final int port;
    private KnockFFA knockFFA;
    public ServerSocket serverSocket;
    public static boolean waiting;

    public ServerSocketThread(KnockFFA knockFFA, int port) {
        this.port = port;
        this.knockFFA = knockFFA;
    }

    @Override
    public void run() {
        serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            if(knockFFA != null)
                Logging.info("Webserver ist erreichbar unter der Adresse: http://kevend.de:" + serverSocket.getLocalPort());
            else
                Logging.info("Webserver ist erreichbar unter der Adresse: http://localhost:" + serverSocket.getLocalPort());
            while (serverSocket.isBound() && !serverSocket.isClosed()) {
                if (!waiting) {
                    Thread socketThread = new SocketThread(knockFFA, serverSocket);
                    socketThread.start();
                    waiting = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

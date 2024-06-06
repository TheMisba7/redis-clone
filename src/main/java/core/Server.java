package core;

import core.ConnectionHandler;
import core.ExpiryCollector;
import dao.IDao;
import dao.IDaoImpl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
    private final String host;
    private final int port;
    private final List<Socket> connections = new ArrayList<>();
    private final IDao dao = new IDaoImpl();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public Server(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        try {
            serverSocket = new ServerSocket();
            SocketAddress socketAddress = new InetSocketAddress(host, port);
            serverSocket.bind(socketAddress);
            serverSocket.setReuseAddress(true);

            //scheduler.scheduleAtFixedRate(new ExpiryCollector(dao), 1000, 1000, TimeUnit.MILLISECONDS);
            // Wait for connection from client.
            while (true) {
                clientSocket = serverSocket.accept();
                Thread.startVirtualThread(new ConnectionHandler(clientSocket, dao));
            }
        } catch (IOException e) {
            System.out.println(STR."IOException: \{e.getMessage()}");
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println(STR."IOException: \{e.getMessage()}");
            }
        }
    }
}

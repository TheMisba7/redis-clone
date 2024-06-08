package core;

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

public abstract class Server {
    protected final String host;
    protected int port = 6379;
    private final List<Socket> connections = new ArrayList<>();
    protected final IDao dao = new IDaoImpl();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    protected int replOffset = 0;

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

            scheduler.scheduleAtFixedRate(new ExpiryCollector(dao), 10, 10, TimeUnit.SECONDS);
            // Wait for connection from client.
            while (true) {
                clientSocket = serverSocket.accept();
                Thread.startVirtualThread(new ConnectionHandler(clientSocket, dao, this));
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

    public int getReplOffset() {
        return replOffset;
    }

    public static class ServerArgs {
        public static final String PORT_ARG = "--port";
        public static final String REPLICA_OF = "--replicaof";
        public static final int DEFAULT_PORT = 6379;
    }
}

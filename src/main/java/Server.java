import dao.IDao;
import dao.IDaoImpl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private final String host;
    private final int port;
    private final List<Socket> connections = new ArrayList<>();
    private final IDao dao = new IDaoImpl();

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

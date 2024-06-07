package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Slave extends Server {
    private int masterPort;
    private String masterHost;

    public Slave(String host, int port, String masterHost, int masterPort) {
        super(host, port);
        this.masterHost = masterHost;
        this.masterPort = masterPort;
        pingMaster();
    }

    public void pingMaster() {
        Socket socket = null;
        try {
            socket = new Socket(masterHost, masterPort);
            socket.getOutputStream().write(Parser.encodeArray(new String[]{"ping"}).getBytes());
            socket.getOutputStream().flush();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("reading line.rrygf..");
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                if (line.equalsIgnoreCase("+PONG"))
                    break;
            }
            System.out.println("ping got ping");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

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
        startHandshake();
    }

    private void startHandshake() {
        Socket socket = null;
        try {
            socket = new Socket(masterHost, masterPort);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            CommandSender.ping(socket.getOutputStream());
            if (CommandSender.parseResponse(in, "+pong")) {
                System.out.println("slave got pong from master");
                CommandSender.replConfig(socket.getOutputStream(), "listening-port", String.valueOf(this.port));
                if (CommandSender.parseResponse(in, "+OK")) {
                    CommandSender.replConfig(socket.getOutputStream(), "capa", "psync2");
                    if (CommandSender.parseResponse(in, "+OK")) {
                        CommandSender.pSync(socket.getOutputStream());
                        if (CommandSender.parseResponse(in, "+FULLRESYNC")) {
                            System.out.println("slave and master shaked hands");
                        }
                    }


                } else {
                    System.out.println("repl config failed: listening-port");
                }





            } else {
                System.out.println("ping master failed");
            }







        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

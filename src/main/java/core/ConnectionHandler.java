package core;

import dao.IDao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
public class ConnectionHandler implements Runnable {
    private Socket socket;
    private final BufferedReader bufferedReader;
    private final OutputStream bufferedWriter;
    private final IDao dao;
    private final Server server;
    private final IServerReader serverReader;


    public ConnectionHandler(Socket socket, IDao dao, Server server) {
        this.socket = socket;
        this.dao = dao;
        this.server = server;
        try {
                this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = socket.getOutputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.serverReader = new CommandReader(this.bufferedReader, this.bufferedWriter, this.dao, this.server);
    }

    @Override
    public void run() {
        this.serverReader.parseCommand();
    }
}

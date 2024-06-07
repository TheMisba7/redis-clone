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
    }

    @Override
    public void run() {
        String in = null;
        try {
            while ((in = bufferedReader.readLine()) != null) {
                if ("ping".equalsIgnoreCase(in)) {
                    encodeAndWrite("+PONG");
                } else if (in.startsWith("*")) {
                    char size = in.toCharArray()[1];
                    String[] input = readArray(Integer.parseInt(String.valueOf(size)));
                    String command = input[0];
                    if ("ping".equalsIgnoreCase(command)) {
                        encodeAndWrite("+PONG");
                    } else if ("echo".equalsIgnoreCase(command)) {
                        encodeAndWrite(input[1]);
                    } else if ("SET".equalsIgnoreCase(command)) {
                        handleSetCommand(input);
                    } else if ("GET".equalsIgnoreCase(command)) {
                      handleGetCommand(input);
                    } else if ("INFO".equalsIgnoreCase(command)) {
                        switch (this.server) {
                            case Slave slave  -> {
                                encodeAndWrite("role:slave");
                            }
                            case Master master -> {
                                encodeAndWrite(STR."role:master\nmaster_replid:\{master.getReplId()}\nmaster_repl_offset:\{master.getReplOffset()}\n");
                            }
                            default -> throw new IllegalStateException(STR."Unexpected value: \{this.server}");
                        }
                    } else if ("REPLCONF".equalsIgnoreCase(command)) {
                        if (input.length == 3) {
                            if ("listening-port".equalsIgnoreCase(input[1])) {
                                int port = Integer.parseInt(input[2]);
                                if (this.server instanceof Master) {
                                    Master master = (Master) this.server;
                                    master.getSlavePorts().add(port);
                                    write(Parser.OK);
                                }
                            }else if ("capa".equalsIgnoreCase(input[1])) {
                                write(Parser.OK);
                            }
                        } else {
                            encodeAndWrite("invalid args");
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String[] readArray(int size) throws IOException {
        String in = null;
        String [] strings = new String[size];
        int index = 0;
      while (size > 0 && ((in = bufferedReader.readLine()) != null)) {
          if (in.startsWith("$"))
              continue;
          size--;
          strings[index] = in;
          index++;
      }
      return strings;
    }

    private void encodeAndWrite(String str) throws IOException {
        write(Parser.encodeBulkStr(str));
    }

    private void write(String str) throws IOException {
        bufferedWriter.write(str.getBytes());
        bufferedWriter.flush();
    }

    private void handleSetCommand(String[] input) throws IOException {
        if (input.length == 3) {
            dao.add(input[1], new ValueContainer(input[2]));
            write(Parser.OK);
        } else if (input.length == 5) {
            if ("px".equalsIgnoreCase(input[3])) {
                try {
                    long expiry = System.currentTimeMillis() + Long.parseLong(input[4]);
                    dao.add(input[1], new ValueContainer(input[2], expiry));
                    write(Parser.OK);
                } catch (NumberFormatException e) {
                    encodeAndWrite("expiry not parsable");
                }
            } else {
                encodeAndWrite("invalid option");
            }
        } else {
            encodeAndWrite("invalid arguments");
        }
    }

    private void handleGetCommand(String[] input) throws IOException {
        if (input.length != 2) {
            encodeAndWrite("invalid arguments");
        } else  {
            ValueContainer res = dao.get(input[1]);
            if (res != null) {
                if (res.isExpired()) {
                    dao.delete(input[1]);
                    write(Parser.NULL_BULK);
                } else {
                    encodeAndWrite(res.getValue());
                }
            } else {
                write(Parser.NULL_BULK);
            }
        }
    }
}

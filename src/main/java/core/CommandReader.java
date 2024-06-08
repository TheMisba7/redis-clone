package core;

import dao.IDao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

public final class CommandReader implements IServerReader{
    private final BufferedReader bufferedReader;
    private final OutputStream bufferedWriter;
    private final IDao dao;
    private final Server server;

    public CommandReader(BufferedReader bufferedReader, OutputStream out, IDao dao, Server server) {
        this.bufferedReader = bufferedReader;
        this.bufferedWriter = out;
        this.dao = dao;
        this.server = server;
    }

    @Override
    public void parseCommand() {
        System.out.println("listening for commands...");
        String in = null;
        try {
            while ((in = bufferedReader.readLine()) != null) {
                if ("ping".equalsIgnoreCase(in)) {
                    encodeAndWrite("+PONG");
                } else if (in.startsWith("*")) {
                    char size = in.toCharArray()[1];
                    String[] input = readArray(Integer.parseInt(String.valueOf(size)));
                    if (input == null || input[0].isBlank())
                        continue;
                    Command cmd = Command.getCommand(input[0].toUpperCase());
                    switch (cmd) {
                        case PING -> encodeAndWrite("+PONG");
                        case GET -> handleGetCommand(input);
                        case SET -> handleSetCommand(input);
                        case INFO -> {
                            switch (this.server) {
                                case Slave slave  -> {
                                    encodeAndWrite("role:slave");
                                }
                                case Master master -> {
                                    encodeAndWrite(STR."role:master\nmaster_replid:\{master.getReplId()}\nmaster_repl_offset:\{master.getReplOffset()}\n");
                                }
                                default -> throw new IllegalStateException(STR."Unexpected value: \{this.server}");
                            }
                        }
                        case REPLCONF -> {
                            if (input.length == 3) {
                                if ("listening-port".equalsIgnoreCase(input[1])) {
                                    int port = Integer.parseInt(input[2]);
                                    if (this.server instanceof Master master) {
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
                        case PSYNC -> {
                            if (input.length != 3 || !input[1].equals("?") && !input[2].equals("-1")) {
                                encodeAndWrite("unknown options");
                            } else {
                                if (this.server instanceof Master master) {
                                    System.out.println("syncing slave...");
                                    String res = STR."FULLRESYNC \{master.getReplId()} \{master.getReplOffset()}";
                                    write(Parser.encodeSimpleStr(res));
                                } else {
                                    write(Parser.NULL_BULK);
                                }
                            }
                        }
                        case EMPTY -> {
                            write(Parser.OK);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String parseResponse() {
        return null;
    }

    @Override
    public boolean parseResponse(String expected) throws IOException {
        String result = null;
        while ((result = bufferedReader.readLine()) != null) {
            if(expected != null && result.equalsIgnoreCase(expected))
                return true;
        }
        return false;
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

import dao.IDao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ConnectionHandler implements Runnable{
    private Socket socket;
    private final BufferedReader bufferedReader;
    private final OutputStream bufferedWriter;
    private final IDao dao;

    public ConnectionHandler(Socket socket, IDao dao) {
        this.socket = socket;
        this.dao = dao;
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
                    String[] strings = readArray(Integer.parseInt(String.valueOf(size)));
                    String command = strings[0];
                    if ("ping".equalsIgnoreCase(command)) {
                        encodeAndWrite("+PONG");
                    } else if ("echo".equalsIgnoreCase(command)) {
                        encodeAndWrite(strings[1]);
                    } else if ("SET".equalsIgnoreCase(command)) {
                        if (strings.length != 3) {
                            encodeAndWrite("invalid arguments");
                        } else {
                            dao.add(strings[1], strings[2]);
                            write(Parser.OK);
                        }
                    } else if ("GET".equalsIgnoreCase(command)) {
                        if (strings.length != 2) {
                            encodeAndWrite("invalid arguments");
                        } else  {
                            String res = dao.get(strings[1]);
                            if (res != null) {
                                encodeAndWrite(res);
                            } else {
                                write(Parser.NULL_BULK);
                            }
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
}

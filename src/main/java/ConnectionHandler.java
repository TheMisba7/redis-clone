import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ConnectionHandler implements Runnable{
    private Socket socket;
    private final BufferedReader bufferedReader;
    private final OutputStream bufferedWriter;

    public ConnectionHandler(Socket socket) {
        this.socket = socket;
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
                    bufferedWriter.write(Parser.encodeBulkStr("+PONG").getBytes());
                    bufferedWriter.flush();
                } else if (in.startsWith("*")) {
                    char size = in.toCharArray()[1];
                    String[] strings = readArray(Integer.parseInt(String.valueOf(size)));
                    if ("ping".equalsIgnoreCase(strings[0])) {
                        bufferedWriter.write(Parser.encodeBulkStr("+PONG").getBytes());
                        bufferedWriter.flush();
                    } else if ("echo".equalsIgnoreCase(strings[0])) {
                        bufferedWriter.write(Parser.encodeBulkStr(strings[1]).getBytes());
                        bufferedWriter.flush();
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
}

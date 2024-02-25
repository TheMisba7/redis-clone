import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class ConnectionHandler implements Runnable{
    private Socket socket;
    private BufferedReader bufferedReader;
    private OutputStream bufferedWriter;

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
                System.out.println(in);
                if ("ping".equalsIgnoreCase(in)) {
                    bufferedWriter.write("+PONG".getBytes());
                    bufferedWriter.write("\r\n".getBytes());
                    bufferedWriter.flush();
                } else if (in.startsWith("echo")) {
                    String[] echoes = in.split("echo ");
                    bufferedWriter.write(echoes[1].getBytes());
                    bufferedWriter.write("\r\n".getBytes());
                    bufferedWriter.flush();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

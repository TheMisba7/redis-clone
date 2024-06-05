import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
  public static void main(String[] args){
    System.out.println("Logs from your program will appear here!");
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        int port = 6379;
        try {
          serverSocket = new ServerSocket(port);
          serverSocket.setReuseAddress(true);
          // Wait for connection from client.
            while (true) {
                clientSocket = serverSocket.accept();
                Thread.startVirtualThread(new ConnectionHandler(clientSocket));
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



public class Main {
  public static void main(String[] args){
    Server server = new Server("127.0.0.1", 6379);
    server.start();
  }
}

import core.Master;
import core.Server;
import core.Slave;

public class Main {
  public static void main(String[] args){

    Server server = null;
    boolean isMaster = true;
    int port = Server.ServerArgs.DEFAULT_PORT;
    int masterPort = Server.ServerArgs.DEFAULT_PORT;
    String masterHost = "";
    if (args != null && args.length > 0) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case Server.ServerArgs.PORT_ARG -> {
                    port = Integer.parseInt(args[i + 1]);
                    i++;
                }
                case Server.ServerArgs.REPLICA_OF -> {
                    isMaster = false;
                    String[] masterConfig = args[i + 1].split(" ");
                    masterHost = masterConfig[0];
                    masterPort = Integer.parseInt(masterConfig[1]);
                    i++;
                }
            }
        }
    }
    if (isMaster) {
      server = new Master("127.0.0.1", port);
    } else {
      server = new Slave("127.0.0.1", port, masterHost, masterPort);
    }
    server.start();
  }



  private static boolean isMaster(String[] args) {
      for (String arg : args) {
          if (Server.ServerArgs.REPLICA_OF.equalsIgnoreCase(arg)) {
              return false;
          }
      }
    return true;
  }

}

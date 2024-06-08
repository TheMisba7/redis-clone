package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;

public class CommandSender {
    public static void ping(OutputStream out) throws IOException {
        sendArray(out, new String[]{"ping"});
    }

    public static void replConfig(OutputStream out, String... options) throws IOException {
        String[] config = new String[options.length + 1];
        config[0] = Command.REPLCONF.name();
        for (int i = 1; i < config.length; i++) {
            config[i] = options[i - 1];
        }
        sendArray(out, config);
    }

    public static void pSync(OutputStream out) throws IOException {
        sendArray(out, new String[]{Command.PSYNC.name(), "?", "-1"});
    }

    private static void sendArray(OutputStream out, String [] config) throws IOException {
        out.write(Parser.encodeArray(config).getBytes());
        out.flush();;
    }

    public static boolean parseResponse(BufferedReader in, String expected) throws IOException {
        String result = null;
        while ((result = in.readLine()) != null) {
            if(expected != null && (result.equalsIgnoreCase(expected) || result.contains(expected)))
                return true;
        }
        return false;
    }
}

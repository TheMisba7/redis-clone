package core;

import java.util.UUID;

public class Master extends Server {
    private String replId;

    public Master(String host, int port) {
        super(host, port);
        this.replId = UUID.randomUUID().toString();
    }

    public String getReplId() {
        return replId;
    }
}

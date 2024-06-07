package core;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Master extends Server {
    private String replId;
    private Set<Integer> slavePorts;

    public Master(String host, int port) {
        super(host, port);
        this.replId = UUID.randomUUID().toString();
        this.slavePorts = new HashSet<>();
    }

    public String getReplId() {
        return replId;
    }

    public Set<Integer> getSlavePorts() {
        return slavePorts;
    }
}

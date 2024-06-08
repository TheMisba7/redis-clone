package core;

import java.io.IOException;

public interface IServerReader {
    void parseCommand();
    String parseResponse();
    boolean parseResponse(String expected) throws IOException;
}

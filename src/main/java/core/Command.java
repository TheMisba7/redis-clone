package core;

public enum Command {
    PING, ECHO, SET, GET, INFO, REPLCONF, PSYNC, EMPTY;

    public static Command getCommand(String cmd) {
        try {
            return Command.valueOf(cmd);
        } catch (IllegalArgumentException e) {
            return EMPTY;
        }
    }
}

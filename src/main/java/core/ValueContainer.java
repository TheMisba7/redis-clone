package core;
public class ValueContainer {
    private final String value;
    private final long expiresAt;

    public ValueContainer(String value, long expiresAt) {
        this.value = value;
        this.expiresAt = expiresAt;
    }

    public ValueContainer(String value) {
        this.value = value;
        this.expiresAt = Long.MAX_VALUE;
    }

    public boolean isExpired() {
        long currentTime = System.currentTimeMillis();
        return currentTime > expiresAt;
    }
    public String getValue() {
        return value;
    }

    public long getExpiresAt() {
        return expiresAt;
    }
}

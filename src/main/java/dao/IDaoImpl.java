package dao;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IDaoImpl implements IDao {
    private static final Map<String, String> DATA = new ConcurrentHashMap<>();
    @Override
    public void add(String key, String value) {
        DATA.put(key, value);
    }

    @Override
    public String get(String key) {
        return DATA.get(key);
    }

    @Override
    public void delete(String key) {
        DATA.remove(key);
    }
}

package dao;

import com.sun.tools.javac.Main;
import core.ValueContainer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IDaoImpl implements IDao {
    private static final Map<String, ValueContainer> DATA = new ConcurrentHashMap<>();
    @Override
    public void add(String key, ValueContainer value) {
        DATA.put(key, value);
    }

    @Override
    public ValueContainer get(String key) {
        return DATA.get(key);
    }

    @Override
    public void delete(String key) {
        DATA.remove(key);
    }

    @Override
    public void collect() {
        DATA.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
}

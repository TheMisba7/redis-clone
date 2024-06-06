package dao;

import core.ValueContainer;

public interface IDao {
    void add(String key, ValueContainer value);
    ValueContainer get(String key);
    void delete(String key);
    void collect();
}

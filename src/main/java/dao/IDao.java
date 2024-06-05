package dao;

public interface IDao {
    void add(String key, String value);
    String get(String key);
    void delete(String key);
}

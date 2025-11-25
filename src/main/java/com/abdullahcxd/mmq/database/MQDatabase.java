package com.abdullahcxd.mmq.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.json.JSONObject;

import java.sql.*;
import java.util.*;
import java.util.function.Function;

/**
 * A key-value database implementation using HikariCP for connection pooling.
 * Supports MySQL and SQLite databases with generic type inference.
 *
 * @param <K> The type of keys
 * @param <V> The type of values
 */
public class MQDatabase<K, V> implements AutoCloseable {

    private final HikariDataSource dataSource;
    private final String tableName;
    private final Function<K, String> keySerializer;
    private final Function<String, K> keyDeserializer;
    private final Function<V, String> valueSerializer;
    private final Function<String, V> valueDeserializer;

    private MQDatabase(HikariDataSource dataSource, String tableName,
                       Function<K, String> keySerializer, Function<String, K> keyDeserializer,
                       Function<V, String> valueSerializer, Function<String, V> valueDeserializer) {
        this.dataSource = dataSource;
        this.tableName = tableName;
        this.keySerializer = keySerializer;
        this.keyDeserializer = keyDeserializer;
        this.valueSerializer = valueSerializer;
        this.valueDeserializer = valueDeserializer;
        initializeTable();
    }

    /**
     * Creates a new MQDatabase instance.
     *
     * @param info Database connection information
     * @param tableName Name of the table to use for key-value storage
     * @param keySerializer Function to serialize keys to strings
     * @param keyDeserializer Function to deserialize strings to keys
     * @param valueSerializer Function to serialize values to strings
     * @param valueDeserializer Function to deserialize strings to values
     * @param <K> Key type
     * @param <V> Value type
     * @return A new MQDatabase instance
     */
    public static <K, V> MQDatabase<K, V> create(
            MQDBInformation info,
            String tableName,
            Function<K, String> keySerializer,
            Function<String, K> keyDeserializer,
            Function<V, String> valueSerializer,
            Function<String, V> valueDeserializer) {

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(info.convert());

        if (info.getUsername() != null) {
            config.setUsername(info.getUsername());
        }
        if (info.getPassword() != null) {
            config.setPassword(info.getPassword());
        }

        // Connection pool settings
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        HikariDataSource dataSource = new HikariDataSource(config);

        return new MQDatabase<>(dataSource, tableName, keySerializer, keyDeserializer,
                valueSerializer, valueDeserializer);
    }

    /**
     * Convenience method for String key-value databases.
     */
    public static MQDatabase<String, String> create(MQDBInformation info, String tableName) {
        return create(info, tableName, k -> k, k -> k, v -> v, v -> v);
    }

    /**
     * Creates a database with org.json JSONObject serialization.
     * Values are stored and retrieved as JSONObject.
     *
     * @param info Database connection information
     * @param tableName Name of the table
     * @return A new MQDatabase instance with JSONObject values
     */
    public static MQDatabase<String, JSONObject> createWithJsonObject(
            MQDBInformation info,
            String tableName) {
        return create(
                info,
                tableName,
                k -> k,
                k -> k,
                JSONObject::toString,
                JSONObject::new
        );
    }

    /**
     * Creates a database with org.json Map serialization.
     * Values are converted to/from Map using JSONObject.
     *
     * @param info Database connection information
     * @param tableName Name of the table
     * @return A new MQDatabase instance with Map values
     */
    public static MQDatabase<String, Map<String, Object>> createWithJsonMap(
            MQDBInformation info,
            String tableName) {
        return create(
                info,
                tableName,
                k -> k,
                k -> k,
                map -> new JSONObject(map).toString(),
                json -> new JSONObject(json).toMap()
        );
    }

    private void initializeTable() {
        String createTableSQL = String.format(
                "CREATE TABLE IF NOT EXISTS %s (" +
                        "key_field VARCHAR(255) PRIMARY KEY, " +
                        "value_field TEXT NOT NULL" +
                        ")", tableName
        );

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize table", e);
        }
    }

    /**
     * Stores a key-value pair in the database.
     * If the key already exists, the value is updated.
     *
     * @param key The key
     * @param value The value
     */
    public void put(K key, V value) {
        String sql = String.format(
                "INSERT OR REPLACE INTO %s (key_field, value_field) VALUES (?, ?)", tableName
        );

        // For MySQL, use ON DUPLICATE KEY UPDATE instead
        if (dataSource.getJdbcUrl().contains("mysql")) {
            sql = String.format(
                    "INSERT INTO %s (key_field, value_field) VALUES (?, ?) " +
                            "ON DUPLICATE KEY UPDATE value_field = VALUES(value_field)", tableName
            );
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, keySerializer.apply(key));
            stmt.setString(2, valueSerializer.apply(value));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to put key-value pair", e);
        }
    }

    /**
     * Stores multiple key-value pairs in a batch operation.
     * More efficient than calling put() multiple times.
     *
     * @param entries Map of key-value pairs to store
     */
    public void putAll(Map<K, V> entries) {
        if (entries.isEmpty()) {
            return;
        }

        String sql = String.format(
                "INSERT OR REPLACE INTO %s (key_field, value_field) VALUES (?, ?)", tableName
        );

        if (dataSource.getJdbcUrl().contains("mysql")) {
            sql = String.format(
                    "INSERT INTO %s (key_field, value_field) VALUES (?, ?) " +
                            "ON DUPLICATE KEY UPDATE value_field = VALUES(value_field)", tableName
            );
        }

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Map.Entry<K, V> entry : entries.entrySet()) {
                stmt.setString(1, keySerializer.apply(entry.getKey()));
                stmt.setString(2, valueSerializer.apply(entry.getValue()));
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to put multiple key-value pairs", e);
        }
    }

    /**
     * Retrieves a value by key.
     *
     * @param key The key
     * @return Optional containing the value if found, empty otherwise
     */
    public Optional<V> get(K key) {
        String sql = String.format(
                "SELECT value_field FROM %s WHERE key_field = ?", tableName
        );

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, keySerializer.apply(key));
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String value = rs.getString("value_field");
                return Optional.of(valueDeserializer.apply(value));
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get value", e);
        }
    }

    /**
     * Retrieves a value by key, or returns a default value if not found.
     *
     * @param key The key
     * @param defaultValue The default value to return if key not found
     * @return The value or default value
     */
    public V getOrDefault(K key, V defaultValue) {
        return get(key).orElse(defaultValue);
    }

    /**
     * Checks if a key exists in the database.
     *
     * @param key The key to check
     * @return true if the key exists, false otherwise
     */
    public boolean containsKey(K key) {
        String sql = String.format(
                "SELECT 1 FROM %s WHERE key_field = ? LIMIT 1", tableName
        );

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, keySerializer.apply(key));
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check key existence", e);
        }
    }

    /**
     * Removes a key-value pair from the database.
     *
     * @param key The key to remove
     * @return true if the key was removed, false if it didn't exist
     */
    public boolean remove(K key) {
        String sql = String.format(
                "DELETE FROM %s WHERE key_field = ?", tableName
        );

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, keySerializer.apply(key));
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove key", e);
        }
    }

    /**
     * Returns all keys in the database.
     *
     * @return Set of all keys
     */
    public Set<K> keySet() {
        String sql = String.format("SELECT key_field FROM %s", tableName);
        Set<K> keys = new HashSet<>();

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                keys.add(keyDeserializer.apply(rs.getString("key_field")));
            }
            return keys;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get keys", e);
        }
    }

    /**
     * Returns all values in the database.
     *
     * @return Collection of all values
     */
    public Collection<V> values() {
        String sql = String.format("SELECT value_field FROM %s", tableName);
        List<V> values = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                values.add(valueDeserializer.apply(rs.getString("value_field")));
            }
            return values;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get values", e);
        }
    }

    /**
     * Returns all key-value pairs as a Map.
     *
     * @return Map of all key-value pairs
     */
    public Map<K, V> asMap() {
        String sql = String.format("SELECT key_field, value_field FROM %s", tableName);
        Map<K, V> map = new HashMap<>();

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                K key = keyDeserializer.apply(rs.getString("key_field"));
                V value = valueDeserializer.apply(rs.getString("value_field"));
                map.put(key, value);
            }
            return map;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all entries", e);
        }
    }

    /**
     * Clears all entries from the database.
     */
    public void clear() {
        String sql = String.format("DELETE FROM %s", tableName);

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to clear database", e);
        }
    }

    /**
     * Returns the number of key-value pairs in the database.
     *
     * @return The size
     */
    public long size() {
        String sql = String.format("SELECT COUNT(*) FROM %s", tableName);

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get size", e);
        }
    }

    /**
     * Checks if the database is empty.
     *
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    /**
     * Database connection information container.
     */
    public static class MQDBInformation {

        public enum DatabaseType {
            MYSQL, SQLITE
        }

        @Getter
        private final DatabaseType type;
        private String host;
        private int port;
        private String database;
        @Getter
        private String username;
        @Getter
        private String password;
        private String file;
        private final Map<String, String> properties;

        private MQDBInformation(DatabaseType type) {
            this.type = type;
            this.properties = new HashMap<>();
        }

        /**
         * Creates MySQL database information.
         */
        public static MQDBInformation mysql(String host, int port, String database,
                                            String username, String password) {
            MQDBInformation info = new MQDBInformation(DatabaseType.MYSQL);
            info.host = host;
            info.port = port;
            info.database = database;
            info.username = username;
            info.password = password;
            return info;
        }

        /**
         * Creates MySQL database information with default port (3306).
         */
        public static MQDBInformation mysql(String host, String database,
                                            String username, String password) {
            return mysql(host, 3306, database, username, password);
        }

        /**
         * Creates SQLite database information.
         */
        public static MQDBInformation sqlite(String file) {
            MQDBInformation info = new MQDBInformation(DatabaseType.SQLITE);
            info.file = file;
            return info;
        }

        /**
         * Adds a custom JDBC property.
         */
        public MQDBInformation property(String key, String value) {
            this.properties.put(key, value);
            return this;
        }

        /**
         * Converts the information to a JDBC URL.
         *
         * @return JDBC URL string
         */
        public String convert() {
            StringBuilder url = new StringBuilder();

            switch (type) {
                case MYSQL:
                    url.append("jdbc:mysql://")
                            .append(host)
                            .append(":")
                            .append(port)
                            .append("/")
                            .append(database);
                    break;

                case SQLITE:
                    url.append("jdbc:sqlite:")
                            .append(file);
                    break;
            }

            if (!properties.isEmpty()) {
                url.append("?");
                properties.forEach((key, value) ->
                        url.append(key).append("=").append(value).append("&")
                );
                url.setLength(url.length() - 1); // Remove trailing &
            }

            return url.toString();
        }

    }
}
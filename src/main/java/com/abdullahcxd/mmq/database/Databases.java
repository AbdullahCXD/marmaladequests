package com.abdullahcxd.mmq.database;

import com.abdullahcxd.mmq.MarmaladeQuests;
import com.abdullahcxd.mmq.utils.LoggingUtility;
import lombok.Getter;
import org.json.JSONObject;

import java.io.File;

public class Databases {

    public static MQDatabase<String, JSONObject> playerTable;
    public static MQDatabase<String, JSONObject> questTable;
    public static MQDatabase<String, JSONObject> economyTable;
    @Getter
    private static boolean loaded = false;

    public static void initialize(MarmaladeQuests plugin) {
        if (isLoaded()) {
            LoggingUtility.plugin("&eThe database has already been loaded and enabled, restart your plugin if you have applied changes.");
            return;
        }

        plugin.reloadConfig();

        String type = plugin.getConfig().getString("database.type", "sqlite");

        if (type.equalsIgnoreCase("sqlite")) {
            loadSqlite(plugin);
        } else if (type.equalsIgnoreCase("mysql")) {
            loadMySQL(plugin);
        } else {
            LoggingUtility.plugin("&eInvalid type, please choose between: sqlite or mysql in the config. Defaulting to SQLite");
            loadSqlite(plugin); // default to sqlite if the type is invalid.
        }

        loaded = true;
    }

    public static void loadSqlite(MarmaladeQuests plugin) {
        String filePath = plugin.getConfig().getString("database.filePath", "database.db");
        File file = new File(plugin.getDatabaseDirectory(), filePath);

        playerTable = MQDatabase.createWithJsonObject(MQDatabase.MQDBInformation.sqlite(file.getPath()), "playerTable");
        questTable = MQDatabase.createWithJsonObject(MQDatabase.MQDBInformation.sqlite(file.getPath()), "questTable");
        economyTable = MQDatabase.createWithJsonObject(MQDatabase.MQDBInformation.sqlite(file.getPath()), "economyTable");
    }

    public static void loadMySQL(MarmaladeQuests plugin) {
        String host = plugin.getConfig().getString("database.host", "localhost");
        int port = plugin.getConfig().getInt("database.port", 3306);
        String user = plugin.getConfig().getString("database.username", "root");
        String password = plugin.getConfig().getString("database.password", "root");
        String database = plugin.getConfig().getString("database.database", "mqdb");

        playerTable = MQDatabase.createWithJsonObject(MQDatabase.MQDBInformation.mysql(host, port, database, user, password), "playerTable");
        questTable = MQDatabase.createWithJsonObject(MQDatabase.MQDBInformation.mysql(host, port, database, user, password), "questTable");
        economyTable = MQDatabase.createWithJsonObject(MQDatabase.MQDBInformation.mysql(host, port, database, user, password), "economyTable");

    }

    public static void closeAll() {
        if (!isLoaded()) return;

        playerTable.close();
        questTable.close();
        economyTable.close();
    }

    public static void clearAll() {
        if (!isLoaded()) return;

        playerTable.clear();
        questTable.clear();
        economyTable.clear();
    }

}

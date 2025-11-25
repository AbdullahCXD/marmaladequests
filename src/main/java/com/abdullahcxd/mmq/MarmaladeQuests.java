package com.abdullahcxd.mmq;

import com.abdullahcxd.mmq.api.MarmaladeAPI;
import com.abdullahcxd.mmq.commands.CommandRegistry;
import com.abdullahcxd.mmq.commands.impl.MarmaladeCommand;
import com.abdullahcxd.mmq.database.Databases;
import com.abdullahcxd.mmq.economy.MQEconomyManager;
import com.abdullahcxd.mmq.listeners.ListenerRegistry;
import com.abdullahcxd.mmq.listeners.MQListener;
import com.abdullahcxd.mmq.listeners.PlayersListener;
import com.abdullahcxd.mmq.player.MQPlayerManager;
import com.abdullahcxd.mmq.quests.MQQuestManager;
import com.abdullahcxd.mmq.threading.ThreadingAPI;
import com.abdullahcxd.mmq.utils.LoggingUtility;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
public final class MarmaladeQuests extends JavaPlugin {

    // For direct use, you can use this instance.
    @Getter
    private static MarmaladeQuests instance;

    private final File databaseDirectory = new File(getDataFolder(), "databases");

    private MQQuestManager questManager;
    private MQPlayerManager playerManager;
    private MQEconomyManager economyManager;

    public MarmaladeQuests() {
        instance = this;
        MarmaladeAPI.setPlugin(this);
    }

    @Override
    public void onLoad() {

        saveDefaultConfig();

        ThreadingAPI.thread("BootstrapThread", () -> {
            if (!databaseDirectory.exists()) { databaseDirectory.mkdirs(); }
        });

    }

    @Override
    public void onEnable() {

        Databases.initialize(this);

        // init
        questManager = new MQQuestManager(this);
        playerManager = new MQPlayerManager(this);
        economyManager = new MQEconomyManager(this);

        registerCommands();
        registerListeners();

        LoggingUtility.plugin(LoggingUtility.separator(32));
        LoggingUtility.plugin("&6");
        LoggingUtility.figlet("MMQuests", '6');
        LoggingUtility.plugin("&aEnabled &6Marmalade&6&lQuests &6v" + this.getDescription().getVersion() + " (Built by AbdullahCXD)");
        LoggingUtility.plugin("&6");
        LoggingUtility.plugin("&6Please leave a star on our github and a review on the spigot page.");
        LoggingUtility.plugin("&6You can use &e/mmq &6for the plugin commands!");
        LoggingUtility.plugin("&6");
        LoggingUtility.plugin(LoggingUtility.separator(32));

    }

    public void registerCommands() {
        CommandRegistry.registerCommand(new MarmaladeCommand());
    }

    public void registerListeners() {
        ListenerRegistry.registerListener(new PlayersListener());
    }

    @Override
    public void onDisable() {

        Databases.closeAll();

        getPlayerManager().unloadAll();

        LoggingUtility.plugin(LoggingUtility.separator(32));
        LoggingUtility.plugin("&6");
        LoggingUtility.figlet("MMQuests", '6');
        LoggingUtility.plugin("&aDisabled &6Marmalade&6&lQuests &6v" + this.getDescription().getVersion() + " (Built by AbdullahCXD)");
        LoggingUtility.plugin("&6");
        LoggingUtility.plugin("&6Please leave a star on our github and a review on the spigot page.");
        LoggingUtility.plugin("&6You can use &e/mmq &6for the plugin commands!");
        LoggingUtility.plugin("&6");
        LoggingUtility.plugin(LoggingUtility.separator(32));

    }
}

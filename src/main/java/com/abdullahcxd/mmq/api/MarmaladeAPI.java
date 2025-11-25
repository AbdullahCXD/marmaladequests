package com.abdullahcxd.mmq.api;

import com.abdullahcxd.mmq.MarmaladeQuests;
import com.abdullahcxd.mmq.economy.MQEconomyManager;
import com.abdullahcxd.mmq.player.MQPlayerManager;
import com.abdullahcxd.mmq.quests.MQQuestManager;
import lombok.Getter;
import lombok.Setter;

public class MarmaladeAPI {

    @Getter
    private static MarmaladeQuests plugin;
    @Getter
    private static MQQuestManager questManager;
    @Getter
    private static MQPlayerManager playerManager;
    @Getter
    private static MQEconomyManager economyManager;

    public static void setPlugin(MarmaladeQuests plugin) {
        MarmaladeAPI.plugin = plugin;
        MarmaladeAPI.questManager = plugin.getQuestManager();
        MarmaladeAPI.playerManager = plugin.getPlayerManager();
        MarmaladeAPI.economyManager = plugin.getEconomyManager();
    }

    public static void reload() {
        getPlugin().reloadConfig();
    }
}

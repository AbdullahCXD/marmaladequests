package com.abdullahcxd.mmq.listeners;

import com.abdullahcxd.mmq.MarmaladeQuests;
import com.abdullahcxd.mmq.api.MarmaladeAPI;
import lombok.Getter;
import org.bukkit.event.Listener;

public class MQListener implements Listener {

    @Getter
    private final MarmaladeQuests plugin;

    public MQListener() {
        plugin = MarmaladeAPI.getPlugin();
    }

}

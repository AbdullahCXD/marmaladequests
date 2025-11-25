package com.abdullahcxd.mmq.listeners;

import org.bukkit.Bukkit;

public class ListenerRegistry {

    public static void registerListener(MQListener listener) {
        Bukkit.getPluginManager().registerEvents(listener, listener.getPlugin());
    }

}

package com.abdullahcxd.mmq.listeners;

import com.abdullahcxd.mmq.api.MarmaladeAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayersListener extends MQListener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        /*

            Everytime a player logs in, the player data gets loaded.
            If there isn't any player data, a new one will be created.

         */

        Player player = event.getPlayer();

        MarmaladeAPI.getPlayerManager().loadOrCreatePlayer(player.getUniqueId());

    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        MarmaladeAPI.getPlayerManager().unloadPlayer(player.getUniqueId());
    }

}

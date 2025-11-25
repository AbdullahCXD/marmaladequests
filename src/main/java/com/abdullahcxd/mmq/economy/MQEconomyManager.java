package com.abdullahcxd.mmq.economy;

import com.abdullahcxd.mmq.MarmaladeQuests;
import com.abdullahcxd.mmq.api.MarmaladeAPI;
import com.abdullahcxd.mmq.player.MQPlayer;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class MQEconomyManager {

    private final MarmaladeQuests plugin;

    public MQEconomyManager(MarmaladeQuests plugin) {
        this.plugin = plugin;
    }

    public MQEconomyManager setBalance(UUID uuid, double balance) {
        MQPlayer player = MarmaladeAPI.getPlayerManager().loadPlayer(uuid);

        if (player == null) return this;

        player.setBalance(balance);
        player.save();
        return this;
    }

    public MQEconomyManager addBalance(UUID uuid, double balance) {
        MQPlayer player = MarmaladeAPI.getPlayerManager().loadPlayer(uuid);

        if (player == null) return this;

        player.addBalance(balance);
        player.save();
        return this;
    }

    public MQEconomyManager removeBalance(UUID uuid, double balance) {
        MQPlayer player = MarmaladeAPI.getPlayerManager().loadPlayer(uuid);

        if (player == null) return this;

        player.removeBalance(balance);
        player.save();
        return this;
    }

    public Set<MQPlayer> getBalanceTop(int limit) {
        List<MQPlayer> players = MarmaladeAPI.getPlayerManager().loadAllPlayers();

        return players.stream()
                .sorted((p1, p2) -> Double.compare(
                        p2.getMetadata().getBalance(),
                        p1.getMetadata().getBalance()
                ))
                .limit(limit)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }
}

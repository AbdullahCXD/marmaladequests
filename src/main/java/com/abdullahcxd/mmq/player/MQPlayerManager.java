package com.abdullahcxd.mmq.player;

import com.abdullahcxd.mmq.MarmaladeQuests;
import com.abdullahcxd.mmq.database.Databases;
import lombok.Getter;
import org.json.JSONObject;

import java.util.*;

@Getter
public class MQPlayerManager {

    private final MarmaladeQuests quests;
    private final Map<UUID, MQPlayer> playerCache = new HashMap<>();

    public MQPlayerManager(MarmaladeQuests quests) {
        this.quests = quests;
    }

    public MQPlayer loadPlayer(UUID uuid) {
        // Check cache first
        if (playerCache.containsKey(uuid)) {
            return playerCache.get(uuid);
        }

        // Load from database
        Optional<JSONObject> playerObject = Databases.playerTable.get(uuid.toString());

        if (playerObject.isEmpty()) return null;

        JSONObject resolvedPlayerObject = playerObject.get();
        PlayerMetadata metadata = new PlayerMetadata(0.0, new HashSet<>(), null);
        metadata.deserialize(resolvedPlayerObject);

        MQPlayer player = new MQPlayer(uuid, metadata);

        // Cache the loaded player
        playerCache.put(uuid, player);

        return player;
    }

    public MQPlayer loadOrCreatePlayer(UUID uuid) {
        MQPlayer player = loadPlayer(uuid);

        if (player == null) {
            player = new MQPlayer(uuid, new PlayerMetadata(0.0, new HashSet<>(), null));
            this.savePlayer(player);
        }

        return player;
    }

    public MQPlayer savePlayer(MQPlayer player) {
        PlayerMetadata metadata = player.getMetadata();
        JSONObject serializedPlayer = metadata.serialize();

        // Save to database
        Databases.playerTable.put(player.getUniqueId().toString(), serializedPlayer);

        // Update cache
        playerCache.put(player.getUniqueId(), player);

        return player;
    }

    public void unloadPlayer(UUID uuid) {
        MQPlayer player = playerCache.remove(uuid);
        if (player != null) {
            savePlayer(player); // Save before removing from cache
        }
    }

    public List<MQPlayer> loadAllPlayers() {
        List<MQPlayer> allPlayers = new ArrayList<>();

        // Get all player UUIDs from the database
        Map<String, JSONObject> allPlayerData = Databases.playerTable.asMap();

        for (Map.Entry<String, JSONObject> entry : allPlayerData.entrySet()) {
            UUID uuid = UUID.fromString(entry.getKey());

            // Check cache first
            if (playerCache.containsKey(uuid)) {
                allPlayers.add(playerCache.get(uuid));
                continue;
            }

            // Load from database and cache
            PlayerMetadata metadata = new PlayerMetadata(0.0, new HashSet<>(), null);
            metadata.deserialize(entry.getValue());

            MQPlayer player = new MQPlayer(uuid, metadata);
            playerCache.put(uuid, player);
            allPlayers.add(player);
        }

        return allPlayers;
    }

}
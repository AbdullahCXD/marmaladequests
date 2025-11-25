package com.abdullahcxd.mmq.player;

import com.abdullahcxd.mmq.MarmaladeQuests;
import com.abdullahcxd.mmq.database.Databases;
import lombok.Getter;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter
public class MQPlayerManager {

    private final MarmaladeQuests quests;
    private final Map<UUID, MQPlayer> playerCache = new HashMap<>();

    public MQPlayerManager(MarmaladeQuests quests) {
        this.quests = quests;
    }

    public MQPlayer loadPlayer(UUID uuid) {
        if (playerCache.containsKey(uuid)) {
            return playerCache.get(uuid);
        }

        Optional<JSONObject> playerObject = Databases.playerTable.get(uuid.toString());

        if (playerObject.isEmpty()) return null;

        JSONObject resolvedPlayerObject = playerObject.get();
        PlayerMetadata metadata = new PlayerMetadata(0.0, new HashSet<>(), null);
        metadata.deserialize(resolvedPlayerObject);

        MQPlayer player = new MQPlayer(uuid, metadata);

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

        Databases.playerTable.put(player.getUniqueId().toString(), serializedPlayer);

        playerCache.put(player.getUniqueId(), player);

        return player;
    }

    public void unloadPlayer(UUID uuid) {
        MQPlayer player = playerCache.remove(uuid);
        if (player != null) {
            savePlayer(player);
        }
    }

    public void unloadAll() {
        for (UUID uuid : playerCache.keySet()) {
            unloadPlayer(uuid);
        }
    }

}
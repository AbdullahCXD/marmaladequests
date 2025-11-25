package com.abdullahcxd.mmq.quests;

import com.abdullahcxd.mmq.MarmaladeQuests;
import com.abdullahcxd.mmq.api.MarmaladeAPI;
import com.abdullahcxd.mmq.player.MQPlayer;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class MQQuestManager {

    private final Map<String, Quest> quests = new HashMap<>();

    public MQQuestManager(MarmaladeQuests plugin) {}

    public MQQuestManager registerQuest(Quest quest) {
        this.quests.put(quest.getQuestInfo().getId(), quest);
        return this;
    }

    public Quest getQuest(String id) {
        return this.quests.get(id);
    }

    public boolean hasFinishedQuest(UUID uuid, String questId) {
        MQPlayer player = MarmaladeAPI.getPlayerManager().loadPlayer(uuid);
        if (player == null) return false;

        return player.hasFinishedQuest(questId);
    }

    public String getCurrentQuest(UUID uuid) {
        MQPlayer player = MarmaladeAPI.getPlayerManager().loadPlayer(uuid);
        if (player == null) return null;

        return player.getCurrentQuest();
    }

    public MQQuestManager removeQuest(Quest quest) {
        this.quests.remove(quest.getQuestInfo().getId());
        return this;
    }

    public MQQuestManager finishQuest(UUID uuid, Quest quest) {
        MQPlayer player = MarmaladeAPI.getPlayerManager().loadPlayer(uuid);
        if (player == null) return null;

        player.addFinishedQuest(quest.getQuestInfo().getId());
        player.save();
        return this;
    }

}

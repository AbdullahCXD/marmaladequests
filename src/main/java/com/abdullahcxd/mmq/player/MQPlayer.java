package com.abdullahcxd.mmq.player;

import com.abdullahcxd.mmq.api.MarmaladeAPI;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Getter
public class MQPlayer {

    private final PlayerMetadata metadata;
    private final UUID uniqueId;

    public MQPlayer(UUID uniqueId, PlayerMetadata metadata) {
        this.metadata = metadata;
        this.uniqueId = uniqueId;
    }


    public MQPlayer setBalance(double balance) {
        this.metadata.setBalance(balance);
        return this;
    }

    public MQPlayer addBalance(double balance) {
        this.setBalance(this.getBalance() + balance);
        return this;
    }

    public MQPlayer removeBalance(double balance) {
        this.setBalance(this.getBalance() - balance);
        return this;
    }

    public MQPlayer addFinishedQuest(String questId) {
        this.metadata.getFinishedQuests().add(questId);
        return this;
    }

    public boolean hasFinishedQuest(String questId) {
        return this.metadata.getFinishedQuests().contains(questId);
    }

    public MQPlayer removeFinishedQuest(String questId) {
        this.metadata.getFinishedQuests().remove(questId);
        return this;
    }

    public MQPlayer setCurrentQuest(String questId) {
        this.metadata.setCurrentQuest(questId);
        return this;
    }

    public double getBalance() {
        return this.metadata.getBalance();
    }

    public Set<String> getFinishedQuests() {
        return this.metadata.getFinishedQuests();
    }

    public String getCurrentQuest() {
        return this.metadata.getCurrentQuest();
    }

    public boolean hasCurrentQuest() {
        return getCurrentQuest() != null;
    }

    public void save() {
        MarmaladeAPI.getPlayerManager().savePlayer(this);
    }
}

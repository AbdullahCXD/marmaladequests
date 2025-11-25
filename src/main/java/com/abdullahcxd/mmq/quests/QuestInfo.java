package com.abdullahcxd.mmq.quests;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class QuestInfo {

    // Underlying logic:
    private String id;

    // Displayed:
    private String name, description;

    private QuestReward reward;

}

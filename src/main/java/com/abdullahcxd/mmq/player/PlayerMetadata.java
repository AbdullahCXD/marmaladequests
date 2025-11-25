package com.abdullahcxd.mmq.player;

import com.abdullahcxd.mmq.utils.Serialization;
import com.abdullahcxd.mmq.utils.StringUtility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Getter
@Setter
public class PlayerMetadata implements Serialization {

    private double balance;
    private Set<String> finishedQuests;
    private String currentQuest;

    @Override
    public void deserialize(JSONObject resolvedPlayerObject) {
        double balance = resolvedPlayerObject.getDouble("balance");
        List<String> finishedQuests = StringUtility.getStringsOnly(resolvedPlayerObject.getJSONArray("finishedQuests").toList());
        String currentQuest = resolvedPlayerObject.getString("currentQuest");

        this.setBalance(balance);
        this.setFinishedQuests(new HashSet<>(finishedQuests));
        this.setCurrentQuest(currentQuest);
    }

    @Override
    public JSONObject serialize() {
        JSONObject object = new JSONObject();
        object.put("balance", balance);
        object.put("finishedQuests", new ArrayList<>(finishedQuests));
        object.put("currentQuest", currentQuest);

        return object;
    }
}

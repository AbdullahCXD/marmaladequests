package com.abdullahcxd.mmq.utils;

import org.json.JSONObject;

public interface Serialization {

    void deserialize(JSONObject object);
    JSONObject serialize();

}

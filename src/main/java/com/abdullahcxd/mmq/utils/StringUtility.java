package com.abdullahcxd.mmq.utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtility {

    public static List<String> getStringsOnly(List<Object> object) {
        List<String> strings = new ArrayList<>();

        for (Object obj : object) {
            if (obj instanceof String) {
                strings.add((String) obj);
            }
        }

        return strings;
    }

}

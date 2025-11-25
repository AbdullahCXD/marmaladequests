package com.abdullahcxd.mmq.confirmation;

import java.util.ArrayList;
import java.util.List;

public class Confirmation {

    private static final List<String> confirmation_list = new ArrayList<>();

    public static void createNew(String id) {
        if (confirmation_list.contains(id)) return;
        confirmation_list.add(id);
    }

    public static boolean willConfirmOnNext(String id) {
        return confirmation_list.contains(id);
    }

    public static void removeConfirmation(String id) {
        confirmation_list.remove(id);
    }

}

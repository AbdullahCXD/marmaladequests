package com.abdullahcxd.mmq.threading;

public class ThreadingAPI {

    public static void thread(String id, Runnable runnable) {
        try {
            new Thread(id) {
                @Override
                public void run() {
                    runnable.run();
                }
            }.start();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

}

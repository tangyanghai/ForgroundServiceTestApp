package com.example.administrator.foregroundservicetest.utils;

/**
 * <p>@author : tangyanghai</p>
 * <p>@time : 2020/4/17</p>
 * <p>@for : </p>
 * <p></p>
 */
public class NotificationIdUtils {
    private boolean useSameId;

    private static final NotificationIdUtils ourInstance = new NotificationIdUtils();

    public static NotificationIdUtils getInstance() {
        return ourInstance;
    }

    private NotificationIdUtils() {
    }

    public void changeUseSameId(boolean useSameId){
        this.useSameId = useSameId;
    }

    public int getId(){
        if (useSameId) {
            return 0x11;
        }
        return (int) (System.currentTimeMillis() % 100_000);
    }
}

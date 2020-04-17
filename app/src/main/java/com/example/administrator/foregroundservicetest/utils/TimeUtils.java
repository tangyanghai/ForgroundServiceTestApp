package com.example.administrator.foregroundservicetest.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>@author : tangyanghai</p>
 * <p>@time : 2020/4/17</p>
 * <p>@for : </p>
 * <p></p>
 */
public class TimeUtils {
    private static TimeUtils instance = new TimeUtils();
    private SimpleDateFormat simpleDateFormat;

    public static TimeUtils getInstance() {
        return instance;
    }

    private TimeUtils() {

    }

    public String getTime() {
        // HH:mm:ss
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat("MM月dd日 HH:mm:ss");
        }
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }
}

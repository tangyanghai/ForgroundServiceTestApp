package com.example.administrator.foregroundservicetest;

import android.app.Application;
import android.content.Context;

import com.example.administrator.foregroundservicetest.cache.BoxCache;

import org.xutils.x;

import cn.jpush.android.api.JPushInterface;
import io.objectbox.BoxStore;

/**
 * <p>@author : tangyanghai</p>
 * <p>@time : 2020/4/14</p>
 * <p>@for : </p>
 * <p></p>
 */
public class App extends Application {
    public static Context app;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        JPushInterface.init(this);
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
        BoxCache.getInstance().init(this);
    }
}

package com.example.administrator.foregroundservicetest.cache;

import android.app.Application;

import com.example.administrator.foregroundservicetest.bean.MyObjectBox;

import java.util.ArrayList;
import java.util.List;

import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * <p>@author : tangyanghai</p>
 * <p>@time : 2020/4/18</p>
 * <p>@for : ObjectBox实现的数据库缓存</p>
 * <p>@for : App退出重新进入,数据还在</p>
 * <p></p>
 */
public class BoxCache implements ICache {
    BoxStore bs;

    private static final BoxCache ourInstance = new BoxCache();

    public static BoxCache getInstance() {
        return ourInstance;
    }

    private BoxCache() {
    }

    public void init(Application context){
       bs = MyObjectBox.builder()
                .androidContext(context.getApplicationContext())
                .build();
    }

    @Override
    public <T> void add(T... list) {
        if (list == null || list.length == 0) {
            return;
        }

        Class<T> clz = (Class<T>) list[0].getClass();

        Box<T> box = bs.boxFor(clz);

        box.put(list);
    }

    @Override
    public <T> List<T> find(Class<T> cls) {

        Box<T> box = bs.boxFor(cls);

        if (box == null) {
            return new ArrayList<>();
        }

        return box.getAll();
    }
}

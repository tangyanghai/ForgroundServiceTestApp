package com.example.administrator.foregroundservicetest.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * <p>@author : tangyanghai</p>
 * <p>@time : 2020/4/16</p>
 * <p>@for : 内存数据缓存</p>
 * <p></p>
 */
class NativeCache implements ICache {
    private static final NativeCache ourInstance = new NativeCache();

    private HashMap<String, List<? extends Object>> map;

    public static NativeCache getInstance() {
        return ourInstance;
    }

    private NativeCache() {
        map = new HashMap<>();
    }

    public synchronized <T> void add(T... list) {
        if (list != null && list.length>0) {
            Class cls = null;
            for (T t : list) {
                cls = t.getClass();
                break;
            }
            String name = cls.getName();
            List l = map.get(name);
            if (l == null) {
                l = new ArrayList<T>();
                map.put(name, l);
            }
            l.addAll(Arrays.asList(list));
        }
    }

    public synchronized <T> List<T> find(Class<T> cls){
        List<?> list = map.get(cls.getName());
        return (List<T>) list;
    }
}

package com.example.administrator.foregroundservicetest.cache;

import java.util.List;

/**
 * <p>@author : tangyanghai</p>
 * <p>@time : 2020/4/16</p>
 * <p>@for : </p>
 * <p></p>
 */
public interface ICache {
    <T> void add(T... list);

    <T> List<T> find(Class<T> cls);
}

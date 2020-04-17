package com.example.administrator.foregroundservicetest.cache;

import java.util.List;

/**
 * <p>@author : tangyanghai</p>
 * <p>@time : 2020/4/16</p>
 * <p>@for : 获取到数据的回调</p>
 * <p></p>
 */
public interface OnFindData<T> {
    void onFind(List<T> ts);
}

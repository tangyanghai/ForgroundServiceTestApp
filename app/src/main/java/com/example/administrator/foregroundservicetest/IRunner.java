package com.example.administrator.foregroundservicetest;

import android.app.Activity;

/**
 * <p>@author : tangyanghai</p>
 * <p>@time : 2020/4/13</p>
 * <p>@for : </p>
 * <p></p>
 */
public interface IRunner {
    Activity getActivity();

    void update();
}

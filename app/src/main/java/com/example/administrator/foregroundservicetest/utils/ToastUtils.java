package com.example.administrator.foregroundservicetest.utils;

import android.widget.Toast;

import com.example.administrator.foregroundservicetest.App;

/**
 * <p>@author : tangyanghai</p>
 * <p>@time : 2020/4/16</p>
 * <p>@for : </p>
 * <p></p>
 */
public class ToastUtils {
    public static void show(String msg){
        Toast.makeText(App.app,msg,Toast.LENGTH_LONG).show();
    }
}

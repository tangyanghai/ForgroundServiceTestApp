package com.example.administrator.foregroundservicetest;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.administrator.foregroundservicetest.service.EnumServiceAction;
import com.example.administrator.foregroundservicetest.service.EventControlService;
import com.example.administrator.foregroundservicetest.service.RunningService;
import com.example.administrator.foregroundservicetest.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

/**
 * <p>@author : tangyanghai</p>
 * <p>@time : 2020/4/24</p>
 * <p>@for : </p>
 * <p></p>
 */
public class PushSettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_setting);
        ButterKnife.bind(this);
        RunningService.start(this);
    }

    @OnClick({R.id.btn_turn_on_push, R.id.btn_turn_off, R.id.btn_check_connect, R.id.btn_check_regid, R.id.btn_auto_check_connect})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_turn_on_push:
                JPushInterface.resumePush(this);
                break;
            case R.id.btn_turn_off:
                JPushInterface.stopPush(this);
                break;
            case R.id.btn_check_connect:
                ToastUtils.show(JPushInterface.isPushStopped(this) ? "未连接" : "已连接");
                break;
            case R.id.btn_check_regid:
                ToastUtils.show(JPushInterface.getRegistrationID(this));
                break;
            case R.id.btn_auto_check_connect:
                EventBus.getDefault().post(new EventControlService(EnumServiceAction.ACTION_CONFIRM_JPUSH_CONNECT));
                break;
        }
    }









}

package com.example.administrator.foregroundservicetest.jpush;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.example.administrator.foregroundservicetest.App;
import com.example.administrator.foregroundservicetest.R;
import com.example.administrator.foregroundservicetest.ServiceActivity;
import com.example.administrator.foregroundservicetest.bean.PushRecord;
import com.example.administrator.foregroundservicetest.cache.CacheUtils;
import com.example.administrator.foregroundservicetest.utils.LogUtils;
import com.example.administrator.foregroundservicetest.utils.NotificationIdUtils;
import com.example.administrator.foregroundservicetest.utils.TimeUtils;

import org.xutils.common.util.LogUtil;

import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.service.JPushMessageReceiver;

import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;

/**
 * <p>@author : tangyanghai</p>
 * <p>@time : 2020/4/14</p>
 * <p>@for : </p>
 * <p></p>
 */
public class JPushMessageReceiverImpl extends JPushMessageReceiver {
    private final static String TAG = "==JPushMessageReceiver==";
    private NotificationChannel normalChannel;
    //普通通知channel_id.
    String normalChannelId = "notification_channel_id_02";

    @Override
    public void onMessage(Context context, CustomMessage msg) {
        super.onMessage(context, msg);
        notify(msg.title, msg.message);
    }

    /**
     * 该方法会在以下情况触发时回调。
     * 1.sdk每次启动后都会检查通知开关状态并通过该方法回调给开发者。
     * 2.当sdk检查到通知状态变更时会通过该方法回调给开发者。
     *
     * @param isOn   通知开关状态
     * @param source 0为sdk启动，1为检测到通知开关状态变更
     */
    @Override
    public void onNotificationSettingsCheck(Context context, boolean isOn, int source) {
        LogUtils.e(TAG, "onNotificationSettingsCheck: " + "isOn = " + isOn + "; source = " + source);
    }

    /**
     * 注册失败的回调
     */
    @Override
    public void onCommandResult(Context context, CmdMessage cmdMessage) {
        LogUtils.e(TAG, "onCommandResult: " + cmdMessage.toString());
    }

    /**
     * 注册成功的回调
     */
    @Override
    public void onRegister(Context context, String s) {
        LogUtils.e(TAG, "onRegister: " + s);
    }

    /**
     * 长连接状态
     */
    @Override
    public void onConnected(Context context, boolean isConnected) {
        LogUtils.e(TAG, "onConnected: isConnected = " + isConnected);
    }

    private void notify(String title, String msg) {
        CacheUtils.getInstance().add(new PushRecord(title, TimeUtils.getInstance().getTime()));
        NotificationManager notificationManager = (NotificationManager) App.app.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            Notification notification = createOtherNotification(title, msg);
            notificationManager.notify(NotificationIdUtils.getInstance().getId(), notification);
        }
    }

    /**
     * 创建一般通知
     */
    private Notification createOtherNotification(String title, String msg) {
        NotificationManager notificationManager = (NotificationManager) App.app.getSystemService(Context.NOTIFICATION_SERVICE);

        // Android8.0以上的系统，新建消息通道
        int id = R.raw.minotificationsound;
        Uri uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + App.app.getPackageName() + "/" + id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //用户可见的通道名称
            // 通道的重要程度
            /* if (normalChannel == null) {*/
            String channelName = "一般通知";
            normalChannel = new NotificationChannel(normalChannelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            normalChannel.setDescription("一般通知");
            //LED灯
            normalChannel.enableLights(true);
            normalChannel.setLightColor(Color.RED);
            //震动
            normalChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            normalChannel.enableVibration(false);
            normalChannel.setSound(uri, new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION).build());
            normalChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(normalChannel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(App.app, normalChannelId);
        //通知小图标
        builder.setSmallIcon(R.mipmap.ic_launcher);
        //通知标题
        builder.setContentTitle(title);
        //通知内容
        builder.setContentText(msg);
        builder.setAutoCancel(false);
        builder.setSound(uri, AudioManager.STREAM_NOTIFICATION);
        //设定通知显示的时间
        builder.setWhen(System.currentTimeMillis());
        builder.setPriority(PRIORITY_MAX);
        //设定启动的内容
        Intent activityIntent = new Intent(App.app, ServiceActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(App.app, 1, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        //创建通知并返回
        return builder.build();
    }
}

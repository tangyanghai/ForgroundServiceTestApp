package com.example.administrator.foregroundservicetest.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.example.administrator.foregroundservicetest.App;
import com.example.administrator.foregroundservicetest.R;
import com.example.administrator.foregroundservicetest.ServiceActivity;
import com.example.administrator.foregroundservicetest.bean.ServiceRunRecord;
import com.example.administrator.foregroundservicetest.cache.CacheUtils;
import com.example.administrator.foregroundservicetest.jpush.EventMsg;
import com.example.administrator.foregroundservicetest.utils.LogUtils;
import com.example.administrator.foregroundservicetest.utils.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.jpush.android.api.JPushInterface;

import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;

/**
 * <p>@author : tangyanghai</p>
 * <p>@time : 2020/4/13</p>
 * <p>@for : </p>
 * <p></p>
 */
public class RunningService extends Service {


    public static final String TAG = "==RunningService==";
    public static boolean isRunning = false;
    /**
     * 是否要保证极光连接
     */
    private boolean isConfirmJpushConnect;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mHandler.removeMessages(msg.what);
            CacheUtils.getInstance().add(new ServiceRunRecord("RunningService", TimeUtils.getInstance().getTime()));
            sendMessageDelayed(getRunningMessage(), 10_000);
            if (isConfirmJpushConnect) {
                boolean pushStopped = JPushInterface.isPushStopped(App.app);
                LogUtils.e(TAG, pushStopped ? "未连接" : "已连接");
                if (pushStopped) {
                    JPushInterface.resumePush(App.app);
                }
            }
        }
    };

    private NotificationChannel foregroundChannel;

    //前台服务通知channel id
    String foregroundChannelId = "notification_channel_id_01";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        isRunning = true;
        super.onCreate();
        Notification notification = createForegroundNotification();
        startForeground(110, notification);
        EventBus.getDefault().register(this);

        mHandler.sendMessage(getRunningMessage());
    }

    private Message getRunningMessage() {
        Message message = mHandler.obtainMessage();
        message.what = 0;
        return message;
    }

    private Message getControlMessage(EventControlService event) {
        Message message = mHandler.obtainMessage();
        message.what = 1;
        message.obj = event;
        return message;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventMsg msg) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventControlService(EventControlService event) {
        switch (event.getAction()) {
            case ACTION_CONFIRM_JPUSH_CONNECT:
                isConfirmJpushConnect = true;
                break;
            case ACTION_STOP_CONFIRM_JPUSH_CONNECT:
                isConfirmJpushConnect = false;
                break;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        stopForeground(true);
        isRunning = false;
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    /**
     * 创建服务通知
     */
    private Notification createForegroundNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

// 唯一的通知通道的id.

// Android8.0以上的系统，新建消息通道
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /*if (foregroundChannel == null) {*/

//用户可见的通道名称
            String channelName = "前台服务";
//通道的重要程度
            int importance = NotificationManager.IMPORTANCE_HIGH;
            foregroundChannel = new NotificationChannel(foregroundChannelId, channelName, importance);
            foregroundChannel.setDescription("Channel description");
//LED灯
            foregroundChannel.enableLights(true);
            foregroundChannel.setLightColor(Color.RED);
//震动
            foregroundChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            foregroundChannel.enableVibration(false);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(foregroundChannel);
            }
            /*}*/
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, foregroundChannelId);
        //通知小图标
        builder.setSmallIcon(R.mipmap.ic_launcher);
        //通知标题
        builder.setContentTitle("推送设置");
        //通知内容
        builder.setContentText("推送设置正在运行中......");
        builder.setAutoCancel(false);
        //设定通知显示的时间
        builder.setWhen(System.currentTimeMillis());
        builder.setPriority(PRIORITY_MAX);
        builder.setOngoing(true);
        //设定启动的内容
        Intent activityIntent = new Intent(this, ServiceActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        //创建通知并返回
        return builder.build();
    }


    public static void start(Context context) {
        if (!RunningService.isRunning) {
            Intent service = new Intent(App.app, RunningService.class);
            // Android 8.0使用startForegroundService在前台启动新服务
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(service);
            } else {
                context.startService(service);
            }
        }
    }

}

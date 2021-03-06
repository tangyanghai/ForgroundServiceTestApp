package com.example.administrator.foregroundservicetest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.example.administrator.foregroundservicetest.bean.ServiceRunRecord;
import com.example.administrator.foregroundservicetest.cache.CacheUtils;
import com.example.administrator.foregroundservicetest.jpush.EventMsg;
import com.example.administrator.foregroundservicetest.utils.TimeUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import static android.media.AudioAttributes.USAGE_MEDIA;
import static android.support.v4.app.NotificationCompat.PRIORITY_MAX;

/**
 * <p>@author : tangyanghai</p>
 * <p>@time : 2020/4/13</p>
 * <p>@for : </p>
 * <p></p>
 */
public class RunningService extends Service {

    public static boolean isRunning = false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            CacheUtils.getInstance().add(new ServiceRunRecord("RunningService", TimeUtils.getInstance().getTime()));
            sendMessageDelayed(obtainMessage(), 60_000);
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
        mHandler.sendMessage(mHandler.obtainMessage());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(EventMsg msg) {

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
        builder.setContentTitle("前台通知");
        //通知内容
        builder.setContentText("app正在运行中......");
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


}

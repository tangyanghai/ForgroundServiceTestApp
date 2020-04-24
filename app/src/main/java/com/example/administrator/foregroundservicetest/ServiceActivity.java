package com.example.administrator.foregroundservicetest;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.administrator.foregroundservicetest.bean.ActivityRunRecord;
import com.example.administrator.foregroundservicetest.bean.PushRecord;
import com.example.administrator.foregroundservicetest.bean.ServiceRunRecord;
import com.example.administrator.foregroundservicetest.cache.CacheUtils;
import com.example.administrator.foregroundservicetest.cache.OnFindData;
import com.example.administrator.foregroundservicetest.service.RunningService;
import com.example.administrator.foregroundservicetest.utils.NotificationIdUtils;
import com.example.administrator.foregroundservicetest.utils.TimeUtils;
import com.example.administrator.foregroundservicetest.utils.ToastUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

/**
 * <p>@author : tangyanghai</p>
 * <p>@time : 2020/4/13</p>
 * <p>@for : </p>
 * <p></p>
 */
public class ServiceActivity extends AppCompatActivity implements IRunner {

    Handler handler;
    TextView tvRunning;
    @BindView(R.id.check_box)
    CheckBox checkBox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_running);
        ButterKnife.bind(this);
        tvRunning = findViewById(R.id.tv);
        handler = new RunningHandler(this);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                NotificationIdUtils.getInstance().changeUseSameId(isChecked);
            }
        });
    }

    private void startService() {
        if (!RunningService.isRunning) {
            Intent service = new Intent(this, RunningService.class);
            // Android 8.0使用startForegroundService在前台启动新服务
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(service);
            } else {
                startService(service);
            }
            handler.sendEmptyMessage(0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int enabled = JPushInterface.isNotificationEnabled(this);
        if (enabled == 1) {
            startService();
        } else {
            endServiceNotificationDisable();
            new AlertDialog.Builder(this)
                    .setMessage("请先打开通知权限")
                    .setPositiveButton("去打开", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            JPushInterface.goToAppNotificationSettings(ServiceActivity.this);
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    private void endServiceNotificationDisable() {
        handler.removeCallbacksAndMessages(null);
        tvRunning.setText("前台服务未运行");
        if (RunningService.isRunning) {
            stopService(new Intent(this, RunningService.class));
        }
    }

    private long lastClickBackTime;

    @Override
    public void onBackPressed() {
        long cur = System.currentTimeMillis();
        if (cur - lastClickBackTime > 1000) {
            Toast.makeText(this, "再点一次退出应用", Toast.LENGTH_LONG).show();
            lastClickBackTime = cur;
        } else {
            lastClickBackTime = cur;
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (RunningService.isRunning) {
            stopService(new Intent(this, RunningService.class));
        }
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    public void startSendNotify(String title, int id) {
        //YjFiZjY3Zjc0MTMxMGU5ZDdmMjM2YmNiOjQ2ZjYxMjkzMWM2ODk5NzJkYWUxMzJkYQ==
        RequestParams params = new RequestParams();
        params.addHeader("Authorization", "Basic YjFiZjY3Zjc0MTMxMGU5ZDdmMjM2YmNiOjQ2ZjYxMjkzMWM2ODk5NzJkYWUxMzJkYQ==");
        params.addHeader("Accept", "application/json");
        params.addHeader("Content-Type", "application/json");
        params.setUri("https://api.jpush.cn/v3/push");
        params.setAsJsonContent(true);
        Message1 msg = new Message1(title + "时间 = " + TimeUtils.getInstance().getTime(), title);
        PushEntity entity = new PushEntity("android", new Audit(JPushInterface.getRegistrationID(this)), msg);
        String s = JSON.toJSONString(entity);
        Log.e("=通知内容=", s);
        params.setBodyContent(s);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                ToastUtils.show("推送成功");
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                ToastUtils.show("推送失败");
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    @Override
    public void update() {
        String time = TimeUtils.getInstance().getTime();
        tvRunning.setText("更新时间" + time);
        CacheUtils.getInstance().add(new ActivityRunRecord(getClass().getSimpleName(), time));
    }


    private <T> void showAlertDiaot(final List<T> list, final ViewBinder<T> binder) {
        if (list == null || list.size() == 0) {
            ToastUtils.show("数据为空");
            return;
        }
        final List<T> revert = new ArrayList<>();
        for (T t : list) {
            revert.add(0, t);
        }
        View v = LayoutInflater.from(this)
                .inflate(R.layout.dialog_push_record, null, false);
        RecyclerView rv = v.findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View v = LayoutInflater.from(ServiceActivity.this).inflate(R.layout.item, viewGroup, false);
                RecyclerView.ViewHolder viewHolder = new RecyclerView.ViewHolder(v) {
                };
                return viewHolder;
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                View itemView = viewHolder.itemView;
                T t = revert.get(i);
                binder.bind(itemView, t);
            }

            @Override
            public int getItemCount() {
                return revert.size();
            }
        });
        new AlertDialog.Builder(this)
                .setCustomTitle(v)
                .create()
                .show();
    }

    @OnClick({R.id.btn, R.id.btn_check_regid, R.id.btn_service_run_record, R.id.btn_activity_run_record, R.id.btn_push_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn:
                startSendNotify("手动发送通知", 2);
                break;
            case R.id.btn_check_regid:
                checkRegId();
                break;
            case R.id.btn_service_run_record:
                showServiceRunRecord();
                break;
            case R.id.btn_activity_run_record:
                showActivityRunRecord();
                break;
            case R.id.btn_push_record:
                showPushRecord();
                break;
        }
    }

    private void showPushRecord() {
        CacheUtils.getInstance().findAsyn(PushRecord.class, new OnFindData<PushRecord>() {
            @Override
            public void onFind(List<PushRecord> list) {
                if (list == null || list.isEmpty()) {
                    ToastUtils.show("未获取到通知记录");
                    return;
                }

                showPushRecord(list);
            }
        });
    }

    private void showPushRecord(List<PushRecord> list) {
        showAlertDiaot(list, new ViewBinder<PushRecord>() {
            @Override
            public void bind(@NonNull View v, PushRecord bean) {
                TextView time = v.findViewById(R.id.tv_time);
                time.setText(bean.getTime());
                TextView status = v.findViewById(R.id.tv_status);
                status.setVisibility(View.VISIBLE);
                status.setText(bean.getTitle());
            }
        });
    }

    private void showActivityRunRecord() {
        CacheUtils.getInstance().findAsyn(ActivityRunRecord.class, new OnFindData<ActivityRunRecord>() {
            @Override
            public void onFind(List<ActivityRunRecord> list) {
                if (list == null || list.isEmpty()) {
                    ToastUtils.show("未获取到Activity运行记录");
                    return;
                }
                showActivityRunRecord(list);
            }
        });
    }

    private void showActivityRunRecord(@NonNull List<ActivityRunRecord> list) {
        showAlertDiaot(list, new ViewBinder<ActivityRunRecord>() {
            @Override
            public void bind(@NonNull View v, ActivityRunRecord bean) {
                TextView time = v.findViewById(R.id.tv_time);
                time.setText(bean.getTime());
                TextView status = v.findViewById(R.id.tv_status);
                status.setVisibility(View.GONE);
            }
        });
    }

    private void showServiceRunRecord() {
        CacheUtils.getInstance().findAsyn(ServiceRunRecord.class, new OnFindData<ServiceRunRecord>() {
            @Override
            public void onFind(List<ServiceRunRecord> list) {
                if (list == null || list.isEmpty()) {
                    ToastUtils.show("未获取到前台服务运行记录");
                    return;
                }
                showServiceRunRecord(list);
            }
        });
    }

    private void showServiceRunRecord(@NonNull List<ServiceRunRecord> list) {
        showAlertDiaot(list, new ViewBinder<ServiceRunRecord>() {
            @Override
            public void bind(@NonNull View v, ServiceRunRecord bean) {
                TextView time = v.findViewById(R.id.tv_time);
                time.setText(bean.getTime());
                TextView status = v.findViewById(R.id.tv_status);
                status.setVisibility(View.GONE);
            }
        });
    }

    private void checkRegId() {
        String registrationID = JPushInterface.getRegistrationID(this);
        if (TextUtils.isEmpty(registrationID)) {
            ToastUtils.show("未获取到REG_ID");
            return;
        }
        //获取剪贴板管理器：
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 创建普通字符型ClipData
        ClipData mClipData = ClipData.newPlainText("REG_ID", registrationID);
        if (cm == null) {
            ToastUtils.show(registrationID + "\n复制失败");
            return;
        }
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData);
        ToastUtils.show(registrationID + "\n已复制到剪切板");
    }


    interface ViewBinder<T> {
        void bind(@NonNull View v, T bean);
    }

    static class PushEntity {
        private String platform;
        private Audit audience;
        private Message1 message;

        public PushEntity(String platform, Audit audience, Message1 message) {
            this.platform = platform;
            this.audience = audience;
            this.message = message;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public Audit getAudience() {
            return audience;
        }

        public void setAudience(Audit audience) {
            this.audience = audience;
        }

        public Message1 getMessage() {
            return message;
        }

        public void setMessage(Message1 message) {
            this.message = message;
        }
    }


    static class Audit {
        private List<String> registration_id;

        public Audit(String registration_id) {
            this.registration_id = new ArrayList<>();
            this.registration_id.add(registration_id);
        }

        public List<String> getRegistration_id() {
            return registration_id;
        }

        public void setRegistration_id(List<String> registration_id) {
            this.registration_id = registration_id;
        }
    }

    static class Message1 {
        private String msg_content;
        private String title;

        public String getMsg_content() {
            return msg_content;
        }

        public void setMsg_content(String msg_content) {
            this.msg_content = msg_content;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public Message1(String msg_content, String title) {
            this.msg_content = msg_content;
            this.title = title;
        }
    }

    public static class RunningHandler extends Handler {
        WeakReference<IRunner> weakAct;

        public RunningHandler(IRunner act) {
            this.weakAct = new WeakReference<>(act);
        }

        boolean isActivityRunning() {
            return weakAct != null && weakAct.get() != null && weakAct.get().getActivity() != null && !weakAct.get().getActivity().isFinishing();
        }

        @Override
        public void handleMessage(Message msg) {
            if (isActivityRunning()) {
                Log.e("===", "app is running");
                sendEmptyMessageDelayed(0, 60_000);
                weakAct.get().update();
            }
        }
    }

}

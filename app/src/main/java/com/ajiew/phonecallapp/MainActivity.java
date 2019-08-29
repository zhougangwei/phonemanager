package com.ajiew.phonecallapp;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ajiew.phonecallapp.listenphonecall.CallListenerService;
import com.ajiew.phonecallapp.phonecallui.OnePiexActivity;

import java.lang.reflect.Field;

import ezy.assist.compat.SettingsCompat;


public class MainActivity extends AppCompatActivity {

    private static MainActivity INSTANCE;

    private Switch switchListenCall;

    private CompoundButton.OnCheckedChangeListener switchCallCheckChangeListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        INSTANCE = this;
        setContentView(R.layout.activity_main);
        String[]  permissions = new String[4];

       permissions[0] = "android.permission.READ_PHONE_STATE";
        permissions[1] = "android.permission.PROCESS_OUTGOING_CALLS";
        permissions[2] = "android.permission.READ_CALL_LOG";
        permissions[3] = "android.permission.READ_PHONE_STATE";
/*

        permissions[0] = "android.permission.READ_PHONE_STATE";
        permissions[1] = "android.permission.PROCESS_OUTGOING_CALLS";
        permissions[2] = "android.permission.READ_CONTACTS";
        permissions[3] = "android.permission.READ_CALL_LOG";
        permissions[4] = "android.permission.READ_CONTACTS";
        permissions[5] = "android.permission.CALL_PHONE";
        permissions[6] = "android.permission.READ_PHONE_STATE";
*/
        this.requestPermissions(permissions, 321);
        initView();
    }
    public static MainActivity getContext() {
        return INSTANCE;
    }

    private void initView() {
        switchListenCall = findViewById(R.id.switch_call_listenr);
        TextView tv = findViewById(R.id.tv_title);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnePiexActivity.startOnePix(MainActivity.this);
            }
        });


        // 使用使用 SettingsCompat 检查是否开启了权限
        switchCallCheckChangeListener = (buttonView, isChecked) -> {
            if (isChecked && !SettingsCompat.canDrawOverlays(MainActivity.this)) {

                // 请求 悬浮框 权限
                askForDrawOverlay();

                // 未开启时清除选中状态，同时避免回调
                switchListenCall.setOnCheckedChangeListener(null);
                switchListenCall.setChecked(false);
                switchListenCall.setOnCheckedChangeListener(switchCallCheckChangeListener);
                return;
            }

            Intent callListener = new Intent(MainActivity.this, CallListenerService.class);
            if (isChecked) {
                startService(callListener);
                Toast.makeText(this, "电话监听服务已开启", Toast.LENGTH_SHORT).show();
            } else {
                stopService(callListener);
                Toast.makeText(this, "电话监听服务已关闭", Toast.LENGTH_SHORT).show();
            }
        };
        switchListenCall.setOnCheckedChangeListener(switchCallCheckChangeListener);
    }

    private void askForDrawOverlay() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this)
                .setTitle("允许显示悬浮框")
                .setMessage("为了使电话监听服务正常工作，请允许这项权限")
                .setPositiveButton("去设置", (dialog, which) -> {
                    openDrawOverlaySettings();
                    dialog.dismiss();
                })
                .setNegativeButton("稍后再说", (dialog, which) -> dialog.dismiss());

        alertDialog.show();
    }

    /**
     * 跳转悬浮窗管理设置界面
     */
    private void openDrawOverlaySettings() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M 以上引导用户去系统设置中打开允许悬浮窗
            // 使用反射是为了用尽可能少的代码保证在大部分机型上都可用
            try {
                Context context = this;
                Class clazz = Settings.class;
                Field field = clazz.getDeclaredField("ACTION_MANAGE_OVERLAY_PERMISSION");
                Intent intent = new Intent(field.get(null).toString());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "请在悬浮窗管理中打开权限", Toast.LENGTH_LONG).show();
            }
        } else {
            // 6.0 以下则直接使用 SettingsCompat 中提供的接口，只有国产手机上才有
            SettingsCompat.manageDrawOverlays(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        switchListenCall.setChecked(isServiceRunning(CallListenerService.class));
    }



    public boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) return false;

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }

        return false;
    }
}

package com.ajiew.phonecallapp.listenphonecall;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ajiew.phonecallapp.MainActivity;
import com.ajiew.phonecallapp.R;
import com.ajiew.phonecallapp.phonecallui.OnePiexActivity;
import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;
public class CallListenerService extends Service {

    private View phoneCallView;
    private TextView tvCallNumber;
    private WindowManager windowManager;
    private WindowManager.LayoutParams params;
    private TextView tv;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;

    private String callNumber = "";
    private boolean hasShown;
    private boolean isCallingIn;

    private View phoneView;

    private TextView b1;
    private TextView b2;
    private final static int GRAY_SERVICE_ID = 3;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(GRAY_SERVICE_ID, getNotification());
        }
        initPhoneCallView();
        initPhoneStateListener();


    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startForeground(GRAY_SERVICE_ID, getNotification());
        return START_STICKY_COMPATIBILITY;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 初始化来电状态监听器
     */
    private void initPhoneStateListener() {
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                super.onCallStateChanged(state, incomingNumber);
                super.onCallStateChanged(state, incomingNumber);

                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE: // 待机，即无电话时，挂断时触发
                        dismiss1();
                        dismiss2();
                        break;

                    case TelephonyManager.CALL_STATE_RINGING: // 响铃，来电时触发
                        isCallingIn = true;
                        来电(incomingNumber);

                        break;

                    case TelephonyManager.CALL_STATE_OFFHOOK: // 摘机，接听或拨出电话时触发
                        dismiss1();
                        去电(incomingNumber);
                        break;

                    default:
                        break;

                }
            }
        };

        // 设置来电监听器
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }

    }



    private void initPhoneCallView() {
        windowManager = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);

        params = new WindowManager.LayoutParams();
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.screenOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

        // 设置图片格式，效果为背景透明
        params.format = PixelFormat.TRANSLUCENT;
        // 设置 Window flag 为系统级弹框 | 覆盖表层
        params.type = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY :
                WindowManager.LayoutParams.TYPE_PHONE;

        // 不可聚集（不响应返回键）| 全屏
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        // API 19 以上则还可以开启透明状态栏与导航栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            params.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_FULLSCREEN
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        }

        FrameLayout interceptorLayout = new FrameLayout(this) {

            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

                        return true;
                    }
                }

                return super.dispatchKeyEvent(event);
            }
        };

        phoneCallView = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.view_phone_call, interceptorLayout);
        tvCallNumber = phoneCallView.findViewById(R.id.tv_call_number);
            tv = phoneCallView.findViewById(R.id.type);

        ImageView jt = phoneCallView.findViewById(R.id.jt);
        jt.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                endcall();
            }
        });


        ImageView gd = phoneCallView.findViewById(R.id.gd);
        gd.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                endcall();
            }
        });


        FrameLayout ceptorLayout = new FrameLayout(this) {

            @Override
            public boolean dispatchKeyEvent(KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {

                        return true;
                    }
                }

                return super.dispatchKeyEvent(event);
            }
        };

        phoneView   = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.view_phone, ceptorLayout);

        b1 = phoneView.findViewById(R.id.b1);
        b2 = phoneView.findViewById(R.id.b2);


        ImageView jtgd = phoneView.findViewById(R.id.jtgd);
        jtgd.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view) {
                endcall();
            }
        });
    }

    private Notification getNotification() {
        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //新增---------------------------------------------
            String CHANNEL_ONE_ID = "com.datecountdown.lock.service.cn";
            String CHANNEL_ONE_NAME = "电话服务";
            NotificationChannel notificationChannel = null;
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID, CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(false);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(false);
            notificationChannel.enableVibration(false);
            notificationChannel.setImportance(NotificationManager.IMPORTANCE_MIN);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);
            Intent intent = new Intent();
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            notification = new Notification.Builder(this).setChannelId(CHANNEL_ONE_ID).setTicker("Nature")
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentIntent(pendingIntent).getNotification();
            notification.flags |= Notification.FLAG_NO_CLEAR;
        } else {
            notification = new Notification();
        }
        return notification;
    }











/*
    public void endcall() {
        try {
            Object telephonyObject = getTelephonyObject(MainActivity.getContext());
            if (telephonyObject != null) {
                Method endCallMethod = telephonyObject.getClass().getMethod("endCall", new Class[0]);
                endCallMethod.setAccessible(true);
                endCallMethod.invoke(telephonyObject, new Object[0]);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
        } catch (IllegalArgumentException e3) {
            e3.printStackTrace();
        } catch (IllegalAccessException e4) {
            e4.printStackTrace();
        } catch (InvocationTargetException e5) {
            e5.printStackTrace();
        }
    }

    private Object getTelephonyObject(Context context) {
        Object telephonyObject = null;
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(TELEPHONY_SERVICE);
            Method getITelephony = telephonyManager.getClass().getDeclaredMethod("getITelephony", new Class[0]);
            getITelephony.setAccessible(true);
            telephonyObject = getITelephony.invoke(telephonyManager, new Object[0]);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e2) {
            e2.printStackTrace();
        } catch (IllegalArgumentException e3) {
            e3.printStackTrace();
        } catch (IllegalAccessException e4) {
            e4.printStackTrace();
        } catch (InvocationTargetException e5) {
            e5.printStackTrace();
        }
        return telephonyObject;
    }

*/



    public  void endcall() { //挂断电话

        if(true){
            OnePiexActivity.startOnePix(this);
            return;
        }
        Context cx = MainActivity.getContext();
        TelephonyManager telMag = (TelephonyManager) cx

                .getSystemService(Context.TELEPHONY_SERVICE);

        Class<TelephonyManager> c = TelephonyManager.class;

        Method mthEndCall = null;

        try {

            mthEndCall = c.getDeclaredMethod("getITelephony", (Class[]) null);

            mthEndCall.setAccessible(true);

            ITelephony iTel = (ITelephony) mthEndCall.invoke(telMag,

                    (Object[]) null);

            iTel.endCall();

        } catch (Exception e) {

            e.printStackTrace();

        }

    }




    /**
     * 显示顶级弹框展示通话信息
     */


    /**
     * 取消显示
     */
    private void dismiss1() {
        try {
            windowManager.removeView(phoneCallView);
            // windowManager.removeView(phoneView);
            isCallingIn = false;
            hasShown = false;
        } catch (Exception e) {

        }
    }

    private void dismiss2() {
        try {
            windowManager.removeView(phoneView);
            isCallingIn = false;
            hasShown = false;
        } catch (Exception e) {

        }
    }

    public void 来电(String incomingNumber) {
        int ui;
        tvCallNumber.setText(formatPhoneNumber(incomingNumber));
        ui =GetPhoneNoOperators(incomingNumber);
        if(ui == 0){
            tv.setText("");
        }

        if(ui == 1){
            tv.setText("中国移动");
        }

        if(ui == 2){
            tv.setText("中国电信");
        }

        if(ui == 3){
            tv.setText("中国联通");
        }

        if (!hasShown) {
            windowManager.addView(phoneCallView, params);
            hasShown = true;
        }
    }



    public void 去电(String incomingNumber) {
        int ui;
        b1.setText(formatPhoneNumber(incomingNumber));
        ui =GetPhoneNoOperators(incomingNumber);
        if(ui == 0){
            b2.setText("");
        }

        if(ui == 1){
            b2.setText("中国移动");
        }

        if(ui == 2){
            b2.setText("中国电信");
        }

        if(ui == 3){
            b2.setText("中国联通");
        }
        windowManager.addView(phoneView, params);
        hasShown = true;
    }


    public static String formatPhoneNumber(String phoneNum) {
        if (!TextUtils.isEmpty(phoneNum) && phoneNum.length() == 11) {
            return phoneNum.substring(0, 3) + "-"
                    + phoneNum.substring(3, 7) + "-"
                    + phoneNum.substring(7);
        }
        return phoneNum;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
    }

    public static int GetPhoneNoOperators(String phoneNo)
    {
        String subphone = phoneNo.substring(0, 3);
        //移动号码段134、135、136、137、138、139、150、151、152、157(TD)、158、159、187、188
        if (subphone.equals("134") || subphone.equals("135") || subphone.equals("136") ||
                subphone.equals("137") || subphone.equals("138") || subphone.equals("139") ||
                subphone.equals("150") || subphone.equals("151") || subphone.equals("152") ||
                subphone.equals("157") || subphone.equals("158") || subphone.equals("159") ||
                subphone.equals("187") || subphone.equals("188"))
            return 1;//移动号码

        else
            //电信号码段133、153、180、181、189、
            if (subphone.equals("133") || subphone.equals("153") || subphone.equals("180") ||
                    subphone.equals("181") || subphone.equals("189"))
                return 2;//电信号
            else
                //联通号码段：130、131、132、155、156、185、186
                if (subphone.equals("130") || subphone.equals("131") || subphone.equals("132") ||
                        subphone.equals("155") || subphone.equals("156") || subphone.equals("185") ||
                        subphone.equals("186") || subphone.equals("145"))
                    return 3;//联通号
                else
                    return 0;//没有与此相关的号码段
    }
}

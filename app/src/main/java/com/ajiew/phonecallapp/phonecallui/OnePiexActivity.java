package com.ajiew.phonecallapp.phonecallui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2017/7/10.
 */
public class OnePiexActivity extends Activity {


    private static final String TAG ="OnePiexActivity" ;

    public  static void startOnePix(Context context){
        Intent it = new Intent(context, OnePiexActivity.class);
        it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // it.putExtra("tele",outPhone);
        context.startActivity(it);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // Intent intent = getIntent();
       // String tele = intent.getStringExtra("tele");
//
//
        Log.e("OnePiexActivity", "onCreate: ");
        //设置1像素
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);
        //检查屏幕状态
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(3000);
                rejectCall();
                SystemClock.sleep(2000);
               // DataManager.getSingleton().doOutCall(OnePiexActivity.this);
                finish();
            }
        }).start();
    }
 
    @Override
    protected void onResume() {
        super.onResume();
    }

    public void rejectCall() {
        try {
            Method method = Class.forName("android.os.ServiceManager")
                    .getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null, new Object[]{Context.TELEPHONY_SERVICE});
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
            telephony.endCall();
        } catch (NoSuchMethodException e) {
            Log.d(TAG, "", e);
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "", e);
        } catch (Exception e) {
        }
    }

}

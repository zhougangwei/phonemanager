package com.ajiew.phonecallapp.listenphonecall;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.Method;

public class PhoneUtils {

    public static ITelephony getITelephony(TelephonyManager telephony) throws Exception {
        Method getITelephonyMethod = telephony.getClass().getDeclaredMethod("getITelephony");
        getITelephonyMethod.setAccessible(true);//私有化函数也能使用
        return (ITelephony)getITelephonyMethod.invoke(telephony);
    }
}
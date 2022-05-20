package com.example.ihelpou.classes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;


public class CallReceiver extends BroadcastReceiver {
    public static boolean comeFromOnReceive = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        comeFromOnReceive = true;
    }
}

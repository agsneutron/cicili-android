package com.cicili.mx.cicili;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cicili.mx.cicili.io.Utilities;


public class NotificationReceiver extends BroadcastReceiver {



    public interface OnNotificationReceiverListener{
        void onButtonClicked(Context context, Intent intent);
        void onNotificationActivity(Context context, Intent intent);
    }

    private final NotificationReceiver.OnNotificationReceiverListener onNotificationReceiverListener;

    public NotificationReceiver(OnNotificationReceiverListener onNotificationReceiverListener) {
        this.onNotificationReceiverListener = onNotificationReceiverListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (intent.getAction().contentEquals(MenuActivity.BUTTON_ACTION)){
            onNotificationReceiverListener.onButtonClicked(context, intent);
        }else{
            onNotificationReceiverListener.onNotificationActivity(context, intent);
        }


    }
}
